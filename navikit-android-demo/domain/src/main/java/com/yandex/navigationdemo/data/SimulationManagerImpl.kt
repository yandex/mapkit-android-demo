package com.yandex.navigationdemo.data

import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.location.LocationSimulator
import com.yandex.mapkit.location.LocationSimulatorListener
import com.yandex.mapkit.location.SimulationAccuracy
import com.yandex.navigationdemo.domain.SimulationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_SIMULATION_SPEED = 20.0

@Singleton
class SimulationManagerImpl @Inject constructor() : SimulationManager {

    private var locationSimulator: LocationSimulator? = null
    private var simulationSpeed = DEFAULT_SIMULATION_SPEED

    private var locationSimulatorListener = LocationSimulatorListener {}

    private val simulationActiveImpl = MutableStateFlow(false)
    override val simulationActive: StateFlow<Boolean> = simulationActiveImpl

    override fun startSimulation(route: DrivingRoute) {
        val locationSimulator =
            MapKitFactory.getInstance().createLocationSimulator(route.geometry).apply {
                locationSimulatorListener = LocationSimulatorListener { stopSimulation() }
                subscribeForSimulatorEvents(locationSimulatorListener)
                speed = simulationSpeed
            }
        this.locationSimulator = locationSimulator
        MapKitFactory.getInstance().setLocationManager(locationSimulator)
        locationSimulator.startSimulation(SimulationAccuracy.COARSE)
        simulationActiveImpl.value = true
    }

    override fun stopSimulation() {
        locationSimulator?.unsubscribeFromSimulatorEvents(locationSimulatorListener)
        locationSimulator = null
        MapKitFactory.getInstance().resetLocationManagerToDefault()
        simulationActiveImpl.value = false
    }

    override fun resume() {
        if (simulationActive.value) {
            locationSimulator?.resume()
        }
    }

    override fun suspend() {
        if (simulationActive.value) {
            locationSimulator?.suspend()
        }
    }

    override fun setSimulationSpeed(speed: Double) {
        simulationSpeed = speed
        locationSimulator?.speed = simulationSpeed
    }
}
