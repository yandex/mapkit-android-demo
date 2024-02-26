package com.yandex.navikitdemo.ui.guidance.views.maneuver

import android.content.Context
import androidx.annotation.DrawableRes
import com.yandex.mapkit.directions.driving.Action
import com.yandex.mapkit.directions.driving.LaneDirection
import com.yandex.mapkit.directions.driving.LaneKind

interface UpcomingManeuverResourcesProvider {
    fun getLaneKindResources(laneKind: LaneKind, context: Context): Pair<Int, Int>?
    fun getSmallResourceForLaneDirection(direction: LaneDirection): Int
    fun getLargeResourceForLaneDirection(direction: LaneDirection): Int

    enum class DimensionType {
        SMALL_OVERLAP,
        LARGE_OVERLAP,
        INDENT,
        WIDTH,
        HEIGHT,
    }

    fun getLaneItemDimension(dimension: DimensionType): Int

    @DrawableRes
    fun getManeuverResource(action: Action): Int?
}
