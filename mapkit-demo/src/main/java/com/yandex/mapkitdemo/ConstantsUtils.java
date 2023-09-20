package com.yandex.mapkitdemo;

import com.yandex.mapkit.geometry.Point;

import java.util.Arrays;
import java.util.List;

public class ConstantsUtils {

    static final Point DEFAULT_POINT = new Point(59.945933, 30.320045);

    // Map Objects
    static final Point ANIMATED_RECTANGLE_CENTER = new Point(59.956, 30.313);
    static final Point TRIANGLE_CENTER = new Point(59.948, 30.313);
    static final Point POLYLINE_CENTER = DEFAULT_POINT;
    static final Point CIRCLE_CENTER = new Point(59.956, 30.323);
    static final Point DRAGGABLE_PLACEMARK_CENTER = new Point(59.948, 30.323);
    static final Point ANIMATED_PLACEMARK_CENTER = new Point(59.948, 30.318);

    // Driving
    static final Point DRIVING_ROUTE_START_LOCATION = new Point(59.959194, 30.407094);
    static final Point DRIVING_ROUTE_END_LOCATION = new Point(55.733330, 37.587649);

    // Custom Layer
    static final String LOGO_URL = "https://maps-ios-pods-public.s3.yandex.net/mapkit_logo.png";

    // Masstransit
    static final Point MASSTRANSIT_POINT = new Point(55.752078, 37.592664);
    static final Point MASSTRANSIT_ROUTE_START_LOCATION = new Point(55.699671, 37.567286);
    static final Point MASSTRANSIT_ROUTE_END_LOCATION = new Point(55.790621, 37.558571);

    // Clustering
    static final List<Point> CLUSTER_CENTERS = Arrays.asList(
            new Point(55.756, 37.618),
            new Point(59.956, 30.313),
            new Point(56.838, 60.597),
            new Point(43.117, 131.900),
            new Point(56.852, 53.204)
    );
}
