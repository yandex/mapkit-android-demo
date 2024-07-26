package com.yandex.navikitdemo.data.helpers

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import androidx.core.app.ActivityCompat
import com.yandex.navikitdemo.domain.helpers.AlertDialogFactory
import com.yandex.navikitdemo.domain.helpers.Permission
import com.yandex.navikitdemo.domain.helpers.PermissionManager
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

private const val REQUEST_PERMISSIONS = 1

@ActivityScoped
class PermissionManagerImpl @Inject constructor(
    private val activity: Activity,
    private val alertDialogFactory: AlertDialogFactory,
) : PermissionManager {

    override fun request(permissions: List<Permission>) {
        permissions
            .filter { !it.isGranted() && it.isRequestRationale() }
            .forEach {
                when (it) {
                    Permission.LOCATION -> alertDialogFactory.locationPermissionDialog().show()
                    Permission.NOTIFICATIONS -> alertDialogFactory.notificationPermissionDialog()
                        .show()
                }
            }

        val permissionsForRequest = permissions
            .filter { !it.isGranted() && !it.isRequestRationale() }
            .mapNotNull { it.toName() }
            .toTypedArray()

        if (permissionsForRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsForRequest,
                REQUEST_PERMISSIONS,
            )
        }
    }

    private fun Permission.toName(): String? = when (this) {
        Permission.LOCATION -> android.Manifest.permission.ACCESS_FINE_LOCATION
        Permission.NOTIFICATIONS -> if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }
    }

    private fun Permission.isGranted(): Boolean {
        val name = this.toName() ?: return true
        return ActivityCompat.checkSelfPermission(
            activity, name
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun Permission.isRequestRationale(): Boolean {
        val name = toName() ?: return true
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, name)
    }
}
