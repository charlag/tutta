package com.charlag.tuta.mail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import androidx.room.Room
import com.charlag.tuta.DependencyDump
import com.charlag.tuta.GroupType
import com.charlag.tuta.MailFolderType
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.data.MailAddressEntity
import com.charlag.tuta.data.MailEntity
import com.charlag.tuta.data.MailFolderEntity
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.sortSystemFolders
import com.charlag.tuta.util.combineLiveData
import com.charlag.tuta.util.map
import kotlinx.coroutines.*
import java.util.*

class MailViewModel(app: Application) : AndroidViewModel(app) {
    private val loginFacade = DependencyDump.loginFacade
    private val api = DependencyDump.api
    private val mailDao by lazy { DependencyDump.db.mailDao() }

    val selectedFolderId = MutableLiveData<IdTuple>()
    val folders: LiveData<List<MailFolderEntity>>
    val selectedFolder: LiveData<MailFolderEntity?>

    init {
        DependencyDump.db = Room.databaseBuilder(
            getApplication(), AppDatabase::class.java,
            "tuta-db"
        ).build()

        folders = mailDao.getFoldersLiveData().map { dbFolders ->
            if (dbFolders.isEmpty()) {
                viewModelScope.launch {
                    val folders = loadFolders()
                    mailDao.insertFolders(folders)
                }
            } else if (selectedFolderId.value == null) {
                val selectedFolder =
                    dbFolders.find { it.folderType == MailFolderType.INBOX.value }!!
                selectFolder(
                    IdTuple(
                        GeneratedId(selectedFolder.listId),
                        GeneratedId(selectedFolder.id)
                    )
                )
            }
            dbFolders
        }
        selectedFolder = combineLiveData(selectedFolderId, folders) { folderId, folders ->
            folders.find { it.id == folderId.elementId.asString() }
        }
    }

    fun selectFolder(folderId: IdTuple) {
        selectedFolderId.value = folderId
    }

    fun loadMails(folder: MailFolderEntity): LiveData<PagedList<MailEntity>> {
        return mailDao.getMailsLiveData(folder.mails.asString())
            .toLiveData(
                pageSize = 40,
                boundaryCallback = object : PagedList.BoundaryCallback<MailEntity>() {
                    override fun onZeroItemsLoaded() {
                        Log.d("MailViewModel", "onZeroItems")
                        viewModelScope.launch {
                            val mails =
                                api.loadRange(Mail::class, folder.mails, GENERATED_MAX_ID, 40, true)
                                    .map { it.toEntity() }
                            Log.d("MailViewModel", "onZeroItems fetched")
                            mailDao.insertMails(mails)
                        }
                    }

                    override fun onItemAtEndLoaded(itemAtEnd: MailEntity) {
                        Log.d("MailViewModel", "onItemAtEndLoaded")
                        viewModelScope.launch {
                            val mails =
                                api.loadRange(Mail::class, folder.mails, itemAtEnd.id, 40, true)
                                    .map { it.toEntity() }
                            Log.d("MailViewModel", "onItemAtEndLoaded fetched")
                            mailDao.insertMails(mails)
                        }
                    }
                })
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

    private suspend fun loadFolders(): List<MailFolderEntity> {
        return withContext(Dispatchers.IO) {
            loginFacade.waitForLogin()
            val mailMembership = DependencyDump.loginFacade.user!!.memberships
                .find { it.groupType == GroupType.Mail.value }!!
            val api = DependencyDump.api
            val groupRoot = api
                .loadElementEntity<MailboxGroupRoot>(mailMembership.group)
            val mailbox = api.loadElementEntity<MailBox>(groupRoot.mailbox)
            api.loadAll(MailFolder::class, mailbox.systemFolders!!.folders)
                .map { it.toEntity() }
                .let(::sortSystemFolders)
        }
    }
}

fun MailAddress.toEntity() = MailAddressEntity(name, address)
fun Mail.toEntity() = MailEntity(
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

fun MailFolder.toEntity() = MailFolderEntity(
    id = _id.elementId.asString(),
    listId = _id.listId.asString(),
    folderType = folderType,
    name = name,
    mails = mails
)