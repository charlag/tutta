package com.charlag.tuta.di

import android.content.Context
import com.charlag.tuta.TuttaApp
import com.charlag.tuta.login.LoginModule
import com.charlag.tuta.user.UserComponent
import com.charlag.tuta.user.UserModule
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
        LoginModule::class
    ]
)
interface AppComponent : AndroidInjector<TuttaApp> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun userComponent(userModule: UserModule): UserComponent
}