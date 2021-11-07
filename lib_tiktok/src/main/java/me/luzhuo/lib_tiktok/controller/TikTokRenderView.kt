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
package me.luzhuo.lib_tiktok.controller

import android.graphics.Bitmap
import android.view.View
import com.dueeeke.videoplayer.player.AbstractPlayer
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.render.IRenderView

/**
 * TikTok专用RenderView，横屏视频默认显示，竖屏视频居中裁剪
 * 使用代理模式实现
 */
class TikTokRenderView(val mProxyRenderView: IRenderView): IRenderView {

    override fun setScaleType(scaleType: Int) {
        // 置空，不要让外部去设置ScaleType
    }

    override fun doScreenShot(): Bitmap = mProxyRenderView.doScreenShot()

    override fun setVideoRotation(degree: Int) {
        mProxyRenderView.setVideoRotation(degree)
    }

    override fun attachToPlayer(player: AbstractPlayer) {
        mProxyRenderView.attachToPlayer(player)
    }

    override fun getView(): View = mProxyRenderView.view

    override fun release() {
        mProxyRenderView.release();
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            mProxyRenderView.setVideoSize(videoWidth, videoHeight)
            if (videoHeight > videoWidth) mProxyRenderView.setScaleType(VideoView.SCREEN_SCALE_CENTER_CROP) // 竖屏视频，使用居中裁剪
            else mProxyRenderView.setScaleType(VideoView.SCREEN_SCALE_DEFAULT) // 横屏视频，使用默认模式
        }
    }
}