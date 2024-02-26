package com.yandex.navikitdemo.di

import android.app.Activity
import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.navigation.automotive.layer.styling.AutomotiveNavigationStyleProvider
import com.yandex.mapkit.navigation.automotive.layer.styling.NavigationStyleProvider
import com.yandex.mapkit.road_events_layer.StyleProvider
import com.yandex.mapkit.styling.roadevents.RoadEventsLayerDefaultStyleProvider
import com.yandex.navikitdemo.AppActivity
import com.yandex.navikitdemo.ui.guidance.views.maneuver.UpcomingManeuverResourcesProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityIntegrationModule {

    companion object {

        @Provides
        @ActivityScoped
        fun mapWindow(activity: Activity): MapWindow = (activity as AppActivity).mapWindow

        @Provides
        @ActivityScoped
        fun coroutineScope(activity: Activity): CoroutineScope =
            (activity as AppActivity).lifecycleScope
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppIntegrationModule {

    @Binds
    abstract fun upcomingManeuverResourcesProvider(impl: UpcomingManeuverResourcesProviderImpl): UpcomingManeuverResourcesProvider

    companion object {
        @Provides
        @Singleton
        fun navigationStyleProvider(@ApplicationContext context: Context): NavigationStyleProvider =
            AutomotiveNavigationStyleProvider(context)

        @Provides
        @Singleton
        fun roadEventsStyleProvider(@ApplicationContext context: Context): StyleProvider =
            RoadEventsLayerDefaultStyleProvider(context)

        @Provides
        @Singleton
        fun defaultAnnotationsLanguage() = AnnotationLanguage.ENGLISH
    }
}
