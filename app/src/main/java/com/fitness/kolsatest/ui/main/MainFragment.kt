package com.fitness.kolsatest.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fitness.kolsatest.R
import com.fitness.kolsatest.data.models.Workout
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
    private lateinit var searchField: EditText

    private var allWorkouts: List<Workout> = emptyList()
    private var currentFilterType: Int = 0
    private var currentQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false)

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        view.setOnTouchListener { _, _ ->
            if (searchField.isFocused) {
                searchField.clearFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchField.windowToken, 0)
            }
            false
        }

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.topAppBar)) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.updatePadding(top = statusBarHeight)
            WindowInsetsCompat.CONSUMED
        }

        setupRecyclerView()
        setupSearchField()
        setupFilterMenu(view)
        observeViewModel()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        searchField = view.findViewById(R.id.searchField)
    }

    private fun setupRecyclerView() {
        workoutAdapter = WorkoutAdapter(emptyList()) { workout ->
            val action = MainFragmentDirections.actionMainFragmentToDetailFragment(workout.id)
            findNavController().navigate(action)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = workoutAdapter
    }


    private fun setupSearchField() {
        val searchIcon = requireView().findViewById<ImageView>(R.id.searchIcon)
        searchIcon.setOnClickListener {
            val isNowVisible = !searchField.isVisible
            searchField.visibility = if (isNowVisible) View.VISIBLE else View.GONE

            if (isNowVisible) {
                searchField.requestFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT)
            } else {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchField.windowToken, 0)
            }
        }
        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchField.clearFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchField.windowToken, 0)
                true
            } else {
                false
            }
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                currentQuery = s.toString()
                filterWorkouts()
            }
        })
    }


    private fun setupFilterMenu(view: View) {
        val filterIcon = view.findViewById<ImageView>(R.id.filterIcon)
        filterIcon.setOnClickListener {
            val popup = PopupMenu(requireContext(), it)
            val filterOptions = resources.getStringArray(R.array.workout_type_filter)

            filterOptions.forEachIndexed { index, title ->
                popup.menu.add(Menu.NONE, index, index, title)
            }

            popup.setOnMenuItemClickListener { item ->
                currentFilterType = item.itemId
                filterWorkouts()
                true
            }

            popup.show()
        }
    }

    private fun filterWorkouts() {
        val filtered = allWorkouts.filter { workout ->
            (currentFilterType == 0 || workout.type == currentFilterType) && workout.title.contains(currentQuery, ignoreCase = true)
        }
        workoutAdapter.updateData(filtered)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is WorkoutUiState.Loading -> showLoading()
                        is WorkoutUiState.Success -> showData(state.workouts)
                        is WorkoutUiState.Error -> showError(state.message)
                        is WorkoutUiState.NoInternet -> showNoInternet()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun showData(workouts: List<Workout>) {
        allWorkouts = workouts
        filterWorkouts()
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        Toast.makeText(requireContext(), getString(R.string.error_loading, message), Toast.LENGTH_SHORT).show()
    }

    private fun showNoInternet() {
        progressBar.visibility = View.GONE
        Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
    }
}
