package com.fitness.kolsatest.data.repository

import com.fitness.kolsatest.data.models.Workout
import com.fitness.kolsatest.data.models.WorkoutVideoDto
import com.fitness.kolsatest.data.remote.WorkoutApi
import javax.inject.Inject

class WorkoutRepository @Inject constructor(
    private val api: WorkoutApi
) {
    suspend fun getWorkouts(): List<Workout> {
        return api.getWorkouts()
    }

    suspend fun getWorkoutVideo(id: Int): WorkoutVideoDto {
        return api.getVideo(id)
    }
}

