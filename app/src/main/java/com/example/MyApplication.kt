package com.example

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object AppState {
    var firebaseInitError: String? = null
}

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:979447256418:android:ea5b570266ae1aa6a9883b")
                .setApiKey("AIzaSyC9CvaM9Mw3NiD-KsOiYvHZHj6XZJQJnPs")
                .setProjectId("cinestream-sulo")
                .setStorageBucket("cinestream-sulo.firebasestorage.app")
                .build()
            FirebaseApp.initializeApp(this, options)
        } catch (e: Exception) {
            AppState.firebaseInitError = e.message
            e.printStackTrace()
        }
    }
}
