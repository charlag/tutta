package com.charlag.tuta

import android.content.Context
import android.graphics.Typeface
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.entities.tutanota.Mail
import java.text.SimpleDateFormat
import java.util.*

class MailsAdapter(
    val onSelected: (Mail) -> Unit
) : RecyclerView.Adapter<MailsAdapter.MailviewHolder>() {
    val mails = mutableListOf<Mail>()

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
    }

    private fun fromThisYear(mail: Mail): Boolean {
        val cal = Calendar.getInstance()
        val yearNow = cal.get(Calendar.YEAR)
        cal.timeInMillis = mail.receivedDate.millis
        return yearNow == cal.get(Calendar.YEAR)
    }

    private fun formatDate(context: Context, mail: Mail): String {
        val date = Date(mail.receivedDate.millis)
        return if (fromThisYear(mail)) {
            SimpleDateFormat(
                "dd/MM",
                Locale.getDefault()
            ).format(date)
        } else {
            DateFormat.getDateFormat(context).format(date)
        }
    }

    class MailviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sender: TextView = itemView.findViewById(
            R.id.sender
        )
        val date: TextView = itemView.findViewById(
            R.id.date
        )
        val subject: TextView = itemView.findViewById(
            R.id.subject
        )
    }
}