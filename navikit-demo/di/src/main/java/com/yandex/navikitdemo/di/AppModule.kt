package com.yandex.navikitdemo.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.navikitdemo.data.AnnotationsManagerImpl
import com.yandex.navikitdemo.data.LocationManagerImpl
import com.yandex.navikitdemo.data.NavigationHolderImpl
import com.yandex.navikitdemo.data.NavigationManagerImpl
import com.yandex.navikitdemo.data.NavigationStyleManagerImpl
import com.yandex.navikitdemo.data.RequestPointsManagerImpl
import com.yandex.navikitdemo.data.SettingsManagerImpl
import com.yandex.navikitdemo.data.SimulationManagerImpl
import com.yandex.navikitdemo.data.smartroute.SmartRouteSearchFactoryImpl
import com.yandex.navikitdemo.data.SpeakerImpl
import com.yandex.navikitdemo.data.VehicleOptionsManagerImpl
import com.yandex.navikitdemo.data.helpers.BackgroundServiceManagerImpl
import com.yandex.navikitdemo.data.helpers.KeyValueStorageImpl
import com.yandex.navikitdemo.data.helpers.NavigationDeserializerImpl
import com.yandex.navikitdemo.data.helpers.NavigationFactoryImpl
import com.yandex.navikitdemo.data.helpers.NavigationSuspenderManagerImpl
import com.yandex.navikitdemo.data.smartroute.SmartRoutePlanningFactoryImpl
import com.yandex.navikitdemo.domain.AnnotationsManager
import com.yandex.navikitdemo.domain.LocationManager
import com.yandex.navikitdemo.domain.NavigationHolder
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.NavigationStyleManager
import com.yandex.navikitdemo.domain.RequestPointsManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SimulationManager
import com.yandex.navikitdemo.domain.SpeakerManager
import com.yandex.navikitdemo.domain.VehicleOptionsManager
import com.yandex.navikitdemo.domain.helpers.BackgroundServiceManager
import com.yandex.navikitdemo.domain.helpers.KeyValueStorage
import com.yandex.navikitdemo.domain.helpers.NavigationDeserializer
import com.yandex.navikitdemo.domain.helpers.NavigationFactory
import com.yandex.navikitdemo.domain.helpers.NavigationSuspenderManager
import com.yandex.navikitdemo.domain.smartroute.SmartRoutePlanningFactory
import com.yandex.navikitdemo.domain.smartroute.SmartRouteSearchFactory
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
    abstract fun smartRoutePlanningFactory(impl: SmartRoutePlanningFactoryImpl): SmartRoutePlanningFactory

    @Binds
    abstract fun smartRouteSearchFactory(impl: SmartRouteSearchFactoryImpl): SmartRouteSearchFactory

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

    @Binds
    abstract fun navigationDeserializer(impl: NavigationDeserializerImpl): NavigationDeserializer

    @Binds
    abstract fun navigationHolder(impl: NavigationHolderImpl): NavigationHolder

    @Binds
    abstract fun speakerManager(impl: SpeakerImpl): SpeakerManager

    @Binds
    abstract fun annotationsManager(impl: AnnotationsManagerImpl): AnnotationsManager

    companion object {

        @Singleton
        @Provides
        fun notificationManager(
            application: Application,
        ): NotificationManager {
            return application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        @Provides
        fun drivingRouter(): DrivingRouter {
            return DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
        }

        @Provides
        fun searchManager(): SearchManager {
            return SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        }

    }
}
