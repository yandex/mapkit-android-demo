package com.yandex.navikitdemo.ui.settings.settingslist

import com.yandex.navikitdemo.domain.SettingModel
import com.yandex.navikitdemo.domain.SettingsManager
import javax.inject.Inject

class SettingsItemsMapper @Inject constructor(
    private val settingsManager: SettingsManager,
) {

    fun items(screen: SettingsScreen): List<SettingsItem> {
        return when (screen) {
            SettingsScreen.START -> listOf(
                SettingsItem.NextScreen(SettingsScreen.MAP),
                SettingsItem.NextScreen(SettingsScreen.CAMERA),
                SettingsItem.NextScreen(SettingsScreen.GUIDANCE),
                SettingsItem.NextScreen(SettingsScreen.SIMULATION),
                SettingsItem.SectionTitle("Events"),
                SettingsItem.NextScreen(SettingsScreen.ROAD_EVENTS),
                SettingsItem.NextScreen(SettingsScreen.SOUND_ANNOTATIONS),
                SettingsItem.SectionTitle("Options"),
                SettingsItem.NextScreen(SettingsScreen.DRIVING_OPTIONS),
                SettingsItem.NextScreen(SettingsScreen.VEHICLE_OPTIONS),
                SettingsItem.NextScreen(SettingsScreen.SMART_ROUTE),
            )
            SettingsScreen.VEHICLE_OPTIONS -> listOf(
                SettingsItem.CheckList(CheckListType.VEHICLE_TYPE),
                SettingsItem.EditFloat("Weight", settingsManager.weight),
                SettingsItem.EditFloat("Axle Weight", settingsManager.axleWeight),
                SettingsItem.EditFloat("Max weight", settingsManager.maxWeight),
                SettingsItem.EditFloat("Height", settingsManager.height),
                SettingsItem.EditFloat("Width", settingsManager.width),
                SettingsItem.EditFloat("Length", settingsManager.length),
                SettingsItem.EditFloat("Payload", settingsManager.payload),
                SettingsItem.CheckList(CheckListType.ECO_CLASS),
                SettingsItem.Toggle("Has trailer", settingsManager.hasTrailer),
                SettingsItem.Toggle("Busway Permitted", settingsManager.buswayPermitted),
            )
            SettingsScreen.ROAD_EVENTS -> listOf(
                SettingsItem.Toggle(
                    "Show Road Events On Route",
                    settingsManager.roadEventsOnRouteEnabled
                ),
                SettingsItem.NextScreen(SettingsScreen.ROAD_EVENTS_ON_ROUTE),
            )
            SettingsScreen.ROAD_EVENTS_ON_ROUTE -> buildList {
                addAll(settingsManager.roadEventsOnRoute.toItems())
            }
            SettingsScreen.SOUND_ANNOTATIONS -> listOf(
                SettingsItem.Toggle("Mute Annotations", settingsManager.muteAnnotations),
                SettingsItem.CheckList(CheckListType.ANNOTATION_LANGUAGE),
                SettingsItem.Toggle("Text Annotations", settingsManager.textAnnotations),
                SettingsItem.Details("Display annotations using Toast popup"),
                SettingsItem.NextScreen(SettingsScreen.ANNOTATED_EVENTS),
                SettingsItem.Details("Setting for each event whether it will be annotated or not")
            )
            SettingsScreen.ANNOTATED_EVENTS -> buildList {
                add(SettingsItem.SectionTitle("Annotated Events"))
                addAll(settingsManager.annotatedEvents.toItems())
                add(SettingsItem.SectionTitle("Annotated Events On Route"))
                addAll(settingsManager.annotatedRoadEvents.toItems())
            }
            SettingsScreen.DRIVING_OPTIONS -> listOf(
                SettingsItem.Toggle("Avoid Tolls Routes", settingsManager.avoidTolls),
                SettingsItem.Toggle("Avoid Unpaved Routes", settingsManager.avoidUnpaved),
                SettingsItem.Toggle(
                    "Avoid Poor Conditions Routes",
                    settingsManager.avoidPoorConditions
                ),
            )
            SettingsScreen.CAMERA -> listOf(
                SettingsItem.Toggle("Auto Camera", settingsManager.autoCamera),
                SettingsItem.Toggle("Auto Zoom", settingsManager.autoZoom),
                SettingsItem.Toggle("Auto Rotation", settingsManager.autoRotation),
                SettingsItem.EditFloat("Zoom Offset", settingsManager.zoomOffset),
            )
            SettingsScreen.MAP -> listOf(
                SettingsItem.CheckList(CheckListType.STYLE_MODE),
                SettingsItem.CheckList(CheckListType.JAMS),
                SettingsItem.Details("Defines routes that will be displayed with traffics jams"),
                SettingsItem.Toggle("Focus Rects Autoupdate", settingsManager.focusRectsAutoUpdate),
                SettingsItem.Details("Enables automatic recalculations of focus rects and points on different screens"),
                SettingsItem.Toggle("Balloons", settingsManager.balloons),
                SettingsItem.Toggle("Traffics Lights", settingsManager.trafficLight),
                SettingsItem.Toggle("Show Predicted", settingsManager.showPredicted),
                SettingsItem.Toggle("Balloons Geometry", settingsManager.balloonsGeometry),
            )
            SettingsScreen.SIMULATION -> listOf(
                SettingsItem.Toggle("Simulation", settingsManager.simulation),
                SettingsItem.Details("When enabled starts guidance simulation demo"),
                SettingsItem.EditFloat("Speed", settingsManager.simulationSpeed),
                SettingsItem.Details("Simulation speed in km/h"),
            )
            SettingsScreen.GUIDANCE -> listOf(
                SettingsItem.SpeedLimits,
                SettingsItem.EditFloat("Speed limit tolerance", settingsManager.speedLimitTolerance),
                SettingsItem.Details("It is responsible for the relative speed value at which annotations play"),
                SettingsItem.Toggle("Background Guidance", settingsManager.background),
                SettingsItem.Details("When enabled guidance will be not suspended after AppActivity.onPause"),
                SettingsItem.Toggle("Alternatives", settingsManager.alternatives),
                SettingsItem.Details("Enable/disable alternatives suggestions during a guidance"),
                SettingsItem.Toggle("Restore Guidance", settingsManager.restoreGuidanceState),
            )
            SettingsScreen.SMART_ROUTE -> listOf(
                SettingsItem.Toggle("Enable Smart Route Planning", settingsManager.smartRoutePlanningEnabled),
                SettingsItem.CheckList(CheckListType.CHARGING_TYPE),
                SettingsItem.MultipleCheckList(MultipleCheckListType.FUEL_CONNECTOR_TYPES),
                SettingsItem.EditFloat("Maximum travel distance (km)", settingsManager.maxTravelDistance),
                SettingsItem.EditFloat("Travel on the current level (km)", settingsManager.currentRangeLvl),
                SettingsItem.EditFloat("Threshold distance (km)", settingsManager.thresholdDistance),
            )
        }
    }

    private fun <T : Enum<T>> Map<T, SettingModel<Boolean>>.toItems(): List<SettingsItem> {
        return this.map { (tag, setting) ->
            SettingsItem.Toggle(tag.toString(), setting)
        }
    }
}
