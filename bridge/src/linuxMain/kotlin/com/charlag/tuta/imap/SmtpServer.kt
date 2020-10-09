package com.charlag.tuta.imap


// Fot now just a dummy server which reads data
class SmtpServer {
    fun newConnection(): List<String> {
        return listOf("220 smtp.tutanota.com ESMTP Tutabridge")
    }

    var reading: MutableList<String>? = null

    fun respondTo(message: String): String? {
        return when {
            message.startsWith("HELO") -> {
                val otherParty = message.split(" ")[1]
                "HELO $otherParty, I am glad to see you"
            }
            message.startsWith("DATA") -> {
                reading = mutableListOf()
                "354 <CR><LF>.<CR><LF>"
            }
            reading != null -> {
                reading!! += message
                // TODO: we need to handle the case of the user-inputted line with dot
                // which should look like just two dots.
                if (message.contains(".\r\n")) {
                    println("Read:")
                    println(reading!!.joinToString("\n"))
                    reading = null
                    "250 Ok"
                } else {
                    null
                }
            }
            message.startsWith("QUIT") -> {
                "221 Bye"
            }
            else -> "250 Ok"
        }
    }
}