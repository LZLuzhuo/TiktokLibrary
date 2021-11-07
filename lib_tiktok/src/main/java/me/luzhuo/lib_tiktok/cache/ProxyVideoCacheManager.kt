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
package me.luzhuo.lib_tiktok.cache

import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import com.danikula.videocache.StorageUtils

/**
 * 视频缓存管理
 */
object ProxyVideoCacheManager {
    private var sharedProxy: HttpProxyCacheServer? = null

    fun getProxy(context: Context): HttpProxyCacheServer {
        if (sharedProxy == null) sharedProxy = newProxy(context)
        return sharedProxy!!
    }

    private fun newProxy(context: Context): HttpProxyCacheServer {
        return HttpProxyCacheServer
            .Builder(context)
            .maxCacheSize(512 * 1024 * 1024) // 512MB
            .build()
    }

    /**
     * 删除所有缓存文件
     * @return 返回缓存是否删除成功
     */
    fun clearAllCache(context: Context): Boolean {
        getProxy(context)
        return StorageUtils.deleteFiles(sharedProxy?.cacheRoot)
    }

    /**
     * 删除url对应默认缓存文件
     * @return 返回缓存是否删除成功
     */
    fun clearDefaultCache(context: Context, url: String): Boolean {
        getProxy(context)
        val pathTmp = sharedProxy?.getTempCacheFile(url)
        val path = sharedProxy?.getCacheFile(url)
        return StorageUtils.deleteFile(pathTmp?.absolutePath) && StorageUtils.deleteFile(path?.absolutePath)
    }
}