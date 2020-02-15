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
    @Inject
    lateinit var loginController: LoginController

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        if (loginController.userComponent == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}