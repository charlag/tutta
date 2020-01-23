package com.charlag.tuta

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.selection.*
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

    private var actionmode: ActionMode? = null
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

        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        fab.setOnClickListener {
            startActivity(Intent(activity, ComposeActivity::class.java))
        }

        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter

        adapter.selectionTracker = SelectionTracker.Builder(
            "selected-mail-id",
            recycler,
            object : ItemKeyProvider<String>(SCOPE_MAPPED) {
                override fun getKey(position: Int): String? {
                    return adapter.mails[position]._id.elementId.asString()
                }

                override fun getPosition(key: String): Int {
                    return adapter.mails.indexOfFirst { it._id.elementId.asString() == key }
                }
            },
            object : ItemDetailsLookup<String>() {
                override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
                    return recycler.findChildViewUnder(e.x, e.y)?.let {
                        val viewHolder =
                            recycler.getChildViewHolder(it) as MailsAdapter.MailviewHolder
                        viewHolder.itemDetails()
                    }
                }

            },
            StorageStrategy.createStringStorage()
        ).build()
        adapter.selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<String>() {
            override fun onSelectionChanged() {
                if (adapter.selectionTracker.hasSelection()) {
                    if (actionmode != null) {
                        actionmode?.invalidate()
                    } else {
                        startActionMode()
                    }
                } else {
                    actionmode?.finish()
                }
            }
        })

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

    private fun startActionMode() {
        this.actionmode = toolbar.startActionMode(object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
                return false
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu): Boolean {
                menu.add("Delete")
                    .setIcon(R.drawable.ic_delete_black_24dp)
                    .setOnMenuItemClickListener {
                        actionmode?.finish()
                        true
                    }
                menu.add("Archive")
                    .setIcon(R.drawable.ic_archive_black_24dp)
                    .setOnMenuItemClickListener {
                        actionmode?.finish()
                        true
                    }
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                adapter.selectionTracker.clearSelection()
                actionmode = null
            }
        })
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