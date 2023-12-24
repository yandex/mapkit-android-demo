package com.yandex.navigationdemo.domain

import com.yandex.mapkit.directions.driving.VehicleOptions

interface VehicleOptionsManager {
    fun vehicleOptions(): VehicleOptions
}
