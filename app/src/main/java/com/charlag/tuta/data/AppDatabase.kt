package com.charlag.tuta.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MailEntity::class], version = 1)
@TypeConverters(TutanotaConverters::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mailDao(): MailDao
}