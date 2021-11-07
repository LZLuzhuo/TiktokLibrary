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
package me.luzhuo.lib_tiktok.bean

import java.io.Serializable

data class TiktokBean (

    /**
     * 视频封面
     */
    val coverImgUrl: String,

    /**
     * 视频地址
     */
    val videoDownloadUrl: String,

    /**
     * 扩展的数据
     */
    val expandData: Any?

): Serializable