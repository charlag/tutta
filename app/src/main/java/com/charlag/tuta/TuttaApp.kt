package com.charlag.tuta

import android.app.Application
import com.charlag.tuta.di.DaggerAppComponent
import com.charlag.tuta.user.LoginController
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class TuttaApp : Application(), HasAndroidInjector {
    @Inject
    lateinit var appDispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var loginController: LoginController

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.factory().create(this).inject(this)
    }

    override fun androidInjector() = AndroidInjector<Any> { instance ->
        // We use this to user userComponent for authenticated activities
        val userComponent = loginController.userComponent
        if (instance is AuthenticatedActivity && userComponent != null) {
            userComponent.androidInjector().maybeInject(instance)
                    || appDispatchingAndroidInjector.maybeInject(instance)
        } else {
            appDispatchingAndroidInjector.maybeInject(instance)
        }
    }
}