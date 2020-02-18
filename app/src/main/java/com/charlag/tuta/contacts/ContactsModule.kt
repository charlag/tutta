package com.charlag.tuta.contacts

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ContactsModule {
    @ContributesAndroidInjector
    fun contactsActivity(): ContactsActivity

    @ContributesAndroidInjector
    fun contactViewerActivity(): ContactViewerActivity
}