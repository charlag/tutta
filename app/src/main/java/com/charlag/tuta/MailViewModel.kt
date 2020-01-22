package com.charlag.tuta

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.MailBox
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.entities.tutanota.MailboxGroupRoot
import com.charlag.tuta.util.combineLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MailViewModel : ViewModel() {
    private val loginFacade = DependencyDump.loginFacade

    val selectedFolderId = MutableLiveData<IdTuple>()
    val folders = MutableLiveData<List<MailFolder>>()

    val selectedFolder = combineLiveData(selectedFolderId, folders) { folderId, folders ->
        folders.find { it._id == folderId }
    }

    fun selectFolder(folderId: IdTuple) {
        selectedFolderId.value = folderId
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