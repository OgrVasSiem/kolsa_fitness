package com.fitness.kolsatest.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fitness.kolsatest.R
import com.fitness.kolsatest.ui.main.adapters.WorkoutAdapter
import com.fitness.kolsatest.ui.main.readModels.WorkoutUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        workoutAdapter = WorkoutAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = workoutAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is WorkoutUiState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }

                        is WorkoutUiState.Success -> {
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            workoutAdapter.updateData(state.workouts)
                        }

                        is WorkoutUiState.Error -> {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_loading, state.message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is WorkoutUiState.NoInternet -> {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.no_internet),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false)
}
