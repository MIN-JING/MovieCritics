package com.jim.moviecritics.trailer


import android.media.MediaDrm
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.DialogTrailerBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.ext.showToast
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
class TrailerDialog : AppCompatDialogFragment(), SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnDrmInfoListener {

    private val mediaPlayer = MediaPlayer()
    private lateinit var runnable: Runnable
    private var handler = Handler(Looper.getMainLooper())


    companion object {
        const val SECOND = 1000
    }


    private val viewModel by viewModels<TrailerViewModel> {
        getVmFactory(TrailerDialogArgs.fromBundle(requireArguments()).movie)
    }

    private lateinit var binding: DialogTrailerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //***** Let layout showing match constraint *****
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TrailerDialog)

//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = DialogTrailerBinding.inflate(inflater, container, false)
        binding.layoutTrailer.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_up))

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnDrmInfoListener(this)
        binding.videoTrailer.holder.addCallback(this)
        binding.seekBarTrailer.setOnSeekBarChangeListener(this)
        binding.buttonTrailerPlay.isEnabled = false


        binding.buttonTrailerPlay.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.buttonTrailerPlay.setImageResource(android.R.drawable.ic_media_play)
            } else {
                mediaPlayer.start()
                binding.buttonTrailerPlay.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

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

    // Release the media player resources when activity gets destroyed
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        mediaPlayer.release()
        mediaPlayer.releaseDrm()
    }

    override fun dismiss() {
        binding.layoutTrailer.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_down))
        lifecycleScope.launch {
            delay(200)
            super.dismiss()
            viewModel.onLeaveCompleted()
        }
    }

    private fun timeInString(seconds: Int): String {
        return String.format(
            "%02d:%02d",
            (seconds / 3600 * 60 + ((seconds % 3600) / 60)),
            (seconds % 60)
        )
    }

    // Initialize seekBar
    private fun initializeSeekBar() {
        binding.seekBarTrailer.max = mediaPlayer.seconds
        binding.textTrailerProgress.text = getString(R.string.default_progress_value)
        binding.textTrailerTotalTime.text = timeInString(mediaPlayer.seconds)
        binding.progressBarTrailer.visibility = View.GONE
        binding.buttonTrailerPlay.isEnabled = true
    }

    // Update seek bar after every 1 second
    private fun updateSeekBar() {
        runnable = Runnable {
            binding.textTrailerProgress.text = timeInString(mediaPlayer.currentSeconds)
            binding.seekBarTrailer.progress = mediaPlayer.currentSeconds
            handler.postDelayed(runnable, SECOND.toLong())
        }
        handler.postDelayed(runnable, SECOND.toLong())
    }

    // SurfaceHolder is ready
    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        mediaPlayer.apply {
            Logger.i("surfaceCreated trailerUri = ${viewModel.movie.value?.trailerUri}")
//            context?.let { setDataSource(it, Uri.parse("android.resource://com.jim.moviecritics/raw/test_video")) }
//            context?.let { setDataSource(it, Uri.parse(viewModel.movie.value?.trailerUri)) }
//            setDataSource(applicationContext, selectedVideoUri)

            //For DRM protected video
            setOnDrmInfoListener(this@TrailerDialog) //This method will invoke onDrmInfo() function
//            selectedVideoUri = Uri.parse("https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1")
            setDataSource("https://www.youtube.com/watch?v=QI3DLYOHUM0")
            setDisplay(surfaceHolder)
            prepareAsync()
        }
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser)
            mediaPlayer.seekTo(progress * SECOND)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        initializeSeekBar()
        updateSeekBar()
    }

    override fun onDrmInfo(mediaPlayer: MediaPlayer?, drmInfo: MediaPlayer.DrmInfo?) {
        mediaPlayer?.apply {
            val key = drmInfo?.supportedSchemes?.get(0)
            key?.let {
                prepareDrm(key)
                val keyRequest = getKeyRequest(
                    null, null, null,
                    MediaDrm.KEY_TYPE_STREAMING, null
                )
                provideKeyResponse(null, keyRequest.data)
            }
        }
    }

    // Creating an extension properties to get the media player total time and current duration in seconds
    private val MediaPlayer.seconds: Int
        get() {
            return this.duration / SECOND
        }

    private val MediaPlayer.currentSeconds: Int
        get() {
            return this.currentPosition / SECOND
        }
}