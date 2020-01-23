package com.charlag.tuta

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.util.combineLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MailViewModel : ViewModel() {
    private val loginFacade = DependencyDump.loginFacade
    private val api = DependencyDump.api

    val selectedFolderId = MutableLiveData<IdTuple>()
    val folders = MutableLiveData<List<MailFolder>>()

    val selectedFolder = combineLiveData(selectedFolderId, folders) { folderId, folders ->
        folders.find { it._id == folderId }
    }

    fun selectFolder(folderId: IdTuple) {
        selectedFolderId.value = folderId
    }

    val openedMail = MutableLiveData<Mail?>()

    fun setOpenedMail(mail: Mail?) {
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