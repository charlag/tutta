package com.charlag.tuta

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.contacts.ContactsActivity
import com.charlag.tuta.data.MailFolderEntity
import com.charlag.tuta.data.MailFolderWithCounter
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.mail.MailListFragment
import com.charlag.tuta.mail.MailViewModel
import com.charlag.tuta.settings.SettingsActivity
import com.charlag.tuta.util.withLifecycleContext
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_folder.view.*
import kotlinx.android.synthetic.main.mail_menu.*

class MainActivity : AppCompatActivity() {
    private val viewModel: MailViewModel by viewModels()
    private val foldersAdapter = MailFoldersAdapter { selectedFolder ->
        viewModel.selectFolder(
            IdTuple(
                GeneratedId(selectedFolder.folder.listId),
                GeneratedId(selectedFolder.folder.id)
            )
        )
        drawerLayout.closeDrawer(navigationView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        foldersRecycler.layoutManager = LinearLayoutManager(this)
        foldersRecycler.adapter = foldersAdapter

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        contactsButton.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragemntFrame,
                MailListFragment()
            )
            .commit()

        withLifecycleContext {
            viewModel.selectedFolderId.observe {
                foldersAdapter.selectedFolder = it
                foldersAdapter.notifyDataSetChanged()
            }

            viewModel.folders.observe {
                foldersAdapter.folders.clear()
                foldersAdapter.folders.addAll(it)
                foldersAdapter.notifyDataSetChanged()
            }

            viewModel.disaplayedMailAddress.observe {
                mailAddressLabel.text = it
            }
        }
    }

    fun openDrawer() {
        drawerLayout.openDrawer(navigationView)
    }
}

class MailFoldersAdapter(
    private val onFolderSelected: (MailFolderWithCounter) -> Unit
) : RecyclerView.Adapter<MailFoldersAdapter.ViewHolder>() {

    val folders = mutableListOf<MailFolderWithCounter>()
    var selectedFolder: IdTuple? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderName = itemView.findViewById<TextView>(R.id.folderName)
        val foldericon = itemView.findViewById<ImageView>(R.id.folderIcon)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onFolderSelected(folders[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = folders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]
        val isSelectedFolder = folder.folder.id == selectedFolder?.elementId?.asString()
        val contentColor = holder.itemView.context.getColor(
            if (isSelectedFolder) R.color.colorAccent else R.color.grey_30
        )
        val bgId = if (isSelectedFolder) R.drawable.selected_mail_bg
        else android.R.color.transparent
        holder.folderName.text = getFolderName(folder.folder)
        holder.foldericon.setImageResource(getFolderIcon(folder.folder))
        holder.folderName.setTextColor(contentColor)
        holder.foldericon.imageTintList = ColorStateList.valueOf(contentColor)
        holder.itemView.setBackgroundResource(bgId)
        if (folder.counter > 0) {
            holder.itemView.counterLabel.isVisible = true
            holder.itemView.counterLabel.text = folder.counter.toString()
        } else {
            holder.itemView.counterLabel.isVisible = false
        }
    }
}

fun getFolderName(folder: MailFolderEntity): String {
    return when (folder.folderType) {
        MailFolderType.CUSTOM.value -> folder.name
        MailFolderType.INBOX.value -> "Inbox"
        MailFolderType.SENT.value -> "Sent"
        MailFolderType.TRASH.value -> "Trash"
        MailFolderType.ARCHIVE.value -> "Archive"
        MailFolderType.SPAM.value -> "Spam"
        MailFolderType.DRAFT.value -> "Drafts"
        else -> ""
    }
}

@DrawableRes
fun getFolderIcon(folder: MailFolderEntity): Int {
    return when (folder.folderType) {
        MailFolderType.INBOX.value -> R.drawable.ic_inbox_black_24dp
        MailFolderType.SENT.value -> R.drawable.ic_send_black_24dp
        MailFolderType.TRASH.value -> R.drawable.ic_delete_black_24dp
        MailFolderType.ARCHIVE.value -> R.drawable.ic_archive_black_24dp
        MailFolderType.DRAFT.value -> R.drawable.ic_drafts_black_24dp
        MailFolderType.SPAM.value -> R.drawable.ic_announcement_black_24dp
        else -> R.drawable.ic_folder_black_24dp
    }
}