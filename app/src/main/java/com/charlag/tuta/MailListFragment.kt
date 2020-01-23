package com.charlag.tuta

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.util.withLifecycleContext
import kotlinx.android.synthetic.main.fragment_mail_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MailListFragment : Fragment() {

    val viewModel: MailViewModel by activityViewModels()
    private val api = DependencyDump.api

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mail_list, container, false)
    }

    private val adapter = MailsAdapter { mail ->
        viewModel.setOpenedMail(mail)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragemntFrame, MailViewerFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setOnClickListener {
            startActivity(Intent(activity, ComposeActivity::class.java))
        }

        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter

        var loadMailsJob: Job? = null
        withLifecycleContext {
            viewModel.selectedFolder.observe { folder ->
                loadMailsJob?.cancel()
                if (folder == null) return@observe
                loadMailsJob = lifecycleScope.launch {
                    loadMails(folder)
                }
            }
            viewModel.selectedFolder.observe { folder ->
                toolbar.title = folder?.let(::getFolderName) ?: ""
            }
        }
    }

    private suspend fun loadMails(folder: MailFolder) {
        val mails = api.loadRange(Mail::class, folder.mails, GENERATED_MAX_ID, 40, true)
        withContext(Dispatchers.Main) {
            progress.visibility = View.INVISIBLE
            adapter.mails.clear()
            adapter.mails += mails
            adapter.notifyDataSetChanged()
        }
    }
}