package com.yandex.navikitdemo.domain

import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.directions.driving.VehicleType
import com.yandex.mapkit.road_events.EventTag
import com.yandex.navikitdemo.domain.models.AnnotatedEventsType
import com.yandex.navikitdemo.domain.models.AnnotatedRoadEventsType
import com.yandex.navikitdemo.domain.models.EcoClass
import com.yandex.navikitdemo.domain.models.FuelConnectorType
import com.yandex.navikitdemo.domain.models.JamsMode
import com.yandex.navikitdemo.domain.models.StyleMode
import com.yandex.navikitdemo.domain.models.ChargingType
import kotlinx.coroutines.flow.Flow

interface SettingModel<T> {
    var value: T
    fun changes(): Flow<T>
}

interface SettingsManager {

    // Style
    val styleMode: SettingModel<StyleMode>

    // Vehicle options
    val vehicleType: SettingModel<VehicleType>
    val weight: SettingModel<Float>
    val axleWeight: SettingModel<Float>
    val maxWeight: SettingModel<Float>
    val height: SettingModel<Float>
    val width: SettingModel<Float>
    val length: SettingModel<Float>
    val payload: SettingModel<Float>
    val ecoClass: SettingModel<EcoClass>
    val hasTrailer: SettingModel<Boolean>
    val buswayPermitted: SettingModel<Boolean>

    // Road events
    val roadEventsOnRouteEnabled: SettingModel<Boolean>
    val roadEventsOnRoute: Map<EventTag, SettingModel<Boolean>>

    // Annotations
    val annotatedEvents: Map<AnnotatedEventsType, SettingModel<Boolean>>
    val annotatedRoadEvents: Map<AnnotatedRoadEventsType, SettingModel<Boolean>>
    val annotationLanguage: SettingModel<AnnotationLanguage>
    val muteAnnotations: SettingModel<Boolean>
    val textAnnotations: SettingModel<Boolean>

    // Driving Options
    val avoidTolls: SettingModel<Boolean>
    val avoidUnpaved: SettingModel<Boolean>
    val avoidPoorConditions: SettingModel<Boolean>

    // Navigation Layer
    val jamsMode: SettingModel<JamsMode>
    val balloons: SettingModel<Boolean>
    val trafficLight: SettingModel<Boolean>
    val showPredicted: SettingModel<Boolean>
    val balloonsGeometry: SettingModel<Boolean>
    val focusRectsAutoUpdate: SettingModel<Boolean>

    // Camera
    val autoZoom: SettingModel<Boolean>
    val autoRotation: SettingModel<Boolean>
    val autoCamera: SettingModel<Boolean>
    val zoomOffset: SettingModel<Float>

    // Guidance
    val alternatives: SettingModel<Boolean>
    val simulation: SettingModel<Boolean>
    val simulationSpeed: SettingModel<Float>
    val background: SettingModel<Boolean>
    val speedLimitTolerance: SettingModel<Float>
    val restoreGuidanceState: SettingModel<Boolean>
    val serializedNavigation: SettingModel<String>

    // Smart Route Planning Options
    val smartRoutePlanningEnabled: SettingModel<Boolean>
    val chargingType: SettingModel<ChargingType>
    val fuelConnectorTypes: SettingModel<Set<FuelConnectorType>>
    val maxTravelDistance: SettingModel<Float>
    val currentRangeLvl: SettingModel<Float>
    val thresholdDistance: SettingModel<Float>
}
