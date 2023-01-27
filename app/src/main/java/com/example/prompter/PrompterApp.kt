package com.example.prompter

import android.app.Application

import timber.log.Timber

class PrompterApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}