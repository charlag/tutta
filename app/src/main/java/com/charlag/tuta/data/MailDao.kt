package com.charlag.tuta.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*

@Dao
interface MailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMail(mailEntity: MailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMails(mailEntity: List<MailEntity>)

    @Query("SELECT * FROM MailEntity WHERE id = :id LIMIT 1")
    suspend fun getMail(id: String): MailEntity

    @Query("SELECT * FROM MailEntity WHERE id IN (:ids)")
    suspend fun getMails(ids: List<String>): MailEntity

    @Query("SELECT * FROM MailEntity WHERE listId = :listId ORDER BY id DESC LIMIT 40 ")
    suspend fun getMailsFromListId(listId: String): List<MailEntity>

    @Query("SELECT * FROM MailEntity WHERE listId = :listId ORDER BY id DESC")
    fun getMailsLiveData(listId: String): DataSource.Factory<Int, MailEntity>

    @Query("DELETE FROM MailEntity WHERE id = :id")
    suspend fun deleteMail(id: String)

    @Query("DELETE FROM MailEntity")
    suspend fun deleteAllMails()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: MailFolderEntity)

    @Insert
    suspend fun insertFolders(folders: List<MailFolderEntity>)

    @Query("SELECT * FROM MailFolderEntity")
    fun getFoldersLiveData(): LiveData<List<MailFolderEntity>>

    @Query("DELETE FROM MailFolderEntity WHERE id = :id")
    suspend fun deleteFolder(id: String)

    @Insert
    suspend fun insertMailBody(mailBody: MailBodyEntity)

    @Query("SELECT * FROM MailBodyEntity WHERE id = :id LIMIT 1")
    suspend fun getMailBody(id: String): MailBodyEntity?

    @Query("SELECT * FROM MailEntity WHERE subject LIKE '%' || :query || '%' OR sender LIKE '%' || :query || '%' OR toRecipients LIKE '%' || :query || '%'")
    fun search(query: String): DataSource.Factory<Int, MailEntity>
}