package com.charlag.tuta

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.charlag.tuta.user.LoginController
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class AuthenticatedActivity(@LayoutRes layoutId: Int) : AppCompatActivity(layoutId),
    HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    private var notAuthenticated = false

    final override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        if (!notAuthenticated) {
            onAuthenticatedCreate(savedInstanceState)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    fun onNotAuthenticated() {
        this.notAuthenticated = true
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    open fun onAuthenticatedCreate(savedInstanceState: Bundle?) {
    }
}