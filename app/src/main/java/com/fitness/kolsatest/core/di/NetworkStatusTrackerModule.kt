package com.fitness.kolsatest.core.di

import android.content.Context
import com.fitness.kolsatest.core.NetworkStatusTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkStatusTrackerModule {

    @Singleton
    @Provides
    fun provideNetworkStatusTracker(@ApplicationContext context: Context): NetworkStatusTracker {
        return NetworkStatusTracker(context = context)
    }
}
