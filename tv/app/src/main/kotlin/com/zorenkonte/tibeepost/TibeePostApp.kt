package com.zorenkonte.tibeepost

import android.app.Application
import com.zorenkonte.tibeepost.service.ServiceNotification

class TibeePostApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceNotification.ensureChannel(this)
    }
}
