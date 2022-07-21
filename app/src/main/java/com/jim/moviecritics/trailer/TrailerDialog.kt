package com.jim.moviecritics.trailer


import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.jim.moviecritics.BuildConfig
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.DialogTrailerBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.ext.showToast
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class TrailerDialog : AppCompatDialogFragment(), YouTubePlayer.OnInitializedListener {



    private val viewModel by viewModels<TrailerViewModel> {
        getVmFactory(TrailerDialogArgs.fromBundle(requireArguments()).movie)
    }

    private lateinit var binding: DialogTrailerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TrailerDialog)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = DialogTrailerBinding.inflate(inflater, container, false)
        binding.layoutTrailer.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_up))

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        binding.youtubePlayer.initialize(BuildConfig.API_KEY_YOUTUBE, this)


        viewModel.movie.observe(viewLifecycleOwner) {
            Logger.i("Review Dialog movie = $it")
        }

        viewModel.leave.observe(viewLifecycleOwner) {
            when (viewModel.leave.value) {
                true -> {
                    Logger.i("Trailer Dialog leave true = $it")
                    dismiss()
                    viewModel.onLeaveCompleted()
                }
                false -> { Logger.i("Trailer Dialog leave false = $it") }
                null -> { Logger.i("Trailer Dialog leave null = $it") }
            }
        }

        viewModel.status.observe(viewLifecycleOwner) {
            Logger.i("Trailer Dialog status = $it")
            it?.let {
                when (it) {
                    LoadApiStatus.DONE -> activity.showToast("The movie's review was published !")
                    else -> { Logger.i("ReviewViewModel status value else = $it") }
                }
            }
        }

        return binding.root
    }

    override fun dismiss() {
        binding.layoutTrailer.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_down))
        lifecycleScope.launch {
            delay(200)
            super.dismiss()
            viewModel.onLeaveCompleted()
        }
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        youTubePlayer: YouTubePlayer?,
        wasRestored: Boolean,
    ) {
        Logger.d("onInitializationSuccess: provider is ${provider?.javaClass}")
        Logger.d("onInitializationSuccess: youTubePlayer is ${youTubePlayer?.javaClass}")
        Logger.i("Initialized Youtube Player")

        youTubePlayer?.setPlayerStateChangeListener(playerStateChangeListener)
        youTubePlayer?.setPlaybackEventListener(playbackEventListener)

        if (!wasRestored) {
            youTubePlayer?.cueVideo("sN3HEtWOcjA")
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        youTubeInitializationResult: YouTubeInitializationResult?,
    ) {
        val REQUEST_CODE = 0

        if (youTubeInitializationResult?.isUserRecoverableError == true) {
            youTubeInitializationResult.getErrorDialog(activity, REQUEST_CODE).show()
        } else {
            val errorMessage = "There was an error initializing the YoutubePlayer ($youTubeInitializationResult)"
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private val playbackEventListener = object: YouTubePlayer.PlaybackEventListener {
        override fun onSeekTo(p0: Int) {
        }

        override fun onBuffering(p0: Boolean) {
        }

        override fun onPlaying() {
            Toast.makeText(context, "Good, video is playing ok", Toast.LENGTH_SHORT).show()
        }

        override fun onStopped() {
            Toast.makeText(context, "Video has stopped", Toast.LENGTH_SHORT).show()
        }

        override fun onPaused() {
            Toast.makeText(context, "Video has paused", Toast.LENGTH_SHORT).show()
        }
    }

    private val playerStateChangeListener = object: YouTubePlayer.PlayerStateChangeListener {
        override fun onAdStarted() {
            Toast.makeText(context, "Click Ad now, make the video creator rich!", Toast.LENGTH_SHORT).show()
        }

        override fun onLoading() {
        }

        override fun onVideoStarted() {
            Toast.makeText(context, "Video has started", Toast.LENGTH_SHORT).show()
        }

        override fun onLoaded(p0: String?) {
        }

        override fun onVideoEnded() {
            Toast.makeText(context, "Congratulations! You've completed another video.", Toast.LENGTH_SHORT).show()
        }

        override fun onError(p0: YouTubePlayer.ErrorReason?) {
        }
    }

}