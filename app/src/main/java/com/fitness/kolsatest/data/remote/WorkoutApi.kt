package com.fitness.kolsatest.data.remote

import com.fitness.kolsatest.data.models.Workout
import com.fitness.kolsatest.data.models.WorkoutVideoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WorkoutApi {
    @GET("/get_workouts")
    suspend fun getWorkouts(): List<Workout>

    @GET("/get_video")
    suspend fun getVideo(@Query("id") id: Int): WorkoutVideoDto
}