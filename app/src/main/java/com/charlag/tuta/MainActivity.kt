package com.charlag.tuta

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.tutanota.MailBox
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.entities.tutanota.MailboxGroupRoot
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    val foldersAdapter = MailFoldersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragemntFrame, MailListFragment())
            .commit()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val userId = prefs.getString("userId", null)
        val accessToken = prefs.getString("accessToken", null)
        val password = prefs.getString("password", null)
        val mailAddress = prefs.getString("mailAddress", null)

        foldersRecycler.layoutManager = LinearLayoutManager(this)
        foldersRecycler.adapter = foldersAdapter

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
                    val mailMembership = DependencyDump.loginFacade.user!!.memberships
                        .find { it.groupType == GroupType.Mail.value }!!
                    val api = DependencyDump.api
                    val groupRoot = api
                        .loadElementEntity<MailboxGroupRoot>(mailMembership.group)
                    val mailbox = api.loadElementEntity<MailBox>(groupRoot.mailbox)
                    val folders = api.loadAll(MailFolder::class, mailbox.systemFolders!!.folders)
                    Log.d("Main", "Folders $folders")

                    withContext(Dispatchers.Main) {
                        foldersAdapter.folders.addAll(folders)
                        foldersAdapter.notifyDataSetChanged()
                    }
                }
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }
}

class MailFoldersAdapter : RecyclerView.Adapter<MailFoldersAdapter.ViewHolder>() {

    val folders = mutableListOf<MailFolder>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderName = itemView.findViewById<TextView>(R.id.folderName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = folders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.folderName.text = getFolderName(folders[position])
    }
}

fun getFolderName(folder: MailFolder): String {
    return when (folder.folderType) {
        MailFolderType.CUSTOM.value -> folder.name
        MailFolderType.INBOX.value -> "Inbox"
        MailFolderType.SENT.value -> "Sent"
        MailFolderType.TRASH.value -> "Trash"
        MailFolderType.ARCHIVE.value -> "Archive"
        MailFolderType.SPAM.value -> "Spam"
        MailFolderType.DRAFT.value -> "Draft"
        else -> ""
    }
}