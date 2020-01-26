package com.charlag.tuta

import android.app.Application

@Suppress("unused")
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyDump.ignite(this)
    }
}