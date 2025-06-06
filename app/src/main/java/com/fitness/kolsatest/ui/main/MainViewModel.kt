package com.fitness.kolsatest.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.kolsatest.R
import com.fitness.kolsatest.core.NetworkStatusTracker
import com.fitness.kolsatest.data.repository.WorkoutRepository
import com.fitness.kolsatest.ui.main.readModels.WorkoutUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    private val networkStatusTracker: NetworkStatusTracker,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutUiState>(WorkoutUiState.Loading)
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            if (!networkStatusTracker.isOnline()) {
                _uiState.value = WorkoutUiState.NoInternet
                return@launch
            }

            _uiState.value = WorkoutUiState.Loading

            try {
                val result = repository.getWorkouts()
                _uiState.value = WorkoutUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = WorkoutUiState.Error(
                    context.getString(R.string.error_loading_with_message, e.message ?: "")
                )
            }
        }
    }

}
