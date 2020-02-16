package com.charlag.tuta.settings

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SettingsModule {
    @ContributesAndroidInjector
    fun settingsActivity(): SettingsActivity
}