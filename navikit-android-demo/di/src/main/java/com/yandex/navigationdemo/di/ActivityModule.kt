package com.yandex.navigationdemo.di

import com.yandex.navigationdemo.data.AnnotationsManagerImpl
import com.yandex.navigationdemo.data.CameraManagerImpl
import com.yandex.navigationdemo.data.NavigationLayerManagerImpl
import com.yandex.navigationdemo.data.SpeakerImpl
import com.yandex.navigationdemo.data.helpers.AlertDialogFactoryImpl
import com.yandex.navigationdemo.data.helpers.MapTapManagerImpl
import com.yandex.navigationdemo.data.helpers.PermissionManagerImpl
import com.yandex.navigationdemo.data.helpers.SettingsBinderManagerImpl
import com.yandex.navigationdemo.domain.AnnotationsManager
import com.yandex.navigationdemo.domain.NavigationLayerManager
import com.yandex.navigationdemo.domain.SpeakerManager
import com.yandex.navigationdemo.domain.helpers.AlertDialogFactory
import com.yandex.navigationdemo.domain.helpers.MapTapManager
import com.yandex.navigationdemo.domain.helpers.PermissionManager
import com.yandex.navigationdemo.domain.helpers.SettingsBinderManager
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
    abstract fun cameraManager(impl: CameraManagerImpl): com.yandex.navigationdemo.domain.CameraManager

    @Binds
    abstract fun navigationLayerManager(impl: NavigationLayerManagerImpl): NavigationLayerManager

    @Binds
    abstract fun settingsBinderManager(impl: SettingsBinderManagerImpl): SettingsBinderManager

    @Binds
    abstract fun speakerManager(impl: SpeakerImpl): SpeakerManager

    @Binds
    abstract fun annotationsManager(impl: AnnotationsManagerImpl): AnnotationsManager
}
