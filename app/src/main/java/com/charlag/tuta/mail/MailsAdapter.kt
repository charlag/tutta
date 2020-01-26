package com.charlag.tuta.mail

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.*

class MailsAdapter(
    val onSelected: (MailEntity) -> Unit
) : PagedListAdapter<MailEntity, MailsAdapter.MailviewHolder>(DIFF_CALLBACK) {
    private val fullFormat = DateFormat.getDateInstance()
    private val shortFormat = SimpleDateFormat(
        "dd/MM",
        Locale.getDefault()
    )
    lateinit var selectionTracker: SelectionTracker<String>

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
        holder.date.text = formatDate(holder.date.context, mail)
        holder.itemView.setOnClickListener { onSelected(mail) }
        holder.subject.setTypeface(null, if (mail.unread) Typeface.BOLD else Typeface.NORMAL)
        if (selectionTracker.isSelected(mail.id)) {
            holder.itemView.setBackgroundResource(R.drawable.selected_mail_bg)
        } else {
            holder.itemView.background = null
        }
    }

    private fun fromThisYear(mail: MailEntity): Boolean {
        val cal = Calendar.getInstance()
        val yearNow = cal.get(Calendar.YEAR)
        cal.timeInMillis = mail.receivedDate.time
        return yearNow == cal.get(Calendar.YEAR)
    }

    private fun formatDate(context: Context, mail: MailEntity): String {
        return if (fromThisYear(mail)) {
            shortFormat.format(mail.receivedDate)
        } else {
            fullFormat.format(mail.receivedDate)
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
        val sender: TextView = itemView.findViewById(
            R.id.sender
        )
        val date: TextView = itemView.findViewById(
            R.id.date
        )
        val subject: TextView = itemView.findViewById(
            R.id.subject
        )

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