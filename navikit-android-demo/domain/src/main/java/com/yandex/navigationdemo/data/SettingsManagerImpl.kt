package com.yandex.navigationdemo.data

import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.directions.driving.VehicleType
import com.yandex.mapkit.road_events.EventTag
import com.yandex.navigationdemo.domain.SettingModel
import com.yandex.navigationdemo.domain.SettingsManager
import com.yandex.navigationdemo.domain.helpers.KeyValueStorage
import com.yandex.navigationdemo.domain.models.AnnotatedEventsType
import com.yandex.navigationdemo.domain.models.AnnotatedRoadEventsType
import com.yandex.navigationdemo.domain.models.EcoClass
import com.yandex.navigationdemo.domain.models.JamsMode
import com.yandex.navigationdemo.domain.models.StyleMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManagerImpl @Inject constructor(
    private val keyValueStorage: KeyValueStorage,
    defaultAnnotationsLanguage: AnnotationLanguage,
) : SettingsManager {

    // Style
    override val styleMode = enum("styleMode", StyleMode.SYSTEM, StyleMode::class.java)

    // Vehicle options
    override val vehicleType = enum("vehicleType", VehicleType.DEFAULT, VehicleType::class.java)
    override val weight = float("weight", 10f)
    override val axleWeight = float("axleWeight", 5f)
    override val maxWeight = float("maxWeight", 10f)
    override val height = float("height", 3.5f)
    override val width = float("width", 2.2f)
    override val length = float("length", 8.0f)
    override val payload = float("payload", 7f)
    override val ecoClass = enum("ecoClass", EcoClass.EURO_4, EcoClass::class.java)
    override val hasTrailer = boolean("hasTrailer", false)
    override val buswayPermitted = boolean("buswayPermitted", false)

    // Road events
    override val roadEventsOnRouteEnabled = boolean("roadEventsOnRouteEnabled", true)
    override val roadEventsOnRoute = createRoadEventsSettings("roadEventsOnRoute")

    // Annotations
    override val annotatedEvents = buildMap {
        AnnotatedEventsType.values().forEach {
            this[it] = boolean("annotatedEvents_$it", true)
        }
    }
    override val annotatedRoadEvents = buildMap {
        AnnotatedRoadEventsType.values().forEach {
            this[it] = boolean("annotatedRoadEvents_$it", true)
        }
    }
    override val annotationLanguage =
        enum("annotationLanguage", defaultAnnotationsLanguage, AnnotationLanguage::class.java)
    override val muteAnnotations = boolean("muteAnnotations", false)
    override val textAnnotations = boolean("textAnnotations", true)

    // Driving Options
    override val avoidTolls = boolean("avoidTolls", false)
    override val avoidUnpaved = boolean("avoidUnpaved", false)
    override val avoidPoorConditions = boolean("avoidPoorConditions", false)

    // Navigation Layer
    override val jamsMode =
        enum("jamsMode", JamsMode.ENABLED_FOR_CURRENT_ROUTE, JamsMode::class.java)
    override val balloons = boolean("balloons", true)
    override val trafficLight = boolean("trafficLight", true)
    override val showPredicted = boolean("showPredicted", false)
    override val balloonsGeometry = boolean("balloonsGeometry", false)
    override val focusRectsAutoUpdate = boolean("focusRectsAutoUpdate", true)

    // Camera
    override val autoZoom = boolean("autoZoom", true)
    override val autoRotation = boolean("autoRotation", true)
    override val autoCamera = boolean("autoCamera", true)
    override val zoomOffset = float("zoomOffset", 0f)

    // Guidance
    override val alternatives = boolean("alternatives", true)
    override val simulation = boolean("simulation", true)
    override val simulationSpeed = float("simulationSpeed", 20f)
    override val background = boolean("background", true)
    override val speedLimitTolerance = float("speedLimitTolerance", 0.8f)
    override val restoreGuidanceState = boolean("restoreGuidanceState", true)
    override val serializedNavigation = string("serializedNavigation")

    private fun createRoadEventsSettings(baseKey: String): Map<EventTag, SettingModel<Boolean>> {
        return buildMap {
            EventTag.values().forEach {
                this[it] = boolean("${baseKey}_$it", true)
            }
        }
    }

    private fun float(key: String, default: Float): SettingModel<Float> {
        return object : SettingModel<Float> {
            private var valueImpl = MutableStateFlow(value)

            override var value: Float
                get() = keyValueStorage.readFloat(key, default)
                set(value) {
                    keyValueStorage.putFloat(key, value)
                    valueImpl.value = value
                }

            override fun changes(): Flow<Float> = valueImpl
        }
    }

    private fun <T : Enum<T>> enum(key: String, default: T, classItem: Class<T>): SettingModel<T> {
        return object : SettingModel<T> {
            private var valueImpl = MutableStateFlow(value)

            override var value: T
                get() = keyValueStorage.readEnum(key, default, classItem)
                set(value) {
                    keyValueStorage.putEnum(key, value)
                    valueImpl.value = value
                }

            override fun changes(): Flow<T> = valueImpl
        }
    }

    private fun boolean(key: String, default: Boolean): SettingModel<Boolean> {
        return object : SettingModel<Boolean> {
            private var valueImpl = MutableStateFlow(value)

            override var value: Boolean
                get() = keyValueStorage.readBoolean(key, default)
                set(value) {
                    keyValueStorage.putBoolean(key, value)
                    valueImpl.value = value
                }

            override fun changes(): Flow<Boolean> = valueImpl
        }
    }

    private fun string(key: String, default: String = ""): SettingModel<String> {
        return object : SettingModel<String> {
            private var valueImpl = MutableStateFlow(value)

            override var value: String
                get() = keyValueStorage.readString(key, default)
                set(value) {
                    keyValueStorage.putString(key, value)
                    valueImpl.value = value
                }

            override fun changes(): Flow<String> = valueImpl
        }
    }
}
