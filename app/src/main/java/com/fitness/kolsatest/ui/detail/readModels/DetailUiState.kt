package com.fitness.kolsatest.ui.detail.readModels

import com.fitness.kolsatest.data.models.Workout
import com.fitness.kolsatest.data.models.WorkoutVideoDto

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(
        val workoutVideoDto: WorkoutVideoDto,
        val workout: Workout
    ) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
    data object NoInternet : DetailUiState()
}
