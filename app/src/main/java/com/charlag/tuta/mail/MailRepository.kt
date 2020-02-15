package com.charlag.tuta.mail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.DataSource
import com.charlag.tuta.GroupType
import com.charlag.tuta.MailFacade
import com.charlag.tuta.UserController
import com.charlag.tuta.compose.LocalDraftEntity
import com.charlag.tuta.data.*
import com.charlag.tuta.di.UserBound
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.network.API
import com.charlag.tuta.util.AsyncProvider
import com.charlag.tuta.util.lazyAsync
import io.ktor.client.features.ResponseException
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MailRepository(
    @UserBound private val api: API,
    private val db: AppDatabase,
    private val userController: UserController,
    private val mailFacade: MailFacade
) {

    val getEnabledMailAddresses: AsyncProvider<List<String>> = lazyAsync {
        val user = userController.waitForLogin()
        val mailGroupMembership =
            user.memberships.first { it.groupType == GroupType.Mail.value }
        val mailGroup = api.loadElementEntity<Group>(mailGroupMembership.group)
        mailFacade.getEnabledMailAddresses(
            user,
            userController.getUserGroupInfo(),
            mailGroup
        )
    }

    // Should this be on the mailFacade?
    suspend fun moveMails(ids: List<IdTuple>, targetFolder: IdTuple) {
        val moveMailData = MoveMailData(
            _format = 0,
            mails = ids,
            targetFolder = targetFolder
        )
        api.serviceRequestVoid(
            "tutanota",
            "movemailservice",
            HttpMethod.Post,
            moveMailData,
            null,
            null
        )
    }

    suspend fun getMail(id: IdTuple): MailEntity? {
        return withContext(Dispatchers.Default) {
            db.mailDao().getMail(id.elementId.asString())
                ?: api.loadListElementEntity<Mail>(id).let {
                    val entity = it.toEntity()
                    db.mailDao().insertMail(entity)
                    entity
                }
        }
    }

    fun observeMails(listId: Id): DataSource.Factory<Int, MailEntity> {
        return db.mailDao().getMailsLiveData(listId.asString())
    }

    /**
     * Loads additional mails (from newest into oldest) older than [startId].
     * @return true if list loaded completely
     */
    suspend fun addMailsFrom(listId: Id, startId: Id): Boolean {
        val loaded = loadMailsFromNetwork(listId, startId)
        db.mailDao().insertMails(loaded)
        return loaded.isEmpty()
    }

    suspend fun getMailBody(id: Id): MailBodyEntity {
        return db.mailDao().getMailBody(id.asString()) ?: loadMailBodyFromTheServer(id)
    }

    suspend fun getLocalDraft(id: Long): LocalDraftEntity? {
        return db.mailDao().getLocalDraft(id)
    }

    suspend fun saveLocalDraft(draft: LocalDraftEntity): Long {
        return db.mailDao().insertLocalDraft(draft)
    }

    suspend fun deleteLocalDraft(draft: LocalDraftEntity) {
        db.mailDao().deleteLocalDraft(draft)
    }

    private suspend fun loadMailBodyFromTheServer(id: Id): MailBodyEntity {
        val mailBody = api.loadElementEntity<MailBody>(id)
        val entity = MailBodyEntity(id.asString(), mailBody.compressedText ?: mailBody.text!!)
        db.mailDao().insertMailBody(entity)
        return entity
    }

    suspend fun deleteMails(folderId: IdTuple, mails: List<IdTuple>) {
        api.serviceRequestVoid(
            "tutanota", "mailservice", HttpMethod.Delete, DeleteMailData(
                _format = 0,
                folder = folderId,
                mails = mails
            )
        )
    }

    fun observeFolders(): LiveData<List<MailFolderWithCounter>> = db.mailDao().getFoldersLiveData()
        .map { folders ->
            if (folders.isEmpty()) {
                // TODO: switch to userScope or similar
                GlobalScope.launch {
                    try {
                        db.mailDao().insertFolders(loadFolders())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to load/insert folders", e)
                    }
                }
            }
            folders
        }

    fun search(query: String): DataSource.Factory<Int, MailEntity> =
        db.mailDao().search(query)

    suspend fun loadAttachment(id: IdTuple): File = api.loadListElementEntity(id)

    suspend fun markReadUnrad(id: IdTuple, unread: Boolean) {
        val mail = (getMail(id) ?: return)
            .copy(unread = unread)
            .toMail()
        try {
            api.updateEntity(mail)
        } catch (e: ResponseException) {
            Log.e("Mail", "Failed to mark as unread", e)
        }
    }

    private suspend fun loadFolders(): List<MailFolderEntity> {
        return withContext(Dispatchers.IO) {
            val user = userController.waitForLogin()
            val mailMembership = user.memberships
                .find { it.groupType == GroupType.Mail.value }!!
            val groupRoot = api
                .loadElementEntity<MailboxGroupRoot>(mailMembership.group)
            val mailbox = api.loadElementEntity<MailBox>(groupRoot.mailbox)
            api.loadAll(MailFolder::class, mailbox.systemFolders!!.folders)
                .flatMap { folder ->
                    // Theoretically subfolders are only allowed for one of the system folders
                    // but practically they still could be anywhere
                    api.loadAll(
                        MailFolder::class,
                        folder.subFolders
                    ) + folder
                }
                .map {
                    it.toEntity()
                }
        }
    }

    private suspend fun loadMailsFromNetwork(listId: Id, startId: Id): List<MailEntity> {
        userController.waitForLogin()
        return withContext(Dispatchers.Default) {
            api.loadRange(
                Mail::class,
                listId,
                startId,
                MAIL_LOAD_PAGE,
                true
            ).map { it.toEntity() }
        }
    }

    companion object {
        private const val MAIL_LOAD_PAGE = 40
        private const val TAG = "MailRepo"
    }

}