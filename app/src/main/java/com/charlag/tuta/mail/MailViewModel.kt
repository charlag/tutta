package com.charlag.tuta.mail

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.charlag.tuta.MailFolderType
import com.charlag.tuta.UserController
import com.charlag.tuta.data.MailBodyEntity
import com.charlag.tuta.data.MailEntity
import com.charlag.tuta.data.MailFolderEntity
import com.charlag.tuta.data.MailFolderWithCounter
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.File
import com.charlag.tuta.files.FileHandler
import com.charlag.tuta.util.LocalAccountData
import com.charlag.tuta.util.combineLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MailViewModel
@Inject constructor(
    private val mailRepository: MailRepository,
    private val fileHandler: FileHandler,
    private val userController: UserController
) : ViewModel() {

    val selectedFolderId = MutableLiveData<IdTuple>()
    val folders: LiveData<List<MailFolderWithCounter>>
    val selectedFolder: LiveData<MailFolderEntity?>
    val displayedMailAddress = MutableLiveData<LocalAccountData>()

    init {
        folders = mailRepository.observeFolders()
            .map(this::sortFolders)
            .map { dbFolders ->
                if (selectedFolderId.value == null) {
                    dbFolders.find { it.folder.folderType == MailFolderType.INBOX.value }
                        ?.let { selectedFolder ->
                            // It might not be there if folders have not been loaded yet
                            selectFolder(
                                IdTuple(
                                    GeneratedId(selectedFolder.folder.listId),
                                    GeneratedId(selectedFolder.folder.id)
                                )
                            )
                        }
                }
                dbFolders
            }
        selectedFolder = combineLiveData(selectedFolderId, folders) { folderId, folders ->
            folders.find { it.folder.id == folderId.elementId.asString() }?.folder
        }

        viewModelScope.launch {
            val mailAddress = userController.getUserGroupInfo().mailAddress
            displayedMailAddress.postValue(LocalAccountData(userController.userId, mailAddress!!))
        }
    }

    fun selectFolder(folderId: IdTuple) {
        selectedFolderId.value = folderId
    }

    fun loadMails(folder: MailFolderEntity): LiveData<PagedList<MailEntity>> {
        val singleTopHelper = SingleOperationHelper(viewModelScope)
        val singleBottomHelper = SingleOperationHelper(viewModelScope)
        return mailRepository.observeMails(folder.mails)
            .toLiveData(
                pageSize = 40,
                boundaryCallback = object : PagedList.BoundaryCallback<MailEntity>() {
                    override fun onZeroItemsLoaded() {
                        Log.d("MailViewModel", "onZeroItems")
                        singleTopHelper.execute {
                            Log.d("MailViewModel", "onZeroItems fetched")
                            if (mailRepository.addMailsFrom(folder.mails, GENERATED_MAX_ID)) {
                                singleTopHelper.setExhausted()
                            }
                        }
                    }

                    override fun onItemAtEndLoaded(itemAtEnd: MailEntity) {
                        Log.d("MailViewModel", "onItemAtEndLoaded")
                        singleBottomHelper.execute {
                            userController.waitForLogin()
                            val exhausted = mailRepository.addMailsFrom(
                                folder.mails,
                                GeneratedId(itemAtEnd.id)
                            )
                            Log.d("MailViewModel", "onItemAtEndLoaded fetched $exhausted")
                            if (exhausted) {
                                singleBottomHelper.setExhausted()
                            }
                        }
                    }
                })
    }

    suspend fun getMail(id: IdTuple): MailEntity? = mailRepository.getMail(id)

    suspend fun markAsRead(ids: List<String>) {
        markReadUnread(ids, false)
    }

    suspend fun markAsUnread(ids: List<String>) {
        markReadUnread(ids, true)
    }

    private suspend fun markReadUnread(ids: List<String>, unread: Boolean) {
        val folder = selectedFolder.value ?: return
        for (id in ids) {
            mailRepository.markReadUnread(
                IdTuple(
                    folder.mails,
                    GeneratedId(id)
                ), unread)
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
                ids.map {
                    IdTuple(
                        selectedFolder.mails,
                        GeneratedId(it)
                    )
                })
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
            ids.map { id ->
                IdTuple(
                    currentMailList,
                    GeneratedId(id)
                )
            },
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
            mailRepository.loadAttachment(it)
        }
    }

    fun search(query: String): LiveData<PagedList<MailEntity>> {
        return mailRepository.search(query).toLiveData(40)
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
                } catch (e: Exception) {
                    Log.e("Operation", "Failed to execute", e)
                } finally {
                    loading = false
                }
            }
        }
    }
}