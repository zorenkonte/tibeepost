package com.zorenkonte.tibeepost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.zorenkonte.tibeepost.service.ServiceStarter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceStarter.start(this)
        setContent { }
    }
}
