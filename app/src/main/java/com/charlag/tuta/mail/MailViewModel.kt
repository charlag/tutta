package com.charlag.tuta.mail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.charlag.tuta.DependencyDump
import com.charlag.tuta.GroupType
import com.charlag.tuta.MailFolderType
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
    private val mailRepository: MailRepository = DependencyDump.mailRepository
    private val fileHandler: FileHandler = DependencyDump.fileHandler
    private val userController = DependencyDump.userController

    val selectedFolderId = MutableLiveData<IdTuple>()
    val folders: LiveData<List<MailFolderWithCounter>>
    val selectedFolder: LiveData<MailFolderEntity?>
    val disaplayedMailAddress = MutableLiveData<String>()

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
                        dbFolders.find { it.folder.folderType == MailFolderType.INBOX.value }!!
                    selectFolder(
                        IdTuple(
                            GeneratedId(selectedFolder.folder.listId),
                            GeneratedId(selectedFolder.folder.id)
                        )
                    )
                }
                dbFolders
            }
        selectedFolder = combineLiveData(selectedFolderId, folders) { folderId, folders ->
            folders.find { it.folder.id == folderId.elementId.asString() }?.folder
        }

        viewModelScope.launch {
            val mailAddress = userController.getUserGroupInfo().mailAddress
            disaplayedMailAddress.postValue(mailAddress)
        }
    }

    fun selectFolder(folderId: IdTuple) {
        selectedFolderId.value = folderId
    }

    private suspend fun loadMailsFromNetwork(listId: Id, startId: Id): List<MailEntity> {
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
            val mail = (mailDao.getMail(id) ?: continue)
                .copy(unread = unread)
                .toMail()
            try {
                api.updateEntity(mail)
            } catch (e: ResponseException) {
                Log.e("Mail", "Failed to mark as unread", e)
            }
        }
    }

    suspend fun delete(ids: List<String>) {
        // Assumes that all mails come from the same list for now
        val trashFolder = getTrashFolder() ?: return
        val selectedFolder = selectedFolder.value ?: return
        if (selectedFolder.id == trashFolder.id) {
            val folderId = IdTuple.fromRawValues(selectedFolder.listId, selectedFolder.id)
            mailRepository.deleteMails(
                folderId,
                ids.map { IdTuple(selectedFolder.mails, GeneratedId(it)) })
        } else {
            Log.w(TAG, "Trying to permanently delete mails in non-trash folder $selectedFolder")
        }
    }

    suspend fun trash(ids: List<String>) {
        moveMails(ids, getTrashFolder() ?: return)
    }

    private fun getTrashFolder(): MailFolderEntity? {
        return folders.value?.find { it.folder.folderType == MailFolderType.TRASH.value }?.folder
    }

    suspend fun archive(ids: List<String>) {
        val folders = folders.value ?: return
        val archiveFolder = folders.find { it.folder.folderType == MailFolderType.ARCHIVE.value }!!
        moveMails(ids, archiveFolder.folder)
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
        Log.d(TAG, "search $query")
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

    private fun sortFolders(folders: List<MailFolderWithCounter>): List<MailFolderWithCounter> {
        return folders.sortedWith(Comparator { left, right ->
            val leftOrder = mailFolderOrder.getValue(left.folder.folderType)
            val rightOrder = mailFolderOrder.getValue(right.folder.folderType)
            if (leftOrder == rightOrder) {
                left.folder.name.compareTo(right.folder.name)
            } else {
                leftOrder.compareTo(rightOrder)
            }
        })
    }

    companion object {
        private const val MAIL_LOAD_PAGE = 40
        private const val TAG = "Mails"
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