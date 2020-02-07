package com.charlag.tuta.mail

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.MailFoldersAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun LifecycleOwner.archiveMails(rootView: View, viewModel: MailViewModel, mails: List<String>) {
    lifecycleScope.launch(Dispatchers.Main) {
        viewModel.archive(mails)
        rootView.showSnackbar("Archived ${mails.size} mail(s)")
    }
}

fun LifecycleOwner.trashMails(rootView: View, viewModel: MailViewModel, mails: List<String>) {
    lifecycleScope.launch {
        viewModel.trash(mails)
        rootView.showSnackbar("Trashed ${mails.size} mail(s)")
    }

}

suspend fun LifecycleOwner.moveMails(rootView: View, viewModel: MailViewModel, ids: List<String>) {
    val currentFolder = viewModel.selectedFolderId.value?.elementId?.asString()
    val folders = viewModel.folders.value?.filter { it.folder.id != currentFolder }
        ?: return
    val deferred = CompletableDeferred<Unit>()

    val view = RecyclerView(rootView.context).apply {
        layoutManager = LinearLayoutManager(rootView.context)
        setHasFixedSize(true)
    }

    val dialog = AlertDialog.Builder(rootView.context)
        .setView(view)
        .show()

    val adapter = MailFoldersAdapter { folder ->
        lifecycleScope.launch {
            viewModel.moveMails(ids, folder.folder)
            dialog.dismiss()
            rootView.showSnackbar("Moved ${ids.size} mail(s)")
            deferred.complete(Unit)
        }
    }
    adapter.folders.addAll(folders)
    view.adapter = adapter
    deferred.await()
}

fun LifecycleOwner.markAsRead(
    rootView: View,
    viewModel: MailViewModel,
    mails: List<String>,
    unread: Boolean
) {
    lifecycleScope.launch {
        if (unread) viewModel.markAsUnread(mails) else viewModel.markAsRead(mails)
        rootView.showSnackbar("Marked ${mails.size} mail(s) as ${if (unread) "unread" else "read"}")
    }
}

private fun View.showSnackbar(text: String) {
    Snackbar.make(this, text, Snackbar.LENGTH_SHORT).show()
}