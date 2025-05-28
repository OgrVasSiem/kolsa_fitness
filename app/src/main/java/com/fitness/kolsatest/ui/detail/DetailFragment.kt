package com.fitness.kolsatest.ui.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TrackSelectionDialogBuilder
import com.fitness.kolsatest.R
import com.fitness.kolsatest.data.models.Workout
import com.fitness.kolsatest.ui.detail.readModels.DetailUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@UnstableApi
@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val viewModel: DetailViewModel by viewModels()
    private var player: ExoPlayer? = null

    private lateinit var playerView: PlayerView
    private lateinit var trackSelector: DefaultTrackSelector
    private lateinit var progressBar: ProgressBar

    private lateinit var titleTextView: TextView
    private lateinit var typeTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var durationTextView: TextView

    private lateinit var qualityButton: ImageButton
    private lateinit var speedButton: ImageButton

    private val baseUrl = "https://ref.test.kolsa.ru"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        observeViewModel()

        val workoutId = DetailFragmentArgs.fromBundle(requireArguments()).workoutId
        viewModel.loadWorkout(workoutId)
    }

    private fun setupViews(view: View) {
        titleTextView = view.findViewById(R.id.titleTextView)
        typeTextView = view.findViewById(R.id.typeTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        durationTextView = view.findViewById(R.id.durationTextView)

        playerView = view.findViewById(R.id.playerView)
        progressBar = view.findViewById(R.id.progressBar)

        qualityButton = playerView.findViewById(R.id.exo_quality)
        speedButton = playerView.findViewById(R.id.exo_speed)

        qualityButton.setOnClickListener { showTrackSelectionDialog() }
        speedButton.setOnClickListener { showSpeedDialog() }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is DetailUiState.Loading -> showLoading()
                        is DetailUiState.Success -> showWorkoutDetails(state)
                        is DetailUiState.Error -> showError(state.message)
                        is DetailUiState.NoInternet -> showNoInternet()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun showWorkoutDetails(state: DetailUiState.Success) {
        progressBar.visibility = View.GONE
        bindWorkoutInfo(state.workout)
        initializePlayer(buildFullUrl(state.workoutVideoDto.link))
    }

    @SuppressLint("StringFormatMatches")
    private fun bindWorkoutInfo(workout: Workout) {
        titleTextView.text = workout.title

        val typeName = when (workout.type) {
            1 -> getString(R.string.workout_type_training)
            2 -> getString(R.string.workout_type_stream)
            3 -> getString(R.string.workout_type_complex)
            else -> getString(R.string.workout_type_unknown)
        }

        typeTextView.text = getString(R.string.workout_type_format, typeName)
        descriptionTextView.text = workout.description ?: getString(R.string.workout_type_unknown)
        durationTextView.text = getString(R.string.workout_duration_format, workout.getDurationAsInt())
    }

    private fun buildFullUrl(path: String): String {
        return if (path.startsWith("/")) "$baseUrl$path" else "$baseUrl/$path"
    }

    private fun initializePlayer(videoUrl: String) {
        trackSelector = DefaultTrackSelector(requireContext())

        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build().also { exo ->
                playerView.player = exo
                exo.setMediaItem(MediaItem.fromUri(videoUrl))
                exo.prepare()
                exo.playWhenReady = true
                playerView.useArtwork = true
            }
    }

    private fun showTrackSelectionDialog() {
        player?.let { exo ->
            TrackSelectionDialogBuilder(
                requireContext(),
                getString(R.string.video_quality),
                exo,
                C.TRACK_TYPE_VIDEO
            )
                .setAllowAdaptiveSelections(true)
                .build()
                .show()
        }
    }

    private fun showSpeedDialog() {
        val speeds = listOf(0.5f, 1f, 1.25f, 1.5f, 2f)
        val labels = speeds.map {
            getString(R.string.playback_speed_format, it)
        }.toTypedArray()

        val currentSpeed = player?.playbackParameters?.speed ?: 1f
        val checkedIndex = speeds.indexOfFirst { it == currentSpeed }.coerceAtLeast(0)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.playback_speed))
            .setSingleChoiceItems(labels, checkedIndex) { dialog, which ->
                player?.playbackParameters = PlaybackParameters(speeds[which])
                dialog.dismiss()
            }
            .show()
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        Toast.makeText(
            requireContext(),
            getString(R.string.error_loading, message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showNoInternet() {
        progressBar.visibility = View.GONE
        Toast.makeText(
            requireContext(),
            getString(R.string.no_internet),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
