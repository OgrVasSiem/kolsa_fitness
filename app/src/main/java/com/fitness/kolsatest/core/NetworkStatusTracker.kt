package com.fitness.kolsatest.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class NetworkStatusTracker(
    context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkStatus = callbackFlow {
        val currentStatus = if (connectivityManager.isCurrentlyConnected()) {
            NetworkStatus.Available
        } else {
            NetworkStatus.Unavailable
        }
        trySend(currentStatus)

        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(NetworkStatus.Available)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                if (!connectivityManager.isCurrentlyConnected()) {
                    trySend(NetworkStatus.Unavailable)
                }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(NetworkStatus.Unavailable)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, networkStatusCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkStatusCallback)
        }
    }

    private fun ConnectivityManager.isCurrentlyConnected(): Boolean {
        val activeNetwork = activeNetwork ?: return false
        val capabilities = getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun isOnline(): Boolean {
        return connectivityManager.isCurrentlyConnected()
    }
}