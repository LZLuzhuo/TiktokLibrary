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

import android.content.Context
import android.util.AttributeSet
import com.dueeeke.videoplayer.controller.BaseVideoController

class TikTokController @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseVideoController(context, attrs, defStyleAttr) {

    override fun getLayoutId(): Int = 0 // 网络警告的布局, 直接贴纸屏幕中间位置

    override fun showNetWarning(): Boolean {
        // false不显示移动网络播放警告, true显示
        return false
    }
}