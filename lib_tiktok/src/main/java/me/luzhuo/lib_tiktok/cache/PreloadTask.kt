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

import android.util.Log
import com.danikula.videocache.HttpProxyCacheServer
import java.io.BufferedInputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService

/**
 * 缓存任务
 */
class PreloadTask: Runnable {
    private val TAG = PreloadTask::class.java.simpleName

    // 原始地址
    var mRawUrl: String? = null

    // 列表中的位置
    var mPosition: Int = 0

    // VideoCache服务器
    var mCacheServer: HttpProxyCacheServer? = null

    // 是否被取消
    var mIsCanceled: Boolean = false

    // 是否正在预加载
    var mIsExecuted: Boolean = false

    override fun run() {
        if (!mIsCanceled) start()
        mIsExecuted = false
        mIsCanceled = false
    }

    // 开始预加载
    private fun start() {
        Log.e(TAG, "开始预加载: $mPosition")
        var connection: HttpURLConnection? = null
        try {

            // 获取HttpProxyCacheServer的代理地址
            val proxyUrl = mCacheServer?.getProxyUrl(mRawUrl)
            val url = URL(proxyUrl)
            connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 5_000
                readTimeout = 5_000
            }
            val ins = BufferedInputStream(connection.inputStream)

            var read = -1
            val bytes = ByteArray(8 * 1024)
            var length = ins.read(bytes)
            while (length != -1){
                read += length
                if (mIsCanceled || read >= PreloadManager.PRELOAD_LENGTH) {
                    Log.e(TAG, "结束预加载: $mPosition")
                    connection.disconnect()
                    break
                }
                length = ins.read(bytes)
            }

            // 这种情况一般是预加载出错了，删掉缓存
            if (read == -1) {
                Log.e(TAG, "预加载失败: $mPosition")
                val cacheFile = mCacheServer?.getCacheFile(mRawUrl)
                if (cacheFile?.exists() == true) cacheFile.delete()
            }

        } catch (e: Exception) {
            Log.e(TAG, "异常结束预加载: $mPosition")
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * 将预加载任务提交到线程池，准备执行
     */
    fun executeOn(executorService: ExecutorService) {
        if (mIsExecuted) return
        mIsExecuted = true
        executorService.submit(this)
    }

    /**
     * 取消预加载任务
     */
    fun cancel() {
        if (mIsExecuted) mIsCanceled = true
    }
}