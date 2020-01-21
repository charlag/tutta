package com.charlag.tuta

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
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
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {

    private val adapter = MailsAdapter { mail ->
        val intent = Intent(this, MailViewerActivity::class.java)
            .putExtra("mailBodyId", mail.body.asString())
        startActivity(intent)
    }


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

        fab.setOnClickListener {
            startActivity(Intent(this, ComposeActivity::class.java))
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

class MailsAdapter(
    val onSelected: (Mail) -> Unit
) : RecyclerView.Adapter<MailsAdapter.MailviewHolder>() {
    val mails = mutableListOf<Mail>()

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): MailviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mail, parent, false)
        return MailviewHolder(view)
    }

    override fun getItemCount(): Int = mails.size

    override fun onBindViewHolder(holder: MailviewHolder, index: Int) {
        val mail = mails[index]
        holder.sender.text =
            if (mail.sender.name.isNotBlank()) "${mail.sender.name} ${mail.sender.address}"
            else mail.sender.address
        holder.subject.text = mail.subject
        holder.date.text = formatDate(holder.date.context, mail)
        holder.itemView.setOnClickListener { onSelected(mail) }
        holder.subject.setTypeface(null, if (mail.unread) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun fromThisYear(mail: Mail): Boolean {
        val cal = Calendar.getInstance()
        val yearNow = cal.get(Calendar.YEAR)
        cal.timeInMillis = mail.receivedDate.millis
        return yearNow == cal.get(Calendar.YEAR)
    }

    private fun formatDate(context: Context, mail: Mail): String {
        val date = Date(mail.receivedDate.millis)
        return if (fromThisYear(mail)) {
            SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
        } else {
            DateFormat.getDateFormat(context).format(date)
        }
    }

    class MailviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sender: TextView = itemView.findViewById(R.id.sender)
        val date: TextView = itemView.findViewById(R.id.date)
        val subject: TextView = itemView.findViewById(R.id.subject)
    }
}