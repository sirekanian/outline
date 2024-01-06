package org.sirekanyan.outline.ext

import android.content.Context
import android.content.pm.PackageManager.GET_ACTIVITIES
import android.content.pm.PackageManager.NameNotFoundException

private const val OUTLINE_PACKAGE = "org.outline.android.client"
private const val OUTLINE_PLAY_LINK = "https://play.google.com/store/apps/details?id=$OUTLINE_PACKAGE"

fun isOutlineInstalled(context: Context): Boolean =
    try {
        context.packageManager.getPackageInfo(OUTLINE_PACKAGE, GET_ACTIVITIES)
        true
    } catch (exception: NameNotFoundException) {
        false
    }

fun openOutline(context: Context) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(OUTLINE_PACKAGE)
        if (intent == null) {
            installOutline(context)
        } else {
            context.startActivity(intent)
        }
    } catch (exception: Exception) {
        logDebug("Cannot open Outline", exception)
        installOutline(context)
    }
}

fun installOutline(context: Context) {
    context.openGooglePlay(OUTLINE_PLAY_LINK)
}
