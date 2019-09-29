package com.charlag.tuta

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailBox
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.entities.tutanota.MailboxGroupRoot
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class MainActivity : Activity() {

    private val adapter = MailsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        val progress = findViewById<ProgressBar>(R.id.progress)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val userId = prefs.getString("userId", null)
        val accessToken = prefs.getString("accessToken", null)
        val password = prefs.getString("password", null)
        val mailAddress = prefs.getString("mailAddress", null)


        if (userId != null && accessToken != null && password != null && mailAddress != null) {
            DependencyDump.credentials = Credentials(userId, accessToken, password, mailAddress)
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    DependencyDump.loginFacade.resumeSession(
                        mailAddress,
                        GeneratedId(userId),
                        accessToken,
                        password
                    )
                    loadMails(DependencyDump.loginFacade, DependencyDump.api)
                }
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
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
        println(mails)
        withContext(Dispatchers.Main) {
            progress.visibility = View.INVISIBLE
            adapter.mails += mails
            adapter.notifyDataSetChanged()
        }
    }
}

class MailsAdapter : RecyclerView.Adapter<MailsAdapter.MailviewHolder>() {
    val mails = mutableListOf<Mail>()

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): MailviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mail, parent, false)
        return MailviewHolder(view)
    }

    override fun getItemCount(): Int = mails.size

    override fun onBindViewHolder(holder: MailviewHolder, index: Int) {
        val item = mails[index]
        holder.sender.text =
            if (item.sender.name.isNotBlank()) "${item.sender.name} ${item.sender.address}"
            else item.sender.address
        holder.subject.text = item.subject
        holder.date.text =
            DateFormat.getDateInstance(DateFormat.SHORT).format(Date(item.receivedDate.millis))
    }

    class MailviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sender: TextView = itemView.findViewById(R.id.sender)
        val date: TextView = itemView.findViewById(R.id.date)
        val subject: TextView = itemView.findViewById(R.id.subject)
    }
}