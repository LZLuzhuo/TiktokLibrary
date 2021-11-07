package me.luzhuo.lib_tiktok.controller

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.dueeeke.videoplayer.controller.ControlWrapper
import com.dueeeke.videoplayer.controller.IControlComponent
import com.dueeeke.videoplayer.player.VideoView
import me.luzhuo.lib_tiktok.R

/**
 * 可拖动的进度条
 */
class TikTokSeekbarControlView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), IControlComponent, View.OnClickListener, OnSeekBarChangeListener {
    private val mVideoProgress: SeekBar
    private var mControlWrapper: ControlWrapper? = null
    private var mIsDragging = false

    init {
        visibility = GONE
        LayoutInflater.from(getContext()).inflate(R.layout.tiktok_layout_seekbar_control_view, this, true)
        mVideoProgress = findViewById<SeekBar>(R.id.seekBar)
        mVideoProgress.setOnSeekBarChangeListener(this)

        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) mVideoProgress.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun attach(controlWrapper: ControlWrapper) {
        mControlWrapper = controlWrapper
    }

    override fun getView(): View = this

    override fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {
    }

    override fun onPlayStateChanged(playState: Int) {
        when (playState) {
            VideoView.STATE_IDLE, VideoView.STATE_PLAYBACK_COMPLETED -> {
                visibility = GONE
                mVideoProgress.progress = 0
                mVideoProgress.secondaryProgress = 0
            }
            VideoView.STATE_START_ABORT, VideoView.STATE_PREPARING, VideoView.STATE_PREPARED, VideoView.STATE_ERROR -> {
                visibility = GONE
            }
            VideoView.STATE_PLAYING -> {
                visibility = VISIBLE
                mControlWrapper?.startProgress()
            }
            VideoView.STATE_PAUSED -> {

            }
            VideoView.STATE_BUFFERING, VideoView.STATE_BUFFERED -> {

            }
        }
    }

    override fun onPlayerStateChanged(playerState: Int) {
    }

    override fun setProgress(duration: Int, position: Int) {
        if (mIsDragging) return

        if (duration > 0) {
            mVideoProgress.isEnabled = true
            val pos = (position * 1.0 / duration * mVideoProgress.max).toInt()
            mVideoProgress.progress = pos
        } else {
            mVideoProgress.isEnabled = false
        }
        val percent = mControlWrapper?.bufferedPercentage ?: 0
        if (percent >= 95) { //解决缓冲进度不能100%问题
            mVideoProgress.secondaryProgress = mVideoProgress.max
        } else {
            mVideoProgress.secondaryProgress = percent * 10
        }
    }

    override fun onLockStateChanged(isLocked: Boolean) {
        onVisibilityChanged(!isLocked, null)
    }

    override fun onClick(v: View?) {
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        mIsDragging = true
        mControlWrapper?.stopProgress()
        mControlWrapper?.stopFadeOut()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        val duration = mControlWrapper?.duration ?: 0
        val newPosition = duration * (seekBar?.progress ?: 0) / mVideoProgress.max
        mControlWrapper?.seekTo(newPosition)
        mIsDragging = false
        mControlWrapper?.startProgress()
        mControlWrapper?.startFadeOut()
    }
}