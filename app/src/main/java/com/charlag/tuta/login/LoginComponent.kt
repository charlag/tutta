package com.charlag.tuta.login

import com.charlag.tuta.LoginActivity
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector

@Module
interface LoginModule : AndroidInjector<LoginActivity> {
    @ContributesAndroidInjector
    fun contributesLoginActivity(): LoginActivity
}

