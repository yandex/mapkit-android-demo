package com.yandex.navigationdemo.data.helpers

import com.yandex.navigationdemo.domain.NavigationManager
import com.yandex.navigationdemo.domain.helpers.NavigationClient
import com.yandex.navigationdemo.domain.helpers.NavigationSuspenderManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationSuspenderManagerImpl @Inject constructor(
    private val navigationManager: NavigationManager,
) : NavigationSuspenderManager {

    private val clients = mutableSetOf<NavigationClient>()

    override fun register(client: NavigationClient) {
        clients.add(client)
    }

    /**
     * We should suspend NavigationManager only after all android components
     * such as Services and Activities have been stopped.
     */
    override fun removeClient(client: NavigationClient) {
        clients.remove(client)
        if (clients.isEmpty()) {
            navigationManager.suspend()
        }
    }
}
