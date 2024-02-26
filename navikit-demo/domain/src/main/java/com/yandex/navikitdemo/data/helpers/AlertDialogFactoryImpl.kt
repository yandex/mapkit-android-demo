package com.yandex.navikitdemo.data.helpers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.yandex.navikitdemo.domain.helpers.AlertDialogFactory
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AlertDialogFactoryImpl @Inject constructor(
    private val activity: Activity,
) : AlertDialogFactory {

    override fun locationPermissionDialog(): AlertDialog {
        return AlertDialog.Builder(activity)
            .setTitle("Open settings to provide locations permissions")
            .setPositiveButton("Ok") { _, _ ->
                activity.openAppSettings()
            }
            .setOnDismissListener {
                activity.finish()
            }
            .create()
    }

    override fun notificationPermissionDialog(): AlertDialog {
        return AlertDialog.Builder(activity)
            .setTitle("Open settings to provide notifications permissions")
            .setPositiveButton("Ok") { _, _ ->
                activity.openAppSettings()
            }
            .setOnDismissListener {
                activity.finish()
            }
            .create()
    }

    override fun requestToPointDialog(toClicked: () -> Unit): AlertDialog {
        return AlertDialog.Builder(activity)
            .setTitle("Build route to point?")
            .setPositiveButton("Ok") { _, _ -> toClicked() }
            .setNeutralButton("Cancel") { _, _ -> }
            .create()
    }

    override fun closeGuidanceDialog(onGuidanceClosed: () -> Unit): AlertDialog {
        return AlertDialog.Builder(activity)
            .setTitle("Cancel the route?")
            .setPositiveButton("Yes") { _, _ -> onGuidanceClosed() }
            .setNeutralButton("No") { _, _ -> }
            .create()
    }

    override fun requestPointDialog(
        toClicked: () -> Unit,
        viaClicked: () -> Unit,
        fromClicked: () -> Unit,
    ): AlertDialog {
        return AlertDialog.Builder(activity)
            .setTitle("Add point to the route?")
            .setPositiveButton("To") { _, _ -> toClicked() }
            .setNeutralButton("From") { _, _ -> fromClicked() }
            .setNegativeButton("Via") { _, _ -> viaClicked() }
            .create()
    }

    private fun Activity.openAppSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null),
            )
        )
    }
}
