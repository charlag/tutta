package com.charlag.tuta.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MailDao {
    @Insert
    suspend fun insertMails(mailEntity: List<MailEntity>)

    @Query("SELECT * from MailEntity ORDER BY id DESC LIMIT 40 ")
    suspend fun getMails(): List<MailEntity>

    @Query("SELECT * from MailEntity where listId = :listId ORDER BY id DESC LIMIT 40 ")
    suspend fun getMailsFromListId(listId: String): List<MailEntity>
}