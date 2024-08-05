package com.yandex.navikitdemo.domain.utils

import com.yandex.mapkit.LocalizedValue
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.geometry.PolylinePosition

fun DrivingRoute.timeWithTraffic(): LocalizedValue {
    return metadata.weight.timeWithTraffic
}

fun DrivingRoute.distanceLeft(): LocalizedValue {
    return metadata.weight.distance
}

fun DrivingRoute.advancePositionOnRoute(distance: Double): PolylinePosition? {
    return routePosition.advance(distance).positionOnRoute(routeId)
}

fun DrivingRoute.buildFlagsString(): String = metadata.flags.run {
    listOfNotNull(
        FLAG_BLOCKED.takeIf { blocked },
        FLAG_BUILT_OFFLINE.takeIf { builtOffline },
        FLAG_HAS_CHECKPOINTS.takeIf { hasCheckpoints },
        FLAG_FOR_PARKING.takeIf { forParking },
        FLAG_HAS_FERRIES.takeIf { hasFerries },
        FLAG_HAS_FORD_CROSSING.takeIf { hasFordCrossing },
        FLAG_HAS_RUGGED_ROADS.takeIf { hasRuggedRoads },
        FLAG_HAS_TOLLS.takeIf { hasTolls },
        FLAG_HAS_VEHICLE_RESTRICTIONS.takeIf { hasVehicleRestrictions },
        FLAG_PREDICTED.takeIf { predicted },
        FLAG_REQUIRES_ACCESS_PASS.takeIf { requiresAccessPass },
    ).joinToString()
}

// Emoji strings representation of DrivingRoute flags
private val FLAG_BLOCKED = String(intArrayOf(0x26d4), 0, 1) // ⛔
private val FLAG_BUILT_OFFLINE = String(intArrayOf(0x2708, 0xfe0f), 0, 2) // ✈️
private val FLAG_HAS_CHECKPOINTS = String(intArrayOf(0x1f6c3), 0, 1) //  🛃
private val FLAG_FOR_PARKING = String(intArrayOf(0x1f17f, 0xfe0f), 0, 2) // 🅿️
private val FLAG_HAS_FERRIES = String(intArrayOf(0x26f4, 0xfe0f), 0, 2) // ⛴️
private val FLAG_HAS_FORD_CROSSING = String(intArrayOf(0x1f3ca), 0, 1) // 🏊
private val FLAG_HAS_RUGGED_ROADS = String(intArrayOf(0x26a0, 0xfe0f), 0, 2) // ⚠️
private val FLAG_HAS_TOLLS = String(intArrayOf(0x1f4b0), 0, 1) // 💰
private val FLAG_HAS_VEHICLE_RESTRICTIONS = String(intArrayOf(0x1f69b), 0, 1) // 🚛
private val FLAG_PREDICTED = String(intArrayOf(0x1f52e), 0, 1) // 🔮
private val FLAG_REQUIRES_ACCESS_PASS = String(intArrayOf(0x1f510), 0, 1) // 🔐
