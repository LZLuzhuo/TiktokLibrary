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
package com.example.tiktokdemo.tiktok.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiktokdemo.R
import com.example.tiktokdemo.tiktok.bean.TikTokExt
import kotlinx.android.synthetic.main.item_tiktok.view.*
import kotlinx.android.synthetic.main.item_tiktok_ext.view.*
import me.luzhuo.lib_tiktok.bean.TiktokBean
import me.luzhuo.lib_tiktok.cache.PreloadManager

/**
 * TikTok供外部使用的Adapter
 */
class TikTokAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context
    val mVideoBeans: ArrayList<TiktokBean> = ArrayList()
    private var callback: OnTikTokCallback? = null

    fun setData(data: List<TiktokBean>) {
        this.mVideoBeans.clear()
        mVideoBeans.addAll(data)
        this.notifyDataSetChanged()
    }

    fun addData(data: List<TiktokBean>) {
        val size = mVideoBeans.size
        mVideoBeans.addAll(data)
        this.notifyItemRangeChanged(size, data.size)
    }

    var currentPage: Int = 1
    var isRefresh: Boolean = true

    fun reset() {
        currentPage = 1
        isRefresh = true
    }

    fun addPage() {
        currentPage += 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.context = parent.context
        return RecyclerHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tiktok, parent, false))
    }

    override fun getItemCount(): Int = mVideoBeans.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
         (holder as RecyclerHolder).bindData(mVideoBeans[position])
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as RecyclerHolder).unbindData(mVideoBeans[holder.mPosition])
    }

    inner class RecyclerHolder(item: View) : RecyclerView.ViewHolder(item) {
        var mPosition: Int = 0
        var item_tiktok_video = item.item_tiktok_video
        var media_thumb = item_tiktok_video.findViewById<ImageView>(R.id.media_thumb)
        var media_controller = item_tiktok_video.findViewById<FrameLayout>(R.id.media_controller)

        var tiktok_dianzan = item.tiktok_dianzan
        var tiktok_dianzan_count = item.tiktok_dianzan_count

        init {
            item.tag = this
            tiktok_dianzan.setOnClickListener { callback?.onLike(layoutPosition, mVideoBeans!![layoutPosition]) }
        }

        fun bindData(data: TiktokBean) {
            val itemData = data.expandData as? TikTokExt

            PreloadManager.instance(context).addPreloadTask(data.videoDownloadUrl, layoutPosition)  // 开始预加载
            Glide.with(context)
                .load(data.coverImgUrl)
                .placeholder(android.R.color.black)
                .into(media_thumb)

            // === update view ===
            tiktok_dianzan_count.text = "${itemData?.likeCount ?: 0}"
            // === update view ===

            mPosition = layoutPosition
        }

        fun unbindData(data: TiktokBean) {
            PreloadManager.instance(context).removePreloadTask(data.videoDownloadUrl) // 取消预加载
        }
    }

    interface OnTikTokCallback {
        fun onLike(position: Int, data: TiktokBean)
    }
    fun setOnTikTokCallback(callback: OnTikTokCallback) {
        this.callback = callback
    }
}