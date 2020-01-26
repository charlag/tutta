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
import com.charlag.tuta.*
import com.charlag.tuta.data.*
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.util.combineLiveData
import com.charlag.tuta.util.map
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MailViewModel(app: Application) : AndroidViewModel(app) {
    private val loginFacade = DependencyDump.loginFacade
    private val api = DependencyDump.api
    private val mailDao by lazy { DependencyDump.db.mailDao() }
    private val mailRepository by lazy { MailRepository(api, mailDao) }

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
                                api.loadRange(
                                    Mail::class, folder.mails, GeneratedId(itemAtEnd.id),
                                    40, true
                                )
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

    fun markAsRead(ids: List<String>) {
        markReadUnread(ids, false)
    }

    fun markAsUnread(ids: List<String>) {
        markReadUnread(ids, true)
    }

    private fun markReadUnread(ids: List<String>, unread: Boolean) {
        viewModelScope.launch {
            for (id in ids) {
                val mail = mailDao.getMail(id)
                    .copy(unread = unread)
                    .toMail()
                try {
                    api.updateEntity(mail)
                } catch (e: ResponseException) {
                    Log.e("Mail", "Failed to mark as unread", e)
                }
            }
        }
    }

    fun delete(ids: List<String>) {
        val folders = folders.value ?: return
        val trashFolder = folders.find { it.folderType == MailFolderType.TRASH.value }!!
        moveMails(ids, trashFolder)
    }

    fun archive(ids: List<String>) {
        val folders = folders.value ?: return
        val archiveFolder = folders.find { it.folderType == MailFolderType.ARCHIVE.value }!!
        moveMails(ids, archiveFolder)
    }

    fun moveMails(ids: List<String>, targetFolder: MailFolderEntity) {
        viewModelScope.launch {
            val currentMailList = selectedFolder.value!!.mails
            mailRepository.moveMails(
                ids.map { id -> IdTuple(currentMailList, GeneratedId(id)) },
                IdTuple(
                    GeneratedId(targetFolder.listId),
                    GeneratedId(targetFolder.id)
                )
            )
        }
    }

    suspend fun loadMailBody(mailBodyId: Id): MailBodyEntity {
        return mailRepository.getMailBody(mailBodyId)
    }

    fun search(query: String): LiveData<PagedList<MailEntity>> {
        Log.d("Mails", "search $query")
        return mailDao.search(query).toLiveData(40)
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