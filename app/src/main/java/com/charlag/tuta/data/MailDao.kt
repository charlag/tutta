package com.charlag.tuta.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MailDao {
    @Insert
    suspend fun insertMails(mailEntity: List<MailEntity>)

    @Query("SELECT * from MailEntity ORDER BY id DESC LIMIT 40")
    suspend fun getMails(): List<MailEntity>

    @Query("SELECT * from MailEntity where listId = :listId ORDER BY id DESC LIMIT 40 ")
    suspend fun getMailsFromListId(listId: String): List<MailEntity>

    @Query("SELECT * from MailEntity where listId = :listId ORDER BY id DESC")
    fun getMailsLiveData(listId: String): DataSource.Factory<Int, MailEntity>

    @Insert
    suspend fun insertFolders(folders: List<MailFolderEntity>)

    @Query("SELECT * from MailFolderEntity")
    fun getFoldersLiveData(): LiveData<List<MailFolderEntity>>
}