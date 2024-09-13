package com.yandex.navikitdemo.di

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.os.ConfigurationCompat
import com.yandex.mapkit.directions.driving.Action
import com.yandex.mapkit.directions.driving.LaneDirection
import com.yandex.mapkit.directions.driving.LaneKind
import com.yandex.navikitdemo.R
import com.yandex.navikitdemo.ui.guidance.views.maneuver.UpcomingManeuverResourcesProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpcomingManeuverResourcesProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
): UpcomingManeuverResourcesProvider {

    override fun getLaneKindResources(laneKind: LaneKind, context: Context): Pair<Int, Int>? {
        return when (laneKind) {
            LaneKind.PLAIN_LANE -> null
            LaneKind.BUS_LANE -> {
                val configuration = context.resources.configuration
                val locale = ConfigurationCompat.getLocales(configuration)[0]
                val country = locale?.country ?: return null

                if (CYRILLIC_COUNTRIES.contains(country)) {
                    Pair(R.drawable.mapkit_styling_automotive_context_lane_bus_ru, R.drawable.mapkit_styling_automotive_context_lane_bus_template_ru)
                } else Pair(R.drawable.mapkit_styling_automotive_context_lane_bus_en, R.drawable.mapkit_styling_automotive_context_lane_bus_template_en)
            }
            LaneKind.TRAM_LANE -> Pair(R.drawable.mapkit_styling_automotive_context_lane_tram, R.drawable.mapkit_styling_automotive_context_lane_tram_template)
            LaneKind.TAXI_LANE -> Pair(R.drawable.mapkit_styling_automotive_context_lane_taxi, R.drawable.mapkit_styling_automotive_context_lane_taxi_template)
            LaneKind.BIKE_LANE -> Pair(R.drawable.mapkit_styling_automotive_context_lane_bike, R.drawable.mapkit_styling_automotive_context_lane_bike_template)
            LaneKind.UNKNOWN_KIND -> throw IllegalStateException("Unexpected value: $laneKind")
        }
    }

    override fun getSmallResourceForLaneDirection(direction: LaneDirection): Int {
        return when (direction) {
            LaneDirection.LEFT180 -> R.drawable.mapkit_styling_automotive_context_lane_left180_small
            LaneDirection.LEFT135 -> R.drawable.mapkit_styling_automotive_context_lane_left135_small
            LaneDirection.LEFT90 -> R.drawable.mapkit_styling_automotive_context_lane_left90_small
            LaneDirection.LEFT45 -> R.drawable.mapkit_styling_automotive_context_lane_left45_small
            LaneDirection.STRAIGHT_AHEAD -> R.drawable.mapkit_styling_automotive_context_lane_straightahead_small
            LaneDirection.RIGHT45 -> R.drawable.mapkit_styling_automotive_context_lane_right45_small
            LaneDirection.RIGHT90 -> R.drawable.mapkit_styling_automotive_context_lane_right90_small
            LaneDirection.RIGHT135 -> R.drawable.mapkit_styling_automotive_context_lane_right135_small
            LaneDirection.RIGHT180 -> R.drawable.mapkit_styling_automotive_context_lane_right180_small
            LaneDirection.LEFT_FROM_RIGHT -> R.drawable.mapkit_styling_automotive_context_lane_leftfromright_small
            LaneDirection.RIGHT_FROM_LEFT -> R.drawable.mapkit_styling_automotive_context_lane_rightfromleft_small
            LaneDirection.LEFT_SHIFT -> R.drawable.mapkit_styling_automotive_context_lane_leftshift_small
            LaneDirection.RIGHT_SHIFT -> R.drawable.mapkit_styling_automotive_context_lane_rightshift_small
            LaneDirection.UNKNOWN_DIRECTION -> throw IllegalStateException("Unexpected value: $direction")
        }
    }

    override fun getLargeResourceForLaneDirection(direction: LaneDirection): Int {
        return when (direction) {
            LaneDirection.LEFT180 -> R.drawable.mapkit_styling_automotive_context_lane_left180_large
            LaneDirection.LEFT135 -> R.drawable.mapkit_styling_automotive_context_lane_left135_large
            LaneDirection.LEFT90 -> R.drawable.mapkit_styling_automotive_context_lane_left90_large
            LaneDirection.LEFT45 -> R.drawable.mapkit_styling_automotive_context_lane_left45_large
            LaneDirection.STRAIGHT_AHEAD -> R.drawable.mapkit_styling_automotive_context_lane_straightahead_large
            LaneDirection.RIGHT45 -> R.drawable.mapkit_styling_automotive_context_lane_right45_large
            LaneDirection.RIGHT90 -> R.drawable.mapkit_styling_automotive_context_lane_right90_large
            LaneDirection.RIGHT135 -> R.drawable.mapkit_styling_automotive_context_lane_right135_large
            LaneDirection.RIGHT180 -> R.drawable.mapkit_styling_automotive_context_lane_right180_large
            LaneDirection.LEFT_FROM_RIGHT -> R.drawable.mapkit_styling_automotive_context_lane_leftfromright_large
            LaneDirection.RIGHT_FROM_LEFT -> R.drawable.mapkit_styling_automotive_context_lane_rightfromleft_large
            LaneDirection.LEFT_SHIFT -> R.drawable.mapkit_styling_automotive_context_lane_leftshift_large
            LaneDirection.RIGHT_SHIFT -> R.drawable.mapkit_styling_automotive_context_lane_rightshift_large
            LaneDirection.UNKNOWN_DIRECTION -> throw IllegalStateException("Unexpected value: $direction")
        }
    }

    override fun getLaneItemDimension(dimension: UpcomingManeuverResourcesProvider.DimensionType): Int {
        val resources = context.resources
        return when (dimension) {
            UpcomingManeuverResourcesProvider.DimensionType.SMALL_OVERLAP -> resources.getDimension(R.dimen.mapkit_styling_automotive_overlap_laneitem_small)
            UpcomingManeuverResourcesProvider.DimensionType.LARGE_OVERLAP -> resources.getDimension(R.dimen.mapkit_styling_automotive_overlap_laneitem_large)
            UpcomingManeuverResourcesProvider.DimensionType.INDENT -> resources.getDimension(R.dimen.mapkit_styling_automotive_margin_laneitem_side)
            UpcomingManeuverResourcesProvider.DimensionType.WIDTH -> resources.getDimension(R.dimen.mapkit_styling_automotive_width_laneicon)
            UpcomingManeuverResourcesProvider.DimensionType.HEIGHT -> resources.getDimension(R.dimen.mapkit_styling_automotive_height_laneicon)
        }.toInt()
    }

    @DrawableRes
    override fun getManeuverResource(action: Action): Int? {
        return when (action) {
            Action.STRAIGHT -> R.drawable.mapkit_styling_automotive_context_ra_forward
            Action.SLIGHT_LEFT -> R.drawable.mapkit_styling_automotive_context_ra_take_left
            Action.SLIGHT_RIGHT -> R.drawable.mapkit_styling_automotive_context_ra_take_right
            Action.LEFT -> R.drawable.mapkit_styling_automotive_context_ra_turn_left
            Action.RIGHT -> R.drawable.mapkit_styling_automotive_context_ra_turn_right
            Action.HARD_LEFT -> R.drawable.mapkit_styling_automotive_context_ra_hard_turn_left
            Action.HARD_RIGHT -> R.drawable.mapkit_styling_automotive_context_ra_hard_turn_right
            Action.FORK_LEFT -> R.drawable.mapkit_styling_automotive_context_ra_take_left
            Action.FORK_RIGHT -> R.drawable.mapkit_styling_automotive_context_ra_take_right
            Action.UTURN_LEFT -> R.drawable.mapkit_styling_automotive_context_ra_turn_back_left
            Action.UTURN_RIGHT -> R.drawable.mapkit_styling_automotive_context_ra_turn_back_right
            Action.ENTER_ROUNDABOUT -> R.drawable.mapkit_styling_automotive_context_ra_in_circular_movement
            Action.LEAVE_ROUNDABOUT -> R.drawable.mapkit_styling_automotive_context_ra_out_circular_movement
            Action.BOARD_FERRY -> R.drawable.mapkit_styling_automotive_context_ra_boardferry
            Action.LEAVE_FERRY -> R.drawable.mapkit_styling_automotive_context_ra_boardferry
            Action.EXIT_LEFT -> R.drawable.mapkit_styling_automotive_context_ra_exit_left
            Action.EXIT_RIGHT -> R.drawable.mapkit_styling_automotive_context_ra_exit_right
            Action.FINISH -> R.drawable.mapkit_styling_automotive_context_ra_finish
            Action.UNKNOWN -> null
            Action.WAYPOINT -> null
        }
    }

    companion object {
        private val CYRILLIC_COUNTRIES = setOf("RU", "UA", "BY", "KZ")
    }
}
