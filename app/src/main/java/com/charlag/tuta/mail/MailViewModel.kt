package com.charlag.tuta.mail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.charlag.tuta.*
import com.charlag.tuta.data.*
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.files.FileHandler
import com.charlag.tuta.util.combineLiveData
import com.charlag.tuta.util.map
import io.ktor.client.features.ResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MailViewModel : ViewModel() {
    private val loginFacade = DependencyDump.loginFacade
    private val api = DependencyDump.api
    private val mailDao = DependencyDump.db.mailDao()
    private val mailRepository = MailRepository(api, mailDao)
    private val fileHandler: FileHandler = DependencyDump.fileHandler

    val selectedFolderId = MutableLiveData<IdTuple>()
    val folders: LiveData<List<MailFolderEntity>>
    val selectedFolder: LiveData<MailFolderEntity?>

    init {
        folders = mailDao.getFoldersLiveData()
            .map(this::sortFolders)
            .map { dbFolders ->
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

    private suspend fun loadMailsFromNetwork(listId: Id, startId: Id): List<MailEntity> {
        return api.loadRange(
            Mail::class,
            listId,
            startId,
            MAIL_LOAD_PAGE,
            true
        ).map { it.toEntity() }
    }

    fun loadMails(folder: MailFolderEntity): LiveData<PagedList<MailEntity>> {
        val singleTopHelper = SingleOperationHelper(viewModelScope)
        val singleBottomHelper = SingleOperationHelper(viewModelScope)
        return mailDao.getMailsLiveData(folder.mails.asString())
            .toLiveData(
                pageSize = 40,
                boundaryCallback = object : PagedList.BoundaryCallback<MailEntity>() {
                    override fun onZeroItemsLoaded() {
                        Log.d("MailViewModel", "onZeroItems")
                        singleTopHelper.execute {
                            loginFacade.waitForLogin()
                            val mails = loadMailsFromNetwork(folder.mails, GENERATED_MAX_ID)
                            Log.d("MailViewModel", "onZeroItems fetched")
                            if (mails.isEmpty()) {
                                singleTopHelper.setExhausted()
                            }
                            mailDao.insertMails(mails)
                        }
                    }

                    override fun onItemAtEndLoaded(itemAtEnd: MailEntity) {
                        Log.d("MailViewModel", "onItemAtEndLoaded")
                        singleBottomHelper.execute {
                            loginFacade.waitForLogin()
                            val mails =
                                loadMailsFromNetwork(folder.mails, GeneratedId(itemAtEnd.id))
                            Log.d("MailViewModel", "onItemAtEndLoaded fetched")
                            if (mails.isEmpty()) {
                                singleBottomHelper.setExhausted()
                            }
                            mailDao.insertMails(mails)
                        }
                    }
                })
    }

    val openedMail = MutableLiveData<MailEntity?>()

    fun setOpenedMail(mail: MailEntity?) {
        openedMail.value = mail
        if (mail != null && mail.unread) {
            viewModelScope.launch {
                markAsRead(listOf(mail.id))
            }
        }
    }

    suspend fun markAsRead(ids: List<String>) {
        markReadUnread(ids, false)
    }

    suspend fun markAsUnread(ids: List<String>) {
        markReadUnread(ids, true)
    }

    private suspend fun markReadUnread(ids: List<String>, unread: Boolean) {
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

    suspend fun trash(ids: List<String>) {
        val folders = folders.value ?: return
        val trashFolder = folders.find { it.folderType == MailFolderType.TRASH.value }!!
        moveMails(ids, trashFolder)
    }

    suspend fun archive(ids: List<String>) {
        val folders = folders.value ?: return
        val archiveFolder = folders.find { it.folderType == MailFolderType.ARCHIVE.value }!!
        moveMails(ids, archiveFolder)
    }

    suspend fun moveMails(ids: List<String>, targetFolder: MailFolderEntity) {
        val currentMailList = selectedFolder.value!!.mails
        mailRepository.moveMails(
            ids.map { id -> IdTuple(currentMailList, GeneratedId(id)) },
            IdTuple(
                GeneratedId(targetFolder.listId),
                GeneratedId(targetFolder.id)
            )
        )
    }

    suspend fun loadMailBody(mailBodyId: Id): MailBodyEntity {
        return mailRepository.getMailBody(mailBodyId)
    }

    suspend fun loadAttachments(mail: MailEntity): List<File> {
        return mail.attachments.map {
            api.loadListElementEntity<File>(it)
        }
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
                .flatMap { folder ->
                    val folderWithSubfolders = mutableListOf(folder)
                    if (folder.folderType == MailFolderType.ARCHIVE.value) {
                        folderWithSubfolders.addAll(
                            api.loadAll(
                                MailFolder::class,
                                folder.subFolders
                            )
                        )
                    }
                    folderWithSubfolders
                }
                .map {
                    it.toEntity()
                }
                .let(::sortFolders)

        }
    }

    fun openFile(file: File) {
        viewModelScope.launch {
            fileHandler.openFile(file)
        }
    }

    fun downloadFile(file: File) {
        viewModelScope.launch {
            fileHandler.downloadFile(file)
        }
    }


    private val mailFolderOrder = mapOf(
        MailFolderType.INBOX.value to 0,
        MailFolderType.DRAFT.value to 1,
        MailFolderType.SENT.value to 2,
        MailFolderType.TRASH.value to 4,
        MailFolderType.ARCHIVE.value to 5,
        MailFolderType.SPAM.value to 6,
        MailFolderType.CUSTOM.value to 7
    )

    private fun sortFolders(folders: List<MailFolderEntity>): List<MailFolderEntity> {
        return folders.sortedWith(Comparator { left, right ->
            val leftOrder = mailFolderOrder.getValue(left.folderType)
            val rightOrder = mailFolderOrder.getValue(right.folderType)
            if (leftOrder == rightOrder) {
                left.name.compareTo(right.name)
            } else {
                leftOrder.compareTo(rightOrder)
            }
        })
    }

    companion object {
        private const val MAIL_LOAD_PAGE = 40
    }
}

private class SingleOperationHelper(
    private val coroutineScope: CoroutineScope
) {
    private var loading = false
    private var exhausted = false

    fun setExhausted() {
        exhausted = true
    }

    inline fun execute(crossinline operation: suspend () -> Unit) {
        coroutineScope.launch {
            if (!exhausted && !loading) {
                loading = true
                try {
                    operation()
                } finally {
                    loading = false
                }
            }
        }
    }
}