package com.charlag.tuta.mail

import android.content.Context
import android.graphics.Typeface
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.paging.PagedListAdapter
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.R
import com.charlag.tuta.data.MailEntity
import java.text.DateFormat
import java.util.*

class MailsAdapter(
    val onSelected: (MailEntity) -> Unit
) : PagedListAdapter<MailEntity, MailsAdapter.MailviewHolder>(DIFF_CALLBACK) {
    private val fullFormat = DateFormat.getDateInstance(DateFormat.SHORT)
    private val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
    lateinit var selectionTracker: SelectionTracker<String>
    private val cal = Calendar.getInstance()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        selectionTracker = makeSelectionTracker(recyclerView)
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): MailviewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mail, parent, false)
        return MailviewHolder(view)
    }

    override fun onBindViewHolder(holder: MailviewHolder, index: Int) {
        val mail = getItem(index) ?: return
        holder.sender.text =
            if (mail.sender.name.isNotBlank()) "${mail.sender.name} ${mail.sender.address}"
            else mail.sender.address
        holder.subject.text = mail.subject
        holder.date.text = formatDate(holder.itemView.context, mail)
        holder.itemView.setOnClickListener { onSelected(mail) }
        holder.subject.setTypeface(null, if (mail.unread) Typeface.BOLD else Typeface.NORMAL)
        if (selectionTracker.isSelected(mail.id)) {
            holder.itemView.setBackgroundResource(R.drawable.selected_mail_bg)
        } else {
            holder.itemView.background = null
        }
        holder.fileIndicator.isGone = mail.attachments.isEmpty()
        holder.encryptionIndicator.isGone = !mail.confidential
        holder.unreadIndicator.isGone = !mail.unread
    }

    private fun formatDate(context: Context, mail: MailEntity): String {
        cal.timeInMillis = System.currentTimeMillis()
        // There's an Android DateUtil method to check that but it allocates multiple calendars
        // right away
        val monthNow = cal.get(Calendar.MONTH)
        val dateNow = cal.get(Calendar.DATE)
        val yearNow = cal.get(Calendar.YEAR)
        cal.timeInMillis = mail.receivedDate.time
        return when {
            cal.get(Calendar.YEAR) != yearNow ->
                fullFormat.format(mail.receivedDate)
            cal.get(Calendar.MONTH) == monthNow && cal.get(Calendar.DATE) == dateNow ->
                timeFormat.format(mail.receivedDate)
            else -> DateUtils.formatDateTime(
                context,
                mail.receivedDate.time,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_YEAR or DateUtils.FORMAT_ABBREV_MONTH
            )
        }
    }

    private fun makeSelectionTracker(recycler: RecyclerView): SelectionTracker<String> {
        return SelectionTracker.Builder(
            "selected-mail-id",
            recycler,
            object : ItemKeyProvider<String>(SCOPE_MAPPED) {
                override fun getKey(position: Int): String? {
                    return getItem(position)?.id
                }

                override fun getPosition(key: String): Int {
                    return currentList?.indexOfFirst { it.id == key } ?: -1
                }
            },
            object : ItemDetailsLookup<String>() {
                override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
                    return recycler.findChildViewUnder(e.x, e.y)?.let {
                        val viewHolder =
                            recycler.getChildViewHolder(it) as MailviewHolder
                        viewHolder.itemDetails()
                    }
                }
            },
            StorageStrategy.createStringStorage()
        ).build()
    }

    inner class MailviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sender: TextView = itemView.findViewById(R.id.sender)
        val date: TextView = itemView.findViewById(R.id.date)
        val subject: TextView = itemView.findViewById(R.id.subject)
        val fileIndicator: ImageView = itemView.findViewById(R.id.fileIndicator)
        val encryptionIndicator: ImageView = itemView.findViewById(R.id.encryptionIndicator)
        val unreadIndicator: ImageView = itemView.findViewById(R.id.unreadIndicator)

        fun itemDetails() =
            if (adapterPosition == -1) null
            else getItem(adapterPosition)?.let { mail ->
                MailItemDetails(mail.id, adapterPosition)
            }
    }


    class MailItemDetails(
        val key: String,
        val adapterPosition: Int
    ) : ItemDetailsLookup.ItemDetails<String>() {
        override fun getSelectionKey(): String = key

        override fun getPosition(): Int = adapterPosition
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MailEntity>() {
            override fun areItemsTheSame(oldItem: MailEntity, newItem: MailEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MailEntity, newItem: MailEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}