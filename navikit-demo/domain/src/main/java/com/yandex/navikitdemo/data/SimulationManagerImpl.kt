package com.yandex.navikitdemo.data

import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.location.LocationSimulator
import com.yandex.mapkit.location.LocationSimulatorListener
import com.yandex.mapkit.location.SimulationAccuracy
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SimulationManager
import com.yandex.navikitdemo.domain.utils.toMetersPerSecond
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

const val DEFAULT_SIMULATION_SPEED = 72.0 // km / h
const val MAX_SIMULATION_SPEED = 160.0f // km / h
const val MIN_SIMULATION_SPEED = 0.0f // km / h

@Singleton
class SimulationManagerImpl @Inject constructor(
    settingsManager: SettingsManager,
) : SimulationManager {

    private var locationSimulator: LocationSimulator? = null
    private var simulationSpeed = settingsManager.simulationSpeed.value.toDouble().toMetersPerSecond()

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
        simulationSpeed = speed.toMetersPerSecond()
        locationSimulator?.speed = simulationSpeed
    }
}
