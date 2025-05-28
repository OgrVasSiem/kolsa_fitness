package com.fitness.kolsatest.ui.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.kolsatest.R
import com.fitness.kolsatest.core.NetworkStatusTracker
import com.fitness.kolsatest.data.repository.WorkoutRepository
import com.fitness.kolsatest.ui.detail.readModels.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    private val networkStatusTracker: NetworkStatusTracker,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadWorkout(id: Int) {
        viewModelScope.launch {
            if (!networkStatusTracker.isOnline()) {
                _uiState.value = DetailUiState.NoInternet
                return@launch
            }

            _uiState.value = DetailUiState.Loading
            try {
                val workouts = repository.getWorkouts()
                val workout = workouts.find { it.id == id }

                if (workout == null) {
                    _uiState.value = DetailUiState.Error(
                        context.getString(R.string.error_workout_not_found, id)
                    )
                    return@launch
                }

                val video = repository.getWorkoutVideo(id)
                _uiState.value = DetailUiState.Success(video, workout)
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(
                    context.getString(R.string.error_loading_with_message, e.message ?: "")
                )
            }
        }
    }

}

