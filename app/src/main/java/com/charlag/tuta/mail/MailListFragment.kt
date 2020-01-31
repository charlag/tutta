package com.charlag.tuta.mail

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlag.tuta.MainActivity
import com.charlag.tuta.R
import com.charlag.tuta.compose.ComposeActivity
import com.charlag.tuta.getFolderName
import com.charlag.tuta.util.map
import com.charlag.tuta.util.withLifecycleContext
import kotlinx.android.synthetic.main.fragment_mail_list.*

class MailListFragment : Fragment() {

    private var actionmode: ActionMode? = null
    val viewModel: MailViewModel by activityViewModels()


    private val tint by lazy(LazyThreadSafetyMode.NONE) {
        val tintColor = toolbar.context.getColor(R.color.primaryOnSurface)
        ColorStateList.valueOf(tintColor)
    }

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
            .replace(
                R.id.fragemntFrame,
                MailViewerFragment()
            )
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
        val searchId = View.generateViewId()
        toolbar.menu.add(0, searchId, 0, "Search")
            .setIcon(R.drawable.ic_search_black_24dp)
            .setIconTintList(tint)
            .setOnMenuItemClickListener {
                val menuView = toolbar.findViewById<View>(searchId)
                val viewLocation = IntArray(2)
                menuView.getLocationInWindow(viewLocation)
                val coords = Coordinates(viewLocation[0], viewLocation[1])
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragemntFrame, SearchFragment(coords))
                    .addToBackStack(null)
                    .commit()
                true
            }
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        fab.setOnClickListener {
            startActivity(Intent(activity, ComposeActivity::class.java))
        }

        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
        recycler.setHasFixedSize(true)

        val emptyDrawable = view.context.getDrawable(R.drawable.ic_inbox_black_24dp)!!
        val emptyDrawableSize = (60 * view.context.resources.displayMetrics.density).toInt()
        emptyDrawable.setTint(view.context.getColor(R.color.primaryOnSurface))
        emptyDrawable.setBounds(
            0,
            0,
            emptyDrawableSize,
            emptyDrawableSize
        )
        emptyTextView.setCompoundDrawables(null, emptyDrawable, null, null)

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

        withLifecycleContext {
            viewModel.selectedFolder.switchMap { folder ->
                if (folder == null) MutableLiveData<Unit>()
                else viewModel.loadMails(folder).map {
                    adapter.submitList(it)
                    progress.visibility = View.GONE
                    emptyTextView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                }
            }.subscribe()
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
                    .setIconTintList(tint)
                    .setOnMenuItemClickListener {
                        viewModel.delete(selectedIds())
                        actionmode?.finish()
                        true
                    }
                menu.add("Archive")
                    .setIcon(R.drawable.ic_archive_black_24dp)
                    .setIconTintList(tint)
                    .setOnMenuItemClickListener {
                        viewModel.archive(selectedIds())
                        actionmode?.finish()
                        true
                    }
                menu.add("Mark as read")
                    .setIcon(R.drawable.ic_eye_black_24dp)
                    .setIconTintList(tint)
                    .setOnMenuItemClickListener {
                        viewModel.markAsRead(selectedIds())
                        actionmode?.finish()
                        true
                    }
                menu.add("Mark as unread")
                    .setIcon(R.drawable.ic_email_black_24dp)
                    .setIconTintList(tint)
                    .setOnMenuItemClickListener {
                        viewModel.markAsUnread(selectedIds())
                        actionmode?.finish()
                        true
                    }

                menu.add("Move to")
                    .setOnMenuItemClickListener {
                        val currentFolder = viewModel.selectedFolderId.value?.elementId?.asString()
                        val folders = viewModel.folders.value?.filter { it.id != currentFolder }
                            ?: return@setOnMenuItemClickListener false
                        val selectedIds = selectedIds()
                        AlertDialog.Builder(toolbar.context)
                            .setItems(folders.map { getFolderName(it) }.toTypedArray()) { dialog, which ->
                                val folder = folders[which]
                                Log.d("Mails", "Folder was selected $which $folder")
                                viewModel.moveMails(selectedIds, folder)
                                dialog.dismiss()
                            }
                            .show()
                        actionmode?.finish()
                        true
                    }
                return true
            }

            private fun selectedIds() = adapter.selectionTracker.selection.toList()

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                adapter.selectionTracker.clearSelection()
                actionmode = null
            }
        })
    }
}