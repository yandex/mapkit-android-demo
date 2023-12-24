package com.yandex.navigationdemo.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.yandex.navigationdemo.data.LocationManagerImpl
import com.yandex.navigationdemo.data.NavigationManagerImpl
import com.yandex.navigationdemo.data.NavigationStyleManagerImpl
import com.yandex.navigationdemo.data.RequestPointsManagerImpl
import com.yandex.navigationdemo.data.SettingsManagerImpl
import com.yandex.navigationdemo.data.SimulationManagerImpl
import com.yandex.navigationdemo.data.VehicleOptionsManagerImpl
import com.yandex.navigationdemo.data.helpers.BackgroundServiceManagerImpl
import com.yandex.navigationdemo.data.helpers.KeyValueStorageImpl
import com.yandex.navigationdemo.data.helpers.NavigationFactoryImpl
import com.yandex.navigationdemo.data.helpers.NavigationSuspenderManagerImpl
import com.yandex.navigationdemo.domain.LocationManager
import com.yandex.navigationdemo.domain.NavigationManager
import com.yandex.navigationdemo.domain.NavigationStyleManager
import com.yandex.navigationdemo.domain.RequestPointsManager
import com.yandex.navigationdemo.domain.SettingsManager
import com.yandex.navigationdemo.domain.SimulationManager
import com.yandex.navigationdemo.domain.VehicleOptionsManager
import com.yandex.navigationdemo.domain.helpers.BackgroundServiceManager
import com.yandex.navigationdemo.domain.helpers.KeyValueStorage
import com.yandex.navigationdemo.domain.helpers.NavigationFactory
import com.yandex.navigationdemo.domain.helpers.NavigationSuspenderManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun navigationStyleManager(impl: NavigationStyleManagerImpl): NavigationStyleManager

    @Binds
    abstract fun backgroundServiceManager(impl: BackgroundServiceManagerImpl): BackgroundServiceManager

    @Binds
    abstract fun keyValueStorage(impl: KeyValueStorageImpl): KeyValueStorage

    @Binds
    abstract fun locationManager(impl: LocationManagerImpl): LocationManager

    @Binds
    abstract fun navigationManager(impl: NavigationManagerImpl): NavigationManager

    @Binds
    abstract fun requestPointsManager(impl: RequestPointsManagerImpl): RequestPointsManager

    @Binds
    abstract fun settingsManager(impl: SettingsManagerImpl): SettingsManager

    @Binds
    abstract fun simulationManager(impl: SimulationManagerImpl): SimulationManager

    @Binds
    abstract fun vehicleOptionsManager(impl: VehicleOptionsManagerImpl): VehicleOptionsManager

    @Binds
    abstract fun navigationFactory(impl: NavigationFactoryImpl): NavigationFactory

    @Binds
    abstract fun navigationSuspenderManger(impl: NavigationSuspenderManagerImpl): NavigationSuspenderManager

    companion object {
        @Singleton
        @Provides
        fun notificationManager(
            application: Application,
        ): NotificationManager {
            return application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }
}
