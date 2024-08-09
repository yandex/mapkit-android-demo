package com.yandex.navikitdemo.domain.utils

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.models.SmartRouteOptions
import com.yandex.runtime.Error
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine

fun DrivingRouter.requestRoutes(
    points: List<RequestPoint>,
    drivingOptions: DrivingOptions,
    vehicleOptions: VehicleOptions
): Flow<Result<DrivingRoute>> {
    return callbackFlow {
        val listener = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
                val result = drivingRoutes.firstOrNull()?.let { Result.success(it) }
                    ?: Result.failure(Exception("DrivingRoutes is empty"))
                trySend(result)
            }

            override fun onDrivingRoutesError(error: Error) {
                trySend(Result.failure(Exception("DrivingRoutesError: $error")))
            }
        }
        val drivingSession = requestRoutes(points, drivingOptions, vehicleOptions, listener)
        awaitClose { drivingSession.cancel() }
    }
}

fun SearchManager.submitSearch(
    query: String,
    geometry: Geometry,
    searchOptions: SearchOptions,
): Flow<Result<List<Point>>> {
    return callbackFlow {
        val listener = object : Session.SearchListener {
            override fun onSearchResponse(response: Response) {
                val items = response.collection.children.mapNotNull {
                    it.obj?.geometry?.firstOrNull()?.point ?: return@mapNotNull null
                }
                trySend(Result.success(items))
            }

            override fun onSearchError(error: Error) {
                trySend(Result.failure(Exception("SearchError: $error")))
            }
        }
        val searchSession = submit(query, geometry, searchOptions, listener)
        awaitClose { searchSession.cancel() }
    }
}

fun SettingsManager.smartRouteOptionsChanges(): Flow<SmartRouteOptions?> {
    return combine(
        smartRoutePlanningEnabled.changes(),
        fuelConnectorTypes.changes(),
        maxTravelDistance.changes(),
        currentRangeLvl.changes(),
        thresholdDistance.changes(),
    ) { smartRoutePlanningEnabled, _, _, _, _ ->
        if (smartRoutePlanningEnabled) {
            smartRouteOptions()
        } else {
            null
        }
    }
}

fun SettingsManager.smartRouteOptions(): SmartRouteOptions {
    return SmartRouteOptions(
        chargingType.value,
        fuelConnectorTypes.value,
        maxTravelDistance.value.toMeters(),
        currentRangeLvl.value.toMeters(),
        thresholdDistance.value.toMeters()
    )
}
