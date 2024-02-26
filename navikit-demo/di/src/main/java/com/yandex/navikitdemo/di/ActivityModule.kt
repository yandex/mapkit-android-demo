package com.yandex.navikitdemo.di

import com.yandex.navikitdemo.data.AnnotationsManagerImpl
import com.yandex.navikitdemo.data.CameraManagerImpl
import com.yandex.navikitdemo.data.NavigationLayerManagerImpl
import com.yandex.navikitdemo.data.SpeakerImpl
import com.yandex.navikitdemo.data.helpers.AlertDialogFactoryImpl
import com.yandex.navikitdemo.data.helpers.MapTapManagerImpl
import com.yandex.navikitdemo.data.helpers.PermissionManagerImpl
import com.yandex.navikitdemo.data.helpers.SettingsBinderManagerImpl
import com.yandex.navikitdemo.domain.AnnotationsManager
import com.yandex.navikitdemo.domain.NavigationLayerManager
import com.yandex.navikitdemo.domain.SpeakerManager
import com.yandex.navikitdemo.domain.helpers.AlertDialogFactory
import com.yandex.navikitdemo.domain.helpers.MapTapManager
import com.yandex.navikitdemo.domain.helpers.PermissionManager
import com.yandex.navikitdemo.domain.helpers.SettingsBinderManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @Binds
    abstract fun alertDialogFactory(impl: AlertDialogFactoryImpl): AlertDialogFactory

    @Binds
    abstract fun mapTapManager(impl: MapTapManagerImpl): MapTapManager

    @Binds
    abstract fun permissionManager(impl: PermissionManagerImpl): PermissionManager

    @Binds
    abstract fun cameraManager(impl: CameraManagerImpl): com.yandex.navikitdemo.domain.CameraManager

    @Binds
    abstract fun navigationLayerManager(impl: NavigationLayerManagerImpl): NavigationLayerManager

    @Binds
    abstract fun settingsBinderManager(impl: SettingsBinderManagerImpl): SettingsBinderManager

    @Binds
    abstract fun speakerManager(impl: SpeakerImpl): SpeakerManager

    @Binds
    abstract fun annotationsManager(impl: AnnotationsManagerImpl): AnnotationsManager
}
