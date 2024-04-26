package com.yandex.navikitdemo.domain.models

import com.yandex.mapkit.navigation.automotive.AnnotatedEvents
import com.yandex.mapkit.navigation.automotive.AnnotatedRoadEvents

enum class AnnotatedEventsType(val mapkitEnum: AnnotatedEvents) {
    MANOEUVRES(AnnotatedEvents.MANOEUVRES),
    FASTER_ALTERNATIVE(AnnotatedEvents.FASTER_ALTERNATIVE),
    ROAD_EVENTS(AnnotatedEvents.ROAD_EVENTS),
    TOLL_ROAD_AHEAD(AnnotatedEvents.TOLL_ROAD_AHEAD),
    SPEED_LIMIT_EXCEEDED(AnnotatedEvents.SPEED_LIMIT_EXCEEDED),
    PARKING_ROUTES(AnnotatedEvents.PARKING_ROUTES),
    STREETS(AnnotatedEvents.STREETS),
    ROUTE_STATUS(AnnotatedEvents.ROUTE_STATUS),
    WAY_POINTS(AnnotatedEvents.WAY_POINTS),
    SPEED_BUMPS(AnnotatedEvents.SPEED_BUMPS),
    RAILWAY_CROSSINGS(AnnotatedEvents.RAILWAY_CROSSINGS),
    LANES(AnnotatedEvents.LANES),
    ROUTE_ACTIONS(AnnotatedEvents.ROUTE_ACTIONS),
    EVERYTHING(AnnotatedEvents.EVERYTHING),
}

enum class AnnotatedRoadEventsType(val mapkitEnum: AnnotatedRoadEvents) {
    DANGER(AnnotatedRoadEvents.DANGER),
    RECONSTRUCTION(AnnotatedRoadEvents.RECONSTRUCTION),
    ACCIDENT(AnnotatedRoadEvents.ACCIDENT),
    SCHOOL(AnnotatedRoadEvents.SCHOOL),
    OVERTAKING_DANGER(AnnotatedRoadEvents.OVERTAKING_DANGER),
    PEDESTRIAN_DANGER(AnnotatedRoadEvents.PEDESTRIAN_DANGER),
    CROSS_ROAD_DANGER(AnnotatedRoadEvents.CROSS_ROAD_DANGER),
    LANE_CONTROL(AnnotatedRoadEvents.LANE_CONTROL),
    ROAD_MARKING_CONTROL(AnnotatedRoadEvents.ROAD_MARKING_CONTROL),
    CROSS_ROAD_CONTROL(AnnotatedRoadEvents.CROSS_ROAD_CONTROL),
    MOBILE_CONTROL(AnnotatedRoadEvents.MOBILE_CONTROL),
    SPEED_LIMIT_CONTROL(AnnotatedRoadEvents.SPEED_LIMIT_CONTROL),
    TRAFFIC_CONTROLS(AnnotatedRoadEvents.TRAFFIC_CONTROLS),
    EVERYTHING(AnnotatedRoadEvents.EVERYTHING),
}
