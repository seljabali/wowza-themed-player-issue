package com.seljabali.templateapplication.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import com.orhanobut.logger.Logger
import com.seljabali.core.BaseActivity
import com.seljabali.templateapplication.R
import com.wowza.gocoder.sdk.api.WowzaGoCoder
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig
import com.wowza.gocoder.sdk.api.player.WOWZPlayerConfig
import com.wowza.gocoder.sdk.api.player.WOWZPlayerView.*
import com.wowza.gocoder.sdk.api.status.WOWZState
import com.wowza.gocoder.sdk.api.status.WOWZStatus
import com.wowza.gocoder.sdk.api.status.WOWZStatusCallback
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), WOWZStatusCallback {

    private var playerConfig = WOWZPlayerConfig()
    private var goCoderSDK: WowzaGoCoder? = null

    companion object {
        private const val MAX_SECONDS_WITHOUT_PACKETS = 4
        private const val DEFAULT_VOLUME = 50
        private const val videoName: String = "sample.mp4"
        private const val appName: String = "vod"
        private const val somePortNumber: Int = 1935
        // TODO: Put url that points to a video
        private const val domainName: String = "foo.com"
        // TODO: Put a functioning App Key
        private const val SDKSampleAppLicenseKey = ""
        // TODO: go to themes.xml & uncomment "background" attr to toggle video visibility
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_LightBlue)
        setContentView(R.layout.activity_home)
        configurePlayer()
        volumeSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        volumeSeekBar.progress = DEFAULT_VOLUME
        playImageView.setOnClickListener { onPlayButtonClicked() }
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        onBackPressed()
        return true
    }


    override fun onWZStatus(status: WOWZStatus) {
        val playerStatus = WOWZStatus(status)
        Handler(Looper.getMainLooper()).post {
            WOWZStatus(playerStatus.state)
            when (playerStatus.state) {
                STATE_PREPARING -> {
                    Logger.v("playback preparing")
                }
                STATE_PREBUFFERING_STARTED -> {
                    Logger.v("playback pre-buffering started")
                }
                STATE_PREBUFFERING_ENDED -> {
                    Logger.v("playback pre-buffering stopped")
                }
                STATE_READY_TO_PLAY -> {
                    Logger.v("playback ready to play")
                    playerStatus.clearLastError()
                }
                STATE_PLAYING -> {
                    Logger.v("playback playing")
                }
                STATE_STOPPING -> {
                    Logger.v("playback stopping")
                }
                STATE_PLAYBACK_COMPLETE -> {
                    Logger.v("playback complete")
                }
                STATE_ERROR -> {
                    Logger.v("playback error")
                }
            }
        }
    }

    override fun onWZError(playerStatus: WOWZStatus) {
        Logger.e(playerStatus.lastError.errorDescription)
    }

    private fun configurePlayer() {
        goCoderSDK = WowzaGoCoder.init(applicationContext, SDKSampleAppLicenseKey)
        playerConfig.apply {
            videoFramerate = WOWZMediaConfig.DEFAULT_VIDEO_FRAME_RATE
            videoKeyFrameInterval = WOWZMediaConfig.DEFAULT_VIDEO_KEYFRAME_INTERVAL
            videoBitRate = WOWZMediaConfig.DEFAULT_VIDEO_BITRATE
            isABREnabled = true
            isPlayback = true
            isAudioEnabled = true
            isVideoEnabled = true
            hostAddress = domainName
            portNumber = somePortNumber
            applicationName = appName
            streamName = videoName
            isHLSEnabled = false
        }
        wowzPlayerView.apply {
            scaleMode = WOWZMediaConfig.FILL_VIEW
            logLevel = 1
            setMaxSecondsWithNoPackets(MAX_SECONDS_WITHOUT_PACKETS)
        }
        wowzPlayerView.volume = DEFAULT_VOLUME
    }


    private fun onTogglePlayStream() {
        if (wowzPlayerView.isPlaying) {
            playImageView.setImageResource(R.drawable.ic_play)
            stopPlayback()
            return
        }
        if (!wowzPlayerView.isReadyToPlay) {
            return
        }
        val configValidationError = playerConfig.validateForPlayback()
        if (configValidationError != null) {
            Logger.e(configValidationError.errorDescription)
            return
        }
        wowzPlayerView.play(playerConfig, this)
        playImageView.setImageResource(R.drawable.ic_stop)
    }


    private fun stopPlayback() {
        wowzPlayerView.stop()
        wowzPlayerView.currentStatus.waitForState(WOWZState.IDLE)
    }

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            wowzPlayerView.volume = progress
            volumeImageView.setImageResource(if (progress == 0) R.drawable.ic_volume_mute else R.drawable.ic_volume)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    private fun onPlayButtonClicked() {
        onTogglePlayStream()
    }

}
