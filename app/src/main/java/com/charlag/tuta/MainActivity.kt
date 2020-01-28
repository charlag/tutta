package com.charlag.tuta

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.data.MailFolderEntity
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.mail.MailListFragment
import com.charlag.tuta.mail.MailViewModel
import com.charlag.tuta.settings.SettingsActivity
import com.charlag.tuta.util.withLifecycleContext
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.mail_menu.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import javax.crypto.Cipher

class MainActivity : AppCompatActivity() {
    private val viewModel: MailViewModel by viewModels()
    private val foldersAdapter = MailFoldersAdapter { selectedFolder ->
        viewModel.selectFolder(
            IdTuple(
                GeneratedId(selectedFolder.listId),
                GeneratedId(selectedFolder.id)
            )
        )
        drawerLayout.closeDrawer(navigationView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val userId = prefs.getString("userId", null)
        val accessToken = prefs.getString("accessToken", null)
        val encPassword = prefs.getString("password", null)
        val mailAddress = prefs.getString("mailAddress", null)

        foldersRecycler.layoutManager = LinearLayoutManager(this)
        foldersRecycler.adapter = foldersAdapter

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        if (userId != null && accessToken != null && encPassword != null && mailAddress != null) {
            lifecycleScope.launch {
                try {
                    val passwordWithIvBytes = base64ToBytes(encPassword)
                    val iv = passwordWithIvBytes.copyOfRange(0, 16)
                    val encPasswordBytes = passwordWithIvBytes.copyOfRange(16, passwordWithIvBytes.size)
                    val cipher = getCipher(iv) ?: return@launch
                    val password = bytesToString(cipher.doFinal(encPasswordBytes))
                    DependencyDump.credentials =
                        Credentials(userId, accessToken, password, mailAddress)
                    GlobalScope.launch {
                        try {
                            DependencyDump.loginFacade.resumeSession(
                                mailAddress,
                                GeneratedId(userId),
                                accessToken,
                                password
                            )
                        } catch (e: IOException) {
                            Log.w("Main", "Failed to log in because of IO $e")
                        }
                    }
                } catch (e: Exception) {
                    Log.d("Mail", "Failed to log in", e)
                    goToLogin()
                }

            }
        } else {
            goToLogin()
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
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    fun openDrawer() {
        drawerLayout.openDrawer(navigationView)
    }

    suspend fun getCipher(iv: ByteArray): Cipher? {
        val deferred = CompletableDeferred<Cipher?>()
        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    ).show()
                    deferred.complete(null)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    ).show()
                    deferred.complete(result.cryptoObject?.cipher)
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    deferred.complete(null)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setNegativeButtonText("Use account password")
            .build()
        prompt.authenticate(
            promptInfo,
            BiometricPrompt.CryptoObject(CredentialsManager().prepareCipher(iv))
        )
        return deferred.await()
    }
}

class MailFoldersAdapter(
    private val onFolderSelected: (MailFolderEntity) -> Unit
) : RecyclerView.Adapter<MailFoldersAdapter.ViewHolder>() {

    val folders = mutableListOf<MailFolderEntity>()
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
        val isSelectedFolder = folder.id == selectedFolder?.elementId?.asString()
        val contentColor = holder.itemView.context.getColor(
            if (isSelectedFolder) R.color.colorAccent else R.color.primaryOnSurface
        )
        val bgId = if (isSelectedFolder) R.drawable.selected_mail_bg
        else android.R.color.transparent
        holder.folderName.text = getFolderName(folder)
        holder.foldericon.setImageResource(getFolderIcon(folder))
        holder.folderName.setTextColor(contentColor)
        holder.foldericon.imageTintList = ColorStateList.valueOf(contentColor)
        holder.itemView.setBackgroundResource(bgId)
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
        else -> R.drawable.ic_email_black_24dp
    }
}

val mailFolderOrder = mapOf(
    MailFolderType.INBOX.value to 0,
    MailFolderType.DRAFT.value to 1,
    MailFolderType.SENT.value to 2,
    MailFolderType.TRASH.value to 4,
    MailFolderType.ARCHIVE.value to 5,
    MailFolderType.SPAM.value to 6
)

fun sortSystemFolders(folders: List<MailFolderEntity>): List<MailFolderEntity> {
    return folders.sortedBy { mailFolderOrder[it.folderType] }
}