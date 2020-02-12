package com.charlag.tuta.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.ContactAddressType
import com.charlag.tuta.R
import com.charlag.tuta.entities.tutanota.ContactMailAddress

class MailAddressesAdapter : RecyclerView.Adapter<MailAddressesAdapter.ViewHolder>() {
    val mailAddresses = mutableListOf<ContactMailAddress>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon = itemView.findViewById<ImageView>(
            R.id.icon
        )
        private val typeLabel = itemView.findViewById<TextView>(
            R.id.typeLabel
        )
        private val contentLabel = itemView.findViewById<TextView>(
            R.id.contentLabel
        )

        fun bind(address: ContactMailAddress) {
            icon.setImageResource(R.drawable.ic_email_black_24dp)
            contentLabel.text = address.address
            typeLabel.text = address.typeLabel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact_item, parent, false)
            .let(::ViewHolder)
    }

    override fun getItemCount(): Int = mailAddresses.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mailAddresses[position])
    }
}

private val ContactMailAddress.typeLabel: String
    get() = when (this.type) {
        ContactAddressType.PRIVATE.raw -> "Private"
        ContactAddressType.WORK.raw -> "Work"
        ContactAddressType.CUSTOM.raw -> customTypeName
        else -> "Other"
    }