package com.charlag.tuta.mail

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.switchMap
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.charlag.tuta.MailFolderType
import com.charlag.tuta.MainActivity
import com.charlag.tuta.R
import com.charlag.tuta.compose.ComposeActivity
import com.charlag.tuta.compose.DraftInitData
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.getFolderName
import com.charlag.tuta.util.map
import com.charlag.tuta.util.setIconTintListCompat
import com.charlag.tuta.util.withLifecycleContext
import kotlinx.android.synthetic.main.fragment_mail_list.*
import kotlinx.coroutines.launch

class MailListFragment : Fragment() {
    private var actionmode: ActionMode? = null
    val viewModel: MailViewModel by activityViewModels()

    private val tint by lazy(LazyThreadSafetyMode.NONE) {
        val tintColor = toolbar.context.getColor(R.color.grey_30)
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
        val selectedFolder = viewModel.selectedFolder.value!!
        if (selectedFolder.folderType == MailFolderType.DRAFT.value) {
            val draftInitData = DraftInitData(
                draftId = mail.id,
                listId = mail.listId
            )
            startActivity(ComposeActivity.intentEditDraft(context!!, draftInitData))
        } else {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragemntFrame,
                    MailViewerFragment.withMailId(IdTuple.fromRawValues(mail.listId, mail.id))
                )
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
        }
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
            .setIconTintListCompat(tint)
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
        (recycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        val swipeCallback = MailSwipeCallback(context!!) { direction, position ->
            val mailId = adapter.currentList?.get(position)?.id ?: return@MailSwipeCallback
            if (direction == ItemTouchHelper.RIGHT) {
                trashMails(view, viewModel, listOf(mailId))
            } else {
                archiveMails(view, viewModel, listOf(mailId))
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recycler)

        val emptyDrawable = view.context.getDrawable(R.drawable.ic_inbox_black_24dp)!!
        val emptyDrawableSize = (60 * view.context.resources.displayMetrics.density).toInt()
        emptyDrawable.setTint(view.context.getColor(R.color.grey_30))
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
                else viewModel.loadMails(folder).map { mails ->
                    adapter.submitList(mails)
                    emptyTextView.visibility = if (mails.isEmpty()) View.VISIBLE else View.GONE
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
                val view = view!!
                menu.add("Delete")
                    .setIcon(R.drawable.ic_delete_black_24dp)
                    .setIconTintListCompat(tint)
                    .setOnMenuItemClickListener {
                        trashMails(view, viewModel, selectedIds())
                        actionmode?.finish()
                        true
                    }
                menu.add("Archive")
                    .setIcon(R.drawable.ic_archive_black_24dp)
                    .setIconTintListCompat(tint)
                    .setOnMenuItemClickListener {
                        archiveMails(view, viewModel, selectedIds())
                        actionmode?.finish()
                        true
                    }
                menu.add("Mark as read")
                    .setIcon(R.drawable.ic_drafts_black_24dp)
                    .setIconTintListCompat(tint)
                    .setOnMenuItemClickListener {
                        markAsRead(view, viewModel, selectedIds(), false)
                        actionmode?.finish()
                        true
                    }
                menu.add("Mark as unread")
                    .setIcon(R.drawable.ic_email_black_24dp)
                    .setIconTintListCompat(tint)
                    .setOnMenuItemClickListener {
                        markAsRead(view, viewModel, selectedIds(), true)
                        actionmode?.finish()
                        true
                    }

                menu.add("Move to")
                    .setOnMenuItemClickListener {
                        lifecycleScope.launch {
                            moveMails(view, viewModel, selectedIds())
                            actionmode?.finish()
                        }
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