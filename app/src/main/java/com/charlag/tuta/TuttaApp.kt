package com.charlag.tuta

import android.app.Application
import android.content.Intent
import android.os.Process
import android.util.Log
import com.charlag.tuta.di.DaggerAppComponent
import com.charlag.tuta.user.LoginController
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject
import kotlin.system.exitProcess

class TuttaApp : Application(), HasAndroidInjector {
    @Inject
    lateinit var appDispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var loginController: LoginController

    override fun onCreate() {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            val intent = ErrorHandlerActivity.newIntent(this, e).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val stacktrace = Log.getStackTraceString(e)
            Log.e("TuttaApp", stacktrace)
            startActivity(intent)
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }

        super.onCreate()
        DaggerAppComponent.factory().create(this).inject(this)
    }

    override fun androidInjector() = AndroidInjector<Any> { instance ->
        // We use this to user userComponent for authenticated activities
        val userComponent = loginController.userComponent
        if (instance is AuthenticatedActivity) {
            if (userComponent != null) {
                userComponent.androidInjector().inject(instance)
                Log.d("App", "Injected into AuthenticatedActivity $instance")
            } else {
                appDispatchingAndroidInjector.maybeInject(instance)
                instance.onNotAuthenticated()
                Log.d("App", "onNotAuthenticated into AuthenticatedActivity $instance")
            }
        } else {
            val injected = appDispatchingAndroidInjector.maybeInject(instance)
            Log.d("App", "Injected into other $instance $injected")
        }
    }
}