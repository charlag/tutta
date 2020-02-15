package com.charlag.tuta.contacts

import androidx.paging.DataSource
import com.charlag.tuta.UserController
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.data.ContactEntity
import com.charlag.tuta.data.toEntity
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.tutanota.Contact
import com.charlag.tuta.entities.tutanota.ContactList
import com.charlag.tuta.network.API
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException

class ContactsRepository(
    private val api: API,
    private val db: AppDatabase,
    private val userController: UserController
) {
    fun loadContacts(): DataSource.Factory<Int, ContactEntity> =
        db.contactDao().getContacts()

    fun findContacts(query: String): List<ContactEntity> =
        db.contactDao().findContacts(query)

    suspend fun getContact(id: String): ContactEntity? {
        return db.contactDao().getContact(id) ?: loadContact(id)
    }

    private suspend fun loadContact(id: String): ContactEntity? {
        return kotlin.runCatching {
            api.loadListElementEntity<Contact>(IdTuple(contactListId(), GeneratedId(id)))
        }.getOrElse { e ->
            if (e is ClientRequestException && e.response.status == HttpStatusCode.NotFound
                || e is IOException
            ) {
                return null
            } else {
                throw e
            }
        }.toEntity()
            .also { db.contactDao().insertContact(it) }
    }

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
        val user = userController.waitForLogin()
        val contactList = api.loadRoot(ContactList::class, user.userGroup.group)
        return contactList.contacts
    }
}