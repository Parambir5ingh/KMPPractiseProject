package org.prm.drica

import android.app.Application

/*
* Created by parambirsingh ON 27/01/26
*/class AndroidApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextProvider.context = applicationContext
    }
}