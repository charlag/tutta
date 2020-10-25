import com.charlag.tuta.imap.ImapServer
import com.charlag.tuta.imap.SmtpServer
import com.charlag.tuta.posix.*
import com.charlag.tuta.zeroOut
import kotlinx.cinterop.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.posix.*
import kotlin.math.min
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze
import kotlin.time.nanoseconds

const val IMAP_PORT = 2143
const val SMTP_PORT = 2125

fun runBridgeServer(imapServerFactory: () -> ImapServer, smtpServerFactory: () -> SmtpServer) {
    // Ignore wild SIGPIPEs, we get return code anyway
    signal(SIGPIPE, SIG_IGN)
    init_sockets()

    runSmtpServer(smtpServerFactory) // Start listening on a different thread
    runImapServer(imapServerFactory) // Start listening on this thread and dispatch each connection to a new one
}

private fun imapLog(value: String) {
//    println(value)
}

private fun smtpLog(value: String) {
    println(value)
}

private fun runImapServer(imapServerFactory: () -> ImapServer) {
    // Spins up a new worker with acceptor loop, then spins up a new worker per connection
    Worker.start().execute(TransferMode.SAFE, { imapServerFactory.freeze() }) { serverFactory ->
        memScoped {
            val listenFd = socket(AF_INET, SOCK_STREAM, 0)
                .check({ it != -1 }) { error("socket failed: ${errorMessage()}") }
            // We set it to reuse port for now
            val enabled = alloc<IntVar>()
            enabled.value = 1
            setsockopt(listenFd, SOL_SOCKET, SO_REUSEPORT, enabled.ptr, sizeOf<IntVar>().convert())

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

                println("${timeString()} accepted I$commFd")
                runImapConnectionWorker(commFd, serverFactory)
            }
        }
    }
}

private fun runImapConnectionWorker(commFd: Int, imapServerFactory: () -> ImapServer) {
    // We don't anticipate concurrent SMTP connections so it just spins up one worker which
    // both accepts and handles connections.
    Worker.start().execute(
        TransferMode.SAFE,
        { (commFd to imapServerFactory.freeze()) }) { (commFd, imapSeverF) ->
        val imapSever = imapSeverF()
        val buffer = ByteArray(1024)
        val tag = "I$commFd"
        buffer.usePinned { pinned ->
            try {
                sendImapResponse(tag, commFd, imapSever.newConnection())
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
                imapLog("${timeString()} $tag C: $received")
                try {
                    val response = imapSever.respondTo(received)
                    sendImapResponse(tag, commFd, response)
                } catch (e: IOException) {
                    println("Could not write response: $e")
                } catch (e: Throwable) {
                    println("Request failed with ${e.printStackTrace()}")
                    sendImapResponse(tag, commFd, listOf("BAD ERROR"))
                }
            }
        }
        println("closed I$commFd")
    }
}

private fun timeString(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return "${now.hour.toString().padStart(2, '0')}:${
        now.minute.toString().padStart(2, '0')
    }:${(now.nanosecond.nanoseconds.toLongMilliseconds()).toString().padStart(2, '0')}"
}

private fun runSmtpServer(smtpServerFactory: () -> SmtpServer) {
    Worker.start().execute(TransferMode.SAFE, { smtpServerFactory.freeze() }) { serverFactory ->
        memScoped {
            val buffer = ByteArray(1024)

            val listenFd = socket(AF_INET, SOCK_STREAM, 0)
                .check({ it != -1 }) { error("socket failed: ${errorMessage()}") }

            val enabled = alloc<IntVar>()
            enabled.value = 1
            setsockopt(listenFd, SOL_SOCKET, SO_REUSEPORT, enabled.ptr, sizeOf<IntVar>().convert())

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

            while (true) {
                val commFd = accept(listenFd, null, null)
                    .check({ it != -1 }) { error("read failed: ${errorMessage()}") }

                println("${timeString()} accepted S$commFd")
                val tag = "P$commFd"

                val server = serverFactory() // new server for new connection

                buffer.usePinned { pinned ->
                    try {
                        sendSmtpResponse(tag, commFd, server.newConnection())
                    } catch (e: IOException) {
                        println("Could not send initial response $e")
                    }

                    for (received in readStrings(pinned, commFd)) {
                        smtpLog("${timeString()} $tag C: $received")
                        try {
                            server.respondTo(received)?.let { response ->
                                sendSmtpResponse(tag, commFd, listOf(response))
                            }
                        } catch (e: IOException) {
                            println("Could not write response: $e")
                        } catch (e: Throwable) {
                            println("Request failed with ${e.printStackTrace()}")
                            sendSmtpResponse(tag, commFd, listOf("500 ERROR"))
                        }
                    }
                }
                println("closed $tag")
            }
        }
    }
}

private fun sendImapResponse(tag: String, commFd: Int, response: List<String>) {
    for (s in response) {
        imapLog("${timeString()} $tag S: ${s.substring(0, min(s.length, 200))}")
        sendString(commFd, s + "\r\n")
    }
}

private fun sendSmtpResponse(tag: String, commFd: Int, response: List<String>) {
    for (s in response) {
        smtpLog("${timeString()} $tag S: ${s.substring(0, min(s.length, 200))}")
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

/**
 * Read a sequence of CRLF (\r\n) separated lines from [fd] using [buffer].
 */
private fun readStrings(buffer: Pinned<ByteArray>, fd: Int): Sequence<String> {
    var previousString: String? = null // part of the command we didn't finish reading
    return generateSequence {
        val length = try {
            recv(fd, buffer.addressOf(0), buffer.get().size.convert(), 0).toInt()
                .check(::isNonNegative) { error("Could not read from socket ${errorMessage()}") }
        } catch (e: Throwable) {
            println(e)
            return@generateSequence null
        }
        if (length == 0) {
            return@generateSequence null
        }

        val full = (previousString ?: "") + buffer.get().decodeToString(0, length)
        previousString = null
        val parts = full.split("\r\n")
        if (parts.last() != "") { // there's a part of line we didn't read
            previousString = parts.last()
        }
        parts.dropLast(1) // it's either a part of the string or empty string
    }.flatten()
}