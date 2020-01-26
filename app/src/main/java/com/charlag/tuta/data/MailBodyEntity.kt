package com.charlag.tuta.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MailBodyEntity(
    @PrimaryKey
    val id: String,
    val text: String
)