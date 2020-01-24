package com.charlag.tuta.mail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.charlag.tuta.DependencyDump
import com.charlag.tuta.GroupType
import com.charlag.tuta.MailFolderType
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.data.MailAddressEntity
import com.charlag.tuta.data.MailEntity
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.sortSystemFolders
import com.charlag.tuta.util.combineLiveData
import kotlinx.coroutines.*
import java.util.*

class MailViewModel(app: Application) : AndroidViewModel(app) {
    private val loginFacade = DependencyDump.loginFacade
    private val api = DependencyDump.api
    private val mailDao by lazy { DependencyDump.db.mailDao() }

    val selectedFolderId = MutableLiveData<IdTuple>()
    val folders = MutableLiveData<List<MailFolder>>()

    val selectedFolder = combineLiveData(selectedFolderId, folders) { folderId, folders ->
        folders.find { it._id == folderId }
    }

    fun selectFolder(folderId: IdTuple) {
        selectedFolderId.value = folderId
    }

    private fun MailAddress.toEntity() = MailAddressEntity(name, address)
    private fun Mail.toEntity() = MailEntity(
        id = _id.elementId,
        listId = _id.listId,
        confidential = confidential,
        differentEnvelopeSender = differentEnvelopeSender,
        receivedDate = Date(receivedDate.millis),
        sendDate = Date(sentDate.millis),
        subject = subject,
        unread = unread,
        sender = sender.toEntity(),
        toReciipients = toRecipients.map { it.toEntity() },
        ccReciipients = ccRecipients.map { it.toEntity() },
        bccReciipients = bccRecipients.map { it.toEntity() },
        body = body,
        conversationEntry = conversationEntry

    )

    suspend fun loadMails(folder: MailFolder): List<MailEntity> {
        val dbMails = mailDao.getMailsFromListId(folder.mails.asString())
        if (dbMails.isEmpty()) {
            val networkMails = api.loadRange(Mail::class, folder.mails, GENERATED_MAX_ID, 40, true)
                .map { mail -> mail.toEntity() }
            viewModelScope.launch {
                mailDao.insertMails(networkMails)
            }
            return networkMails
        }
        return dbMails
    }

    val openedMail = MutableLiveData<MailEntity?>()

    fun setOpenedMail(mail: MailEntity?) {
        openedMail.value = mail
    }

    val loadedMailBody = MutableLiveData<MailBody?>()

    suspend fun loadMailBody(mailBodyId: Id): MailBody {
        val loaded = loadedMailBody.value
        if (loaded != null && loaded._id == mailBodyId) {
            return loaded
        }
        loadedMailBody.value = null
        val freshlyLoaded = withContext(Dispatchers.IO) {
            api.loadElementEntity<MailBody>(mailBodyId)
        }
        loadedMailBody.value = freshlyLoaded
        return freshlyLoaded
    }


    init {
        DependencyDump.db = Room.databaseBuilder(
            getApplication(), AppDatabase::class.java,
            "tuta-db"
        ).build()

        viewModelScope.launch {
            val folders = withContext(Dispatchers.IO) {
                loginFacade.loggedIn.await()
                val mailMembership = DependencyDump.loginFacade.user!!.memberships
                    .find { it.groupType == GroupType.Mail.value }!!
                val api = DependencyDump.api
                val groupRoot = api
                    .loadElementEntity<MailboxGroupRoot>(mailMembership.group)
                val mailbox = api.loadElementEntity<MailBox>(groupRoot.mailbox)
                api.loadAll(MailFolder::class, mailbox.systemFolders!!.folders)
                    .let(::sortSystemFolders)
            }

            withContext(Dispatchers.Main) {
                this@MailViewModel.folders.value = folders
                val selectedFolderId =
                    folders.find { it.folderType == MailFolderType.INBOX.value }!!._id
                selectFolder(selectedFolderId)
            }
        }
    }
}