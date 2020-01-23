package com.charlag.tuta.mail

import android.content.Context
import android.graphics.Typeface
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.R
import com.charlag.tuta.data.MailEntity
import java.text.SimpleDateFormat
import java.util.*

class MailsAdapter(
    val onSelected: (MailEntity) -> Unit
) : RecyclerView.Adapter<MailsAdapter.MailviewHolder>() {
    val mails = mutableListOf<MailEntity>()
    lateinit var selectionTracker: SelectionTracker<String>

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): MailviewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mail, parent, false)
        return MailviewHolder(view)
    }

    override fun getItemCount(): Int = mails.size

    override fun onBindViewHolder(holder: MailviewHolder, index: Int) {
        val mail = mails[index]
        holder.sender.text =
            if (mail.sender.name.isNotBlank()) "${mail.sender.name} ${mail.sender.address}"
            else mail.sender.address
        holder.subject.text = mail.subject
        holder.date.text = formatDate(holder.date.context, mail)
        holder.itemView.setOnClickListener { onSelected(mail) }
        holder.subject.setTypeface(null, if (mail.unread) Typeface.BOLD else Typeface.NORMAL)
        if (selectionTracker.isSelected(mail.id.asString())) {
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
            SimpleDateFormat(
                "dd/MM",
                Locale.getDefault()
            ).format(mail.receivedDate)
        } else {
            DateFormat.getDateFormat(context).format(mail.receivedDate)
        }
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
            MailItemDetails(
                mails[adapterPosition].id.asString(),
                adapterPosition
            )
    }


    class MailItemDetails(
        val key: String,
        val adapterPosition: Int
    ) : ItemDetailsLookup.ItemDetails<String>() {
        override fun getSelectionKey(): String = key

        override fun getPosition(): Int = adapterPosition
    }
}