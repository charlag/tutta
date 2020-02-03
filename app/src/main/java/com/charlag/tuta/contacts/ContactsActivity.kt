package com.charlag.tuta.contacts

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.paging.PagedListAdapter
import androidx.paging.toLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.DependencyDump
import com.charlag.tuta.R
import com.charlag.tuta.data.ContactEntity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_contacts.*

class ContactsActivity : AppCompatActivity(R.layout.activity_contacts) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        val adapter = ContactsAdapter()
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        DependencyDump.contactRepository.loadContacts().toLiveData(40).observe(this) {
            adapter.submitList(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}


class ContactsAdapter : PagedListAdapter<ContactEntity, ContactsAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameLabel = itemView.findViewById<TextView>(R.id.nameLabel)

        init {
            itemView.setOnClickListener {
                Snackbar.make(itemView, "This is not implemented yet", Snackbar.LENGTH_SHORT).show()
            }
        }

        fun bind(contact: ContactEntity) {
            nameLabel.text = "${contact.firstName} ${contact.lastName}"
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ContactEntity>() {
            override fun areItemsTheSame(oldItem: ContactEntity, newItem: ContactEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ContactEntity,
                newItem: ContactEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}