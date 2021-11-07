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
package me.luzhuo.lib_tiktok

import com.dueeeke.videoplayer.player.VideoViewConfig
import com.dueeeke.videoplayer.player.VideoViewManager
import com.dueeeke.videoplayer.BuildConfig;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory

object TikTokManager {
    /**
     * 初始化 dkplayer
     * 在Application里调用, 如果不想使用ijk框架, 可以不进行初始化
     *
     * implementation "com.github.dueeeke.dkplayer:player-ijk:3.2.6"
     */
    fun init(){
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
            .setLogEnabled(BuildConfig.DEBUG)
            .setPlayerFactory(IjkPlayerFactory.create())
            .build())
    }
}