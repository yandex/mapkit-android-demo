# Navigation layer demo-app

This repository contains an application that demonstraits use of MapKit SDK API with NavigationLayer api

## Build

1. To build the project you need to put external dependencies to the ```libs/``` directory:

    - maps.mobile.aar
    - com.yandex.mapkit.navigation.automotive.layer.styling.aar
    - com.yandex.mapkit.styling.roadevents.aar

2. Put your MapKit API key in to the project level ```local.properties``` file, like the following:

    ```
    MAPKIT_API_KEY=<your-api-key>
    ```

3. Open the project in Android Studio and build the application.
