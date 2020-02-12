package com.charlag.tuta.data

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contact: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContacts(contacts: List<ContactEntity>)

    @Query("SELECT * FROM ContactEntity WHERE id = :id LIMIT 1")
    suspend fun getContact(id: String): ContactEntity?

    @Query("SELECT * FROM ContactEntity WHERE mailAddresses LIKE '%' || :query || '%' OR firstName LIKE '%' || :query || '%' or lastName LIKE '%' || :query || '%' LIMIT 20")
    fun findContacts(query: String): List<ContactEntity>

    @Query("SELECT Count(*) from ContactEntity")
    suspend fun countContacts(): Int

    @Query("SELECT * FROM ContactEntity ORDER BY firstName ASC, lastName DESC")
    fun getContacts(): DataSource.Factory<Int, ContactEntity>
}