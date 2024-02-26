package com.yandex.navikitdemo.domain.helpers

import androidx.appcompat.app.AlertDialog

interface AlertDialogFactory {
    fun locationPermissionDialog(): AlertDialog
    fun notificationPermissionDialog(): AlertDialog
    fun requestPointDialog(
        toClicked: () -> Unit,
        viaClicked: () -> Unit,
        fromClicked: () -> Unit,
    ): AlertDialog

    fun requestToPointDialog(toClicked: () -> Unit): AlertDialog
    fun closeGuidanceDialog(onGuidanceClosed: () -> Unit): AlertDialog
}
