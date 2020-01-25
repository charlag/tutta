package com.charlag.tuta.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*

@Dao
interface MailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMail(mailEntity: MailEntity)

    @Insert
    suspend fun insertMails(mailEntity: List<MailEntity>)

    @Query("SELECT * FROM MailEntity WHERE id = :id LIMIT 1")
    suspend fun getMail(id: String)

    @Query("SELECT * FROM MailEntity WHERE listId = :listId ORDER BY id DESC LIMIT 40 ")
    suspend fun getMailsFromListId(listId: String): List<MailEntity>

    @Query("SELECT * FROM MailEntity WHERE listId = :listId ORDER BY id DESC")
    fun getMailsLiveData(listId: String): DataSource.Factory<Int, MailEntity>

    @Query("DELETE FROM MailEntity WHERE id = :id")
    suspend fun deleteMail(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: MailFolderEntity)

    @Insert
    suspend fun insertFolders(folders: List<MailFolderEntity>)

    @Query("SELECT * FROM MailFolderEntity")
    fun getFoldersLiveData(): LiveData<List<MailFolderEntity>>

    @Query("DELETE FROM MailFolderEntity WHERE id = :id")
    suspend fun deleteFolder(id: String)
}