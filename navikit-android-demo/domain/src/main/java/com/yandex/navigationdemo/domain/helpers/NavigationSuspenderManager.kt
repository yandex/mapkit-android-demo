package com.yandex.navigationdemo.domain.helpers

enum class NavigationClient {
    BACKGROUND_SERVICE,
    ACTIVITY,
}

interface NavigationSuspenderManager {
    fun register(client: NavigationClient)
    fun removeClient(client: NavigationClient)
}
