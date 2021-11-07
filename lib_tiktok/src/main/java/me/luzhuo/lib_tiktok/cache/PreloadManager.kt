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
import android.util.Log
import com.danikula.videocache.HttpProxyCacheServer
import java.util.concurrent.Executors

/**
 * 抖音预加载工具，使用AndroidVideoCache实现
 */
class PreloadManager {
    private val TAG = PreloadManager::class.java.simpleName

    // 单线程池，按照添加顺序依次执行PreloadTask
    private val mExecutorService = Executors.newSingleThreadExecutor()
    // 保存正在预加载的PreloadTask
    private val mPreloadTasks = LinkedHashMap<String, PreloadTask>()

    // 标识是否需要预加载
    private var mIsStartPreload: Boolean = true

    private var mHttpProxyCacheServer: HttpProxyCacheServer

    companion object {
        // 预加载的大小，每个视频预加载512KB，这个参数可根据实际情况调整
        const val PRELOAD_LENGTH: Int = 512 * 1024
        private var sPreloadManager: PreloadManager? = null

        fun instance(context: Context): PreloadManager {
            if (sPreloadManager == null) {
                synchronized(PreloadManager::class.java) {
                    if (sPreloadManager == null) sPreloadManager = PreloadManager(context.applicationContext)
                }
            }
            return sPreloadManager!!
        }
    }

    private constructor(context: Context) {
        mHttpProxyCacheServer = ProxyVideoCacheManager.getProxy(context)
    }

    /**
     * 开始预加载
     * @param rawUrl 原始视频地址
     */
    fun addPreloadTask(rawUrl: String, position: Int) {
        if (isPreloaded(rawUrl)) return
        Log.e(TAG, "addPreloadTask: rawUrl: $rawUrl position: $position")

        val task = PreloadTask()
        task.mRawUrl = rawUrl
        task.mPosition = position
        task.mCacheServer = mHttpProxyCacheServer

        mPreloadTasks[rawUrl] = task
        // 开始预加载
        if (mIsStartPreload) task.executeOn(mExecutorService)
    }

    /**
     * 判断该播放地址是否已经预加载
     */
    private fun isPreloaded(rawUrl: String): Boolean {
        // 先判断是否有缓存文件，如果已经存在缓存文件，并且其大小大于1KB，则表示已经预加载完成了
        val cacheFile = mHttpProxyCacheServer.getCacheFile(rawUrl)
        if (cacheFile.exists()) {
            return if (cacheFile.length() >= 1024) {
                true
            } else {
                // 这种情况一般是缓存出错，把缓存删掉，重新缓存
                cacheFile.delete()
                false
            }
        }

        // 再判断是否有临时缓存文件，如果已经存在临时缓存文件，并且临时缓存文件超过了预加载大小，则表示已经预加载完成了
        val tempCacheFile = mHttpProxyCacheServer.getTempCacheFile(rawUrl)
        if (tempCacheFile.exists()) return tempCacheFile.length() >= PRELOAD_LENGTH

        return false
    }

    /**
     * 暂停预加载
     * 根据是否反向滑动取消在position之下或之上的PreloadTask
     *
     * @param position 当前滑到的位置
     * @param isReverseScroll 列表是否反向滑动
     */
    fun pausePreload(position: Int, isReverseScroll: Boolean) {
        Log.e(TAG, "pausePreload: position: $position isReverseScroll: $isReverseScroll")
        mIsStartPreload = false
        mPreloadTasks.forEach {
            val task = it.value
            if (isReverseScroll) {
                if (task.mPosition >= position) task.cancel()
            } else {
                if (task.mPosition <= position) task.cancel()
            }
        }
    }

    /**
     * 恢复预加载
     * 根据是否反向滑动开始在position之下或之上的PreloadTask
     *
     * @param position        当前滑到的位置
     * @param isReverseScroll 列表是否反向滑动
     */
    fun resumePreload(position: Int, isReverseScroll: Boolean) {
        Log.e(TAG, "resumePreload: position: $position isReverseScroll: $isReverseScroll")
        mIsStartPreload = true
        mPreloadTasks.forEach {
            val task = it.value
            if (isReverseScroll) {
                if (task.mPosition < position) {
                    if (!isPreloaded(task.mRawUrl ?: "")) task.executeOn(mExecutorService)
                }
            } else {
                if (task.mPosition > position) {
                    if (!isPreloaded(task.mRawUrl ?: "")) task.executeOn(mExecutorService)
                }
            }
        }
    }

    /**
     * 通过原始地址取消预加载
     *
     * @param rawUrl 原始地址
     */
    fun removePreloadTask(rawUrl: String) {
        val task = mPreloadTasks[rawUrl]
        if (task != null) {
            task.cancel()
            mPreloadTasks.remove(rawUrl)
        }
    }

    /**
     * 取消所有的预加载
     */
    fun removeAllPreloadTask() {
        val iterator = mPreloadTasks.entries.iterator()
        while (iterator.hasNext()) {
            val task = iterator.next().value
            task.cancel()
            iterator.remove()
        }
    }

    fun getPlayUrl(rawUrl: String): String {
        val task = mPreloadTasks[rawUrl]
        task?.cancel()
        return if (isPreloaded(rawUrl)) mHttpProxyCacheServer.getProxyUrl(rawUrl)
        else rawUrl
    }
}