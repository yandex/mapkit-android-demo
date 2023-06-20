package com.yandex.mapkitdemo

import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.geometry.Polyline

object GeometryProvider {

    val polygon: Polygon
        get() {
            var points = listOf(
                25.190614 to 55.265616,
                25.187532 to 55.275413,
                25.196605 to 55.280940,
                25.198219 to 55.272685,
            ).map { (lat, lon) -> Point(lat, lon) }

            points = points.toMutableList() + points.first()
            val ring = LinearRing(points)

            val innerRing = listOf(
                25.190978 to 55.273982,
                25.191958 to 55.273780,
                25.192516 to 55.272040,
                25.192015 to 55.271365,
                25.190978 to 55.273982,
            )
                .map { (lat, lon) -> Point(lat, lon) }
                .let { LinearRing(it) }

            return Polygon(
                ring,
                listOf(innerRing)
            )
        }


    val polyline: Polyline
        get() {
            val points = listOf(
                25.184844 to 55.258163,
                25.188887 to 55.261771,
                25.190809 to 55.259483,
                25.204718 to 55.270949,
                25.195031 to 55.289207,
            ).map { (lat, lon) -> Point(lat, lon) }
            return Polyline(points)
        }

    fun circle(): Circle {
        return Circle(
            Point(25.209252, 55.282737),
            (300..1000).random().toFloat()
        )
    }

    val clusterizedPoints = listOf(
        25.190614 to 55.265616,
        25.187532 to 55.275413,
        25.196605 to 55.280940,
        25.198219 to 55.272685,
        25.180998 to 55.255508,
        25.179091 to 55.258284,
        25.178095 to 55.255314,
        25.169084 to 55.273855,
        25.172865 to 55.275724,
        25.165051 to 55.275517,
        25.170596 to 55.279671,
        25.178446 to 55.244884,
        25.177345 to 55.243418,
        25.176301 to 55.242463,
        25.177808 to 55.240233,
        25.181345 to 55.242272,
        25.182055 to 55.241091,
        25.184258 to 55.241001,
        25.176576 to 55.260324,
        25.176725 to 55.262622,
        25.174783 to 55.260433,
        25.174982 to 55.263005,
    ).map { (lat, lon) -> Point(lat, lon) }

}
