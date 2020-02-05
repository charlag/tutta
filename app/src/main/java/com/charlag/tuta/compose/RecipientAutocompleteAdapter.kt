package com.charlag.tuta.compose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.charlag.tuta.R
import com.charlag.tuta.data.ContactEntity

data class ContactResult(val mailAddress: String, val contact: ContactEntity)

typealias ContactAutocompleteResult = List<ContactResult>
typealias ContactAutocompleteProvider = (String) -> ContactAutocompleteResult

class RecipientAutocompleteAdapter(
    private val resultProvider: ContactAutocompleteProvider
) : BaseAdapter(), Filterable {
    private val resultList = mutableListOf<ContactResult>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact_autocomplete, parent, false).apply {
                tag = AutocompelteViewHolder(this)
            }
        val viewHolder = view.tag as AutocompelteViewHolder
        val item = getItem(position)
        viewHolder.nameLabel.text = "${item.contact.firstName} ${item.contact.lastName}"
        viewHolder.addressLabel.text = item.mailAddress

        return view
    }

    override fun getItem(position: Int): ContactResult = resultList[position]

    override fun getItemId(position: Int): Long = getItem(position).mailAddress.hashCode().toLong()

    override fun getCount(): Int = resultList.size

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            // Done on background thread
            return if (constraint == null) {
                FilterResults()
            } else {
                val contacts = resultProvider(constraint.toString().trim())
                FilterResults().apply {
                    count = contacts.size
                    values = contacts
                }
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results != null && results.count > 0) {
                resultList.clear()
                @Suppress("UNCHECKED_CAST")
                resultList.addAll(results.values as List<ContactResult>)
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as ContactResult).mailAddress + " "
        }
    }

    private class AutocompelteViewHolder(itemView: View) {
        val nameLabel: TextView = itemView.findViewById(
            R.id.nameLabel
        )
        val addressLabel: TextView = itemView.findViewById(
            R.id.addressLabel
        )
    }
}