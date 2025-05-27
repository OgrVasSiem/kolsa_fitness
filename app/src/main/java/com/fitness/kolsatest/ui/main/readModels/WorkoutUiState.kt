package com.fitness.kolsatest.ui.main.readModels

import com.fitness.kolsatest.data.models.Workout

sealed class WorkoutUiState {
    data object Loading : WorkoutUiState()
    data class Success(val workouts: List<Workout>) : WorkoutUiState()
    data class Error(val message: String) : WorkoutUiState()
    data object NoInternet : WorkoutUiState()
}
