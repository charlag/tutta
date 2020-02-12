package com.charlag.tuta.contacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlag.tuta.DependencyDump
import com.charlag.tuta.R
import kotlinx.android.synthetic.main.activity_contact_viewer.*
import kotlinx.android.synthetic.main.item_contact.nameLabel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ContactViewerActivity : AppCompatActivity(R.layout.activity_contact_viewer) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contactId = intent.getStringExtra(CONTACT_ID_EXTRA)

        if (contactId == null) {
            finish()
            return
        }

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val mailAddressesAdapter = MailAddressesAdapter()
        addressesRecycler.layoutManager = LinearLayoutManager(this)
        addressesRecycler.adapter = mailAddressesAdapter

        lifecycleScope.launch {
            val contact = DependencyDump.contactRepository.getContact(contactId)
            if (contact == null) {
                finish()
                return@launch
            }
            nameLabel.text =
                contact.firstName + " " + (if (contact.nickname.isNullOrBlank()) "" else "'${contact.nickname}' ") + contact.lastName
            val birthday = contact.birthday
            if (birthday != null) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, birthday.day.toInt())
                cal.set(Calendar.MONTH, birthday.month.toInt() - 1)
                val year = birthday.year
                birthdayLabel.text = if (year != null) {
                    cal.set(Calendar.YEAR, year.toInt())
                    SimpleDateFormat("dd MMMM YYYY").format(cal.time)
                } else {
                    SimpleDateFormat("dd MMMM").format(cal.time)
                }
                birthdayIcon.visibility = View.VISIBLE
                birthdayLabel.visibility = View.VISIBLE
            }

            mailAddressesAdapter.mailAddresses.clear()
            mailAddressesAdapter.mailAddresses.addAll(contact.mailAddresses)
            mailAddressesAdapter.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val CONTACT_ID_EXTRA = "contactId"

        fun newIntent(context: Context, contactId: String): Intent {
            return Intent(context, ContactViewerActivity::class.java).apply {
                putExtra(CONTACT_ID_EXTRA, contactId)
            }
        }
    }
}