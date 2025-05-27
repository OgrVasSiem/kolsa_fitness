package com.fitness.kolsatest.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.kolsatest.core.NetworkStatusTracker
import com.fitness.kolsatest.data.repository.WorkoutRepository
import com.fitness.kolsatest.ui.detail.readModels.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    private val networkStatusTracker: NetworkStatusTracker
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
                val result = repository.getWorkoutVideo(id)
                _uiState.value = DetailUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }
}

