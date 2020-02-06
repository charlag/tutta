package com.charlag.tuta.contacts

import androidx.paging.DataSource
import com.charlag.tuta.network.API
import com.charlag.tuta.LoginFacade
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.data.ContactEntity
import com.charlag.tuta.data.toEntity
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.tutanota.Contact
import com.charlag.tuta.entities.tutanota.ContactList

class ContactsRepository(
    private val api: API,
    private val db: AppDatabase,
    private val loginFacade: LoginFacade
) {
    fun loadContacts(): DataSource.Factory<Int, ContactEntity> =
        db.contactDao().getContacts()

    fun findContacts(query: String): List<ContactEntity> =
        db.contactDao().findContacts(query)

    suspend fun ignite() {
        val contactNumber = db.contactDao().countContacts()
        if (contactNumber > 0) {
            return
        } else {
            val contacts = api.loadAll(Contact::class, contactListId())
            db.contactDao().insertContacts(contacts.map { it.toEntity() })
        }
    }

    suspend fun contactListId(): Id {
        val contactList = api.loadRoot(ContactList::class, loginFacade.user!!.userGroup.group)
        return contactList.contacts
    }
}