package com.charlag.tuta.imap

import com.charlag.tuta.entities.tutanota.Mail

data class MailWithUid(val uid: Int, val mail: Mail)