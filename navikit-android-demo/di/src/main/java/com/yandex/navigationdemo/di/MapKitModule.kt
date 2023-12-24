package com.yandex.navigationdemo.di

import com.yandex.mapkit.navigation.automotive.Annotator
import com.yandex.mapkit.navigation.automotive.Guidance
import com.yandex.mapkit.navigation.automotive.Navigation
import com.yandex.navigationdemo.domain.helpers.NavigationFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapKitModule {

    companion object {
        @Provides
        @Singleton
        fun navigation(factory: NavigationFactory): Navigation = factory.create()

        @Provides
        @Singleton
        fun guidance(navigation: Navigation): Guidance = navigation.guidance

        @Provides
        @Singleton
        fun annotator(guidance: Guidance): Annotator = guidance.annotator
    }
}
