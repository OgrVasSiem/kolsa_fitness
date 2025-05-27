package com.fitness.kolsatest.data.di

import com.fitness.kolsatest.data.remote.WorkoutApi
import com.fitness.kolsatest.data.repository.WorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://ref.test.kolsa.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideWorkoutApi(retrofit: Retrofit): WorkoutApi =
        retrofit.create(WorkoutApi::class.java)

    @Provides
    @Singleton
    fun provideWorkoutRepository(api: WorkoutApi): WorkoutRepository =
        WorkoutRepository(api)
}