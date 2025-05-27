package com.fitness.kolsatest.core

sealed class NetworkStatus {

    data object Available : NetworkStatus()

    data object Unavailable : NetworkStatus()
}
