package com.charlag.tuta

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailBox
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.entities.tutanota.MailboxGroupRoot
import kotlinx.android.synthetic.main.fragment_mail_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MailListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mail_list, container, false)
    }

    private val adapter = MailsAdapter { mail ->
        val intent = Intent(activity, MailViewerActivity::class.java)
            .putExtra("mailBodyId", mail.body.asString())
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setOnClickListener {
            startActivity(Intent(activity, ComposeActivity::class.java))
        }

        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter

        GlobalScope.launch {
            DependencyDump.loginFacade.loggedIn.await()
            loadMails(DependencyDump.loginFacade, DependencyDump.api)
        }
    }

    private suspend fun loadMails(
        loginFacade: LoginFacade,
        api: API
    ) {
        val mailMembership = loginFacade.user!!.memberships.filter {
            it.groupType == GroupType.Mail.value
        }.first()
        val mailboxGroupRoot: MailboxGroupRoot =
            api.loadElementEntity(mailMembership.group)
        val mailbox = api.loadElementEntity<MailBox>(mailboxGroupRoot.mailbox)
        val folders = api.loadAll(MailFolder::class, mailbox.systemFolders!!.folders)
        val inbox = folders.find { it.folderType == MailFolderType.INBOX.value }
        val mails = api.loadRange(Mail::class, inbox!!.mails, GENERATED_MAX_ID, 40, true)
        withContext(Dispatchers.Main) {
            progress.visibility = View.INVISIBLE
            adapter.mails += mails
            adapter.notifyDataSetChanged()
        }
    }
}