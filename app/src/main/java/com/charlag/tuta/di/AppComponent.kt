package com.charlag.tuta.di

import android.content.Context
import com.charlag.tuta.TuttaApp
import com.charlag.tuta.mail.MailModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        AndroidSupportInjectionModule::class,
        MailModule::class
    ]
)
interface AppComponent : AndroidInjector<TuttaApp> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }
}