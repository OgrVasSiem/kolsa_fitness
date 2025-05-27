package com.fitness.kolsatest.ui.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
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

    private lateinit var qualityButton: ImageButton
    private lateinit var speedButton: ImageButton

    private val baseUrl = "https://ref.test.kolsa.ru"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerView = view.findViewById(R.id.playerView)
        progressBar = view.findViewById(R.id.progressBar)

        qualityButton = playerView.findViewById(R.id.exo_quality)
        speedButton = playerView.findViewById(R.id.exo_speed)

        qualityButton.setOnClickListener { showTrackSelectionDialog() }
        speedButton.setOnClickListener { showSpeedDialog() }

        val workoutId = DetailFragmentArgs.fromBundle(requireArguments()).workoutId
        viewModel.loadWorkout(workoutId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is DetailUiState.Loading ->
                            progressBar.visibility = View.VISIBLE

                        is DetailUiState.Success -> {
                            progressBar.visibility = View.GONE
                            initializePlayer(buildFullUrl(state.workoutVideoDto.link))
                        }

                        is DetailUiState.Error ->
                            showError(state.message)

                        is DetailUiState.NoInternet ->
                            showNoInternet()
                    }
                }
            }
        }
    }

    private fun buildFullUrl(path: String) =
        if (path.startsWith("/")) "$baseUrl$path" else "$baseUrl/$path"

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
                "Выбор качества",
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
        val labels = speeds.map { "${it}×" }.toTypedArray()
        val current = player?.playbackParameters?.speed ?: 1f
        val checked = speeds.indexOfFirst { it == current }.coerceAtLeast(0)

        AlertDialog.Builder(requireContext())
            .setTitle("Скорость воспроизведения")
            .setSingleChoiceItems(labels, checked) { dlg, which ->
                player?.playbackParameters = PlaybackParameters(speeds[which])
                dlg.dismiss()
            }
            .show()
    }

    private fun showError(msg: String) {
        progressBar.visibility = View.GONE
        Toast.makeText(
            requireContext(),
            getString(R.string.error_loading, msg),
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


