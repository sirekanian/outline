package org.sirekanyan.outline

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize

object CrashReporter {

    fun init(app: App) {
        Firebase.initialize(app)
    }

    fun handleException(throwable: Throwable) {
        Firebase.crashlytics.recordException(throwable)
    }

}