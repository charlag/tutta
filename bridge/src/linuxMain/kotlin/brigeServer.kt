import com.charlag.tuta.zeroOut
import com.charlag.tuta.imap.ImapServer
import com.charlag.tuta.posix.*
import kotlinx.cinterop.*
import platform.posix.*

const val PORT = 2143

fun runBridgeServer(server: ImapServer) {
    // Ignore wild SIGPIPEs, we get return code anyway
    signal(SIGPIPE, SIG_IGN);
    init_sockets()

    memScoped {
        val buffer = ByteArray(1024)

        val listenFd = socket(AF_INET, SOCK_STREAM, 0)
            .check({ it != -1 }) { error("socket failed: ${errorMessage()}")}

        val serverAddr = alloc<sockaddr_in> {
            zeroOut()
            sin_family = AF_INET.convert()
            sin_port = posix_htons(PORT.toShort()).convert()
        }

        bind(listenFd, serverAddr.ptr.reinterpret(), sockaddr_in.size.convert())
            .check(::isZero) { error("bind failed: ${errorMessage()}")}

        println("bound $PORT")

        listen(listenFd, 10)
            .check(::isZero) { error("listen failed: ${errorMessage()}")}

        println("listen $PORT")

        while (true) {
            val commFd = accept(listenFd, null, null)
                .check({ it != -1 }) { error("read failed: ${errorMessage()}")}

            println("accepted $commFd")

            buffer.usePinned { pinned ->
                sendImapResponse(commFd, server.newConnection())

                while (true) {
                    val received = readString(pinned, commFd) ?: break
                    print("C: $received")
                    try {
                        val response = server.respondTo(received)
                        sendImapResponse(commFd, response)
                    } catch (e: Throwable) {
                        println("Request failed with $e")
                        sendImapResponse(commFd, listOf("BAD ERROR"))
                    }
                }
            }
            println("closed $commFd")
        }
    }
}

private fun sendImapResponse(commFd: Int, response: List<String>) {
    for (s in response) {
        println("S: $s")
        sendString(commFd, s + "\r\n")
    }
}

private fun sendString(fd: Int, value: String) {
    val messageBuffer = value.encodeToByteArray()
    send(fd, messageBuffer.refTo(0), messageBuffer.size.convert(), 0)
        .check(::isNonNegative) { error("Could not write to socket ${errorMessage()}") }
}

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