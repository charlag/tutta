package com.charlag.tuta.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.charlag.tuta.compose.LocalDraftEntity

@Dao
interface MailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMail(mailEntity: MailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMails(mailEntity: List<MailEntity>)

    @Query("SELECT * FROM MailEntity WHERE id = :id LIMIT 1")
    suspend fun getMail(id: String): MailEntity?

    @Query("SELECT * FROM MailEntity WHERE id IN (:ids)")
    suspend fun getMails(ids: List<String>): MailEntity

    @Query("SELECT * FROM MailEntity WHERE listId = :listId ORDER BY id DESC LIMIT 40 ")
    suspend fun getMailsFromListId(listId: String): List<MailEntity>

    @Query("SELECT * FROM MailEntity WHERE listId = :listId ORDER BY id DESC")
    fun getMailsLiveData(listId: String): DataSource.Factory<Int, MailEntity>

    @Query("DELETE FROM MailEntity WHERE id = :id")
    suspend fun deleteMail(id: String)

    @Query("DELETE FROM MailFolderEntity")
    suspend fun deleteAllMailFolders()

    @Query("DELETE FROM MailEntity")
    suspend fun deleteAllMails()

    @Query("DELETE FROM MailBodyEntity")
    suspend fun deleteAllMailBodies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: MailFolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolders(folders: List<MailFolderEntity>)

    @Query("SELECT * FROM MailFolderEntity LEFT OUTER JOIN MailFolderCounterEntity ON MailFolderEntity.mails = MailFolderCounterEntity.mailListId")
    fun getFoldersLiveData(): LiveData<List<MailFolderWithCounter>>

    @Query("DELETE FROM MailFolderEntity WHERE id = :id")
    suspend fun deleteFolder(id: String)

    @Insert
    suspend fun insertMailBody(mailBody: MailBodyEntity)

    @Query("SELECT * FROM MailBodyEntity WHERE id = :id LIMIT 1")
    suspend fun getMailBody(id: String): MailBodyEntity?

    @Query("SELECT * FROM MailEntity WHERE subject LIKE '%' || :query || '%' OR sender LIKE '%' || :query || '%' OR toRecipients LIKE '%' || :query || '%'")
    fun search(query: String): DataSource.Factory<Int, MailEntity>

    @Query("SELECT * FROM LocalDraftEntity WHERE id = :id LIMIT 1")
    suspend fun getLocalDraft(id: Long): LocalDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalDraft(localDraft: LocalDraftEntity): Long

    @Delete
    suspend fun deleteLocalDraft(localDraft: LocalDraftEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolderCounter(folderCounterEntity: MailFolderCounterEntity)
}