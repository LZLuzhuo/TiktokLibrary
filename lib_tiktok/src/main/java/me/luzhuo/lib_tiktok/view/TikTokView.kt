/* Copyright 2021 Luzhuo. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.luzhuo.lib_tiktok.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.Toast
import com.dueeeke.videoplayer.controller.ControlWrapper
import com.dueeeke.videoplayer.controller.IControlComponent
import com.dueeeke.videoplayer.player.VideoView
import kotlinx.android.synthetic.main.tiktok_layout_media_controller.view.*
import me.luzhuo.lib_tiktok.R
import kotlin.math.abs

class TikTokView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), IControlComponent {

    private var mControlWrapper: ControlWrapper? = null
    private var mScaledTouchSlop: Int
    private var mStartX: Int = 0
    private var mStartY: Int = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.tiktok_layout_media_controller, this, true)
        setOnClickListener {
            mControlWrapper?.togglePlay()
        }
        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    /**
     * 解决点击和VerticalViewPager滑动冲突问题
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return super.onTouchEvent(event)
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = event.x.toInt()
                mStartY = event.y.toInt()
                return true
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.x.toInt()
                val endY = event.y.toInt()
                if (abs(endX - mStartX) < mScaledTouchSlop && abs(endY - mStartY) < mScaledTouchSlop) performClick()
            }
        }
        return false
    }

    override fun attach(controlWrapper: ControlWrapper) {
        mControlWrapper = controlWrapper
    }

    override fun getView(): View = this

    override fun onPlayStateChanged(playState: Int) {
        when (playState) {
            VideoView.STATE_IDLE -> media_thumb.visibility = VISIBLE
            VideoView.STATE_PLAYING -> {
                media_thumb.visibility = GONE
                media_play.visibility = GONE
            }
            VideoView.STATE_PAUSED -> {
                media_thumb.visibility = GONE
                media_play.visibility = VISIBLE
            }
            VideoView.STATE_PREPARED -> {}
            VideoView.STATE_ERROR -> Toast.makeText(context, R.string.dkplayer_error_message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPlayerStateChanged(playerState: Int) { }
    override fun setProgress(duration: Int, position: Int) { }
    override fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) { }
    override fun onLockStateChanged(isLocked: Boolean) { }
}