import com.charlag.tuta.zeroOut
import com.charlag.tuta.imap.ImapServer
import com.charlag.tuta.imap.SmtpServer
import com.charlag.tuta.posix.*
import kotlinx.cinterop.*
import platform.posix.*
import kotlin.math.min
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

const val IMAP_PORT = 2143
const val SMTP_PORT = 2125

fun runBridgeServer(imapServerFactory: () -> ImapServer) {
    // Ignore wild SIGPIPEs, we get return code anyway
    signal(SIGPIPE, SIG_IGN)
    init_sockets()

    runSmtpServer() // Start listening on a different thread
    runImapServer(imapServerFactory) // Start listening on this thread and dispatch each connection to a new one
}

private fun runImapServer(imapServerFactory: () -> ImapServer) {
    memScoped {
        val listenFd = socket(AF_INET, SOCK_STREAM, 0)
            .check({ it != -1 }) { error("socket failed: ${errorMessage()}") }

        val serverAddr = alloc<sockaddr_in> {
            zeroOut()
            sin_family = AF_INET.convert()
            sin_port = posix_htons(IMAP_PORT.toShort()).convert()
        }

        bind(listenFd, serverAddr.ptr.reinterpret(), sockaddr_in.size.convert())
            .check(::isZero) { error("bind failed: ${errorMessage()}") }

        println("bound $IMAP_PORT")

        listen(listenFd, 10)
            .check(::isZero) { error("listen failed: ${errorMessage()}") }

        println("listen $IMAP_PORT")

        while (true) {
            val commFd = accept(listenFd, null, null)
                .check({ it != -1 }) { error("read failed: ${errorMessage()}") }

            println("accepted $commFd")
            runImapConnectionWorker(commFd, imapServerFactory)
        }
    }
}

private fun runImapConnectionWorker(commFd: Int, imapServerFactory: () -> ImapServer) {
    Worker.start()
        .execute(
            TransferMode.SAFE,
            { (commFd to imapServerFactory.freeze()) }) { (commFd, imapSeverF) ->
            val imapSever = imapSeverF()
            val buffer = ByteArray(1024)
            buffer.usePinned { pinned ->
                try {
                    sendImapResponse(commFd, imapSever.newConnection())
                } catch (e: IOException) {
                    println("Could not send initial response $e")
                }

                while (true) {
                    val received = try {
                        readString(pinned, commFd) ?: break
                    } catch (e: IOException) {
                        println("Could not read from the input $e")
                        break
                    }
                    print("C: $received")
                    try {
                        val response = imapSever.respondTo(received)
                        sendImapResponse(commFd, response)
                    } catch (e: IOException) {
                        println("Could not write response: $e")
                    } catch (e: Throwable) {
                        println("Request failed with ${e.printStackTrace()}")
                        sendImapResponse(commFd, listOf("BAD ERROR"))
                    }
                }
            }
            println("closed $commFd")
        }
}

private fun runSmtpServer() {
    Worker.start().execute(TransferMode.SAFE, {}) {
        memScoped {
            val buffer = ByteArray(1024)

            val listenFd = socket(AF_INET, SOCK_STREAM, 0)
                .check({ it != -1 }) { error("socket failed: ${errorMessage()}") }

            val serverAddr = alloc<sockaddr_in> {
                zeroOut()
                sin_family = AF_INET.convert()
                sin_port = posix_htons(SMTP_PORT.toShort()).convert()
            }

            bind(listenFd, serverAddr.ptr.reinterpret(), sockaddr_in.size.convert())
                .check(::isZero) { error("bind smtp failed: ${errorMessage()}") }

            println("SMTP: bound $SMTP_PORT")

            listen(listenFd, 10)
                .check(::isZero) { error("listen failed: ${errorMessage()}") }

            println("SMTP: listen $SMTP_PORT")

            val server = SmtpServer()

            while (true) {
                val commFd = accept(listenFd, null, null)
                    .check({ it != -1 }) { error("read failed: ${errorMessage()}") }

                println("accepted $commFd")

                buffer.usePinned { pinned ->
                    try {
                        sendImapResponse(commFd, server.newConnection())
                    } catch (e: IOException) {
                        println("Could not send initial response $e")
                    }

                    while (true) {
                        val received = try {
                            readString(pinned, commFd) ?: break
                        } catch (e: IOException) {
                            println("Could not read from the input $e")
                            break
                        }
                        print("C: $received")
                        try {
                            server.respondTo(received)?.let { response ->
                                sendImapResponse(commFd, listOf(response))
                            }
                        } catch (e: IOException) {
                            println("Could not write response: $e")
                        } catch (e: Throwable) {
                            println("Request failed with $e")
                            sendImapResponse(commFd, listOf("500 ERROR"))
                        }
                    }
                }
                println("closed $commFd")
                break
            }
        }
    }
}

private fun sendImapResponse(commFd: Int, response: List<String>) {
    for (s in response) {
        println("S: ${s.substring(0, min(s.length, 200))}")
        sendString(commFd, s + "\r\n")
    }
}

private fun sendString(fd: Int, value: String) {
    val messageBuffer = value.encodeToByteArray()
    val retVal = send(fd, messageBuffer.refTo(0), messageBuffer.size.convert(), MSG_NOSIGNAL)
    when {
        retVal == EPIPE.toLong() ->
            throw IOException("Could not write to the stream, other end broke the connectiion")
        retVal < 0 -> error("Could not write to socket $retVal ${errorMessage()}")
    }
}

private class IOException(message: String) : Exception(message)

private fun readString(buffer: Pinned<ByteArray>, fd: Int): String? {
    val length = try {
        recv(fd, buffer.addressOf(0), buffer.get().size.convert(), 0).toInt()
            .check(::isNonNegative) { error("Could not read from socket ${errorMessage()}") }
    } catch (e: Throwable) {
        println(e)
        return null
    }
    if (length == 0) {
        return null
    }
    return buffer.get().decodeToString(0, length)
}