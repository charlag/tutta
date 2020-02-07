package com.charlag.tuta.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.charlag.tuta.compose.LocalDraftEntity

@Database(
    entities = [
        MailEntity::class, MailFolderEntity::class, MailBodyEntity::class,
        ContactEntity::class, LocalDraftEntity::class, MailFolderCounterEntity::class
    ],
    version = 6
)
@TypeConverters(TutanotaConverters::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mailDao(): MailDao
    abstract fun contactDao(): ContactDao
}