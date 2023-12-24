package com.yandex.navigationdemo.domain

import com.yandex.mapkit.directions.driving.DrivingRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SimulationManager {
    val simulationActive: StateFlow<Boolean>

    fun startSimulation(route: DrivingRoute)
    fun stopSimulation()

    fun resume()
    fun suspend()

    fun setSimulationSpeed(speed: Double)
}
