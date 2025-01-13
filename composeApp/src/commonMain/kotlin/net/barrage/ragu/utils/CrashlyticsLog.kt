package net.barrage.ragu.utils

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

object CrashlyticsLog {
    fun log(message: String) {
        Firebase.crashlytics.log(message)
    }

    fun logException(exception: Exception) {
        Firebase.crashlytics.recordException(exception)
    }
}