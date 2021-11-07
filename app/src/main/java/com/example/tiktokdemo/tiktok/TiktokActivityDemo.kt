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
package com.example.tiktokdemo.tiktok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.dueeeke.videoplayer.player.AbstractPlayer
import com.dueeeke.videoplayer.player.VideoView
import com.example.tiktokdemo.R
import kotlinx.android.synthetic.main.activity_tiktok.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.tiktokdemo.tiktok.adapter.TikTokAdapter
import com.example.tiktokdemo.tiktok.bean.TikTokExt
import kotlinx.android.synthetic.main.layout_tiktop_top.*
import kotlinx.coroutines.flow.*
import me.luzhuo.lib_tiktok.bean.TiktokBean
import me.luzhuo.lib_tiktok.cache.PreloadManager
import me.luzhuo.lib_tiktok.controller.TikTokController
import me.luzhuo.lib_tiktok.controller.TikTokRenderViewFactory
import me.luzhuo.lib_tiktok.controller.TikTokSeekbarControlView

/**
 * 抖音Activity案例
 */
class TiktokActivityDemo : AppCompatActivity() {
    private val TAG = TiktokActivityDemo::class.java.simpleName
    private lateinit var mPreloadManager: PreloadManager
    private lateinit var tiktokAdapter: TikTokAdapter
    private lateinit var mVideoView: VideoView<AbstractPlayer>
    private lateinit var mController: TikTokController

    private var mCurPos: Int = 0  // 当前播放位置
    private var index: Int = 0 // 外部进来需要跳转的位置

    companion object {
        fun start(context: Context, index: Int = 0) {
            context.startActivity(Intent(context, TiktokActivityDemo::class.java).apply {
                putExtra("index", index)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tiktok)
        index = intent.getIntExtra("index", 0)

        initView()
        initData()
    }

    private fun initView() {
        return_back.setOnClickListener { finish() }

        // video view
        mPreloadManager = PreloadManager.instance(this)
        mVideoView = VideoView(this)
        mVideoView.setLooping(true)
        mVideoView.setRenderViewFactory(TikTokRenderViewFactory())
        mController = TikTokController(this).apply {
            // 添加进度条
            addControlComponent(TikTokSeekbarControlView(this@TiktokActivityDemo))
        }
        mVideoView.setVideoController(mController)

        // ViewPager
        vvp.offscreenPageLimit = 4
        tiktokAdapter = TikTokAdapter()
        vvp.post { vvp.setCurrentItem(index, false) }
        vvp.adapter = tiktokAdapter
        vvp.overScrollMode = View.OVER_SCROLL_NEVER
        vvp.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

            private var mCurItem: Int = 0
            private var mIsReverseScroll: Boolean = false // 是否反向滑动

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == mCurItem) return
                mIsReverseScroll = position < mCurItem
            }
            override fun onPageSelected(position: Int) {
                if (position == mCurPos) return
                startPlay(position)

                // 加载更多数据
                if(tiktokAdapter.mVideoBeans.size - 1 <= position) getMoreData(position)
            }
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) mCurItem = vvp.currentItem

                if (state == ViewPager2.SCROLL_STATE_IDLE) mPreloadManager.resumePreload(mCurPos, mIsReverseScroll)
                else mPreloadManager.pausePreload(mCurPos, mIsReverseScroll)
            }
        })
        vvp.post { startPlay(index) }
        tiktokAdapter.setOnTikTokCallback(object: TikTokAdapter.OnTikTokCallback{
            override fun onLike(position: Int, data: TiktokBean) {
                tiktokLike(data)
            }
        })
    }

    // 播放视频
    private fun startPlay(position: Int) {
        val mViewPagerImpl = vvp.getChildAt(0) as RecyclerView
        val count = mViewPagerImpl.childCount
        repeat(count) {
            val itemView = mViewPagerImpl.getChildAt(it)
            val viewHolder = itemView.tag as TikTokAdapter.RecyclerHolder
            if (viewHolder.mPosition == position) {
                mVideoView.release()
                removeViewFormParent(mVideoView)

                val tiktokBean = tiktokAdapter.mVideoBeans[position]
                val playUrl = mPreloadManager.getPlayUrl(tiktokBean.videoDownloadUrl)
                mVideoView.setUrl(playUrl)
                mController.addControlComponent(viewHolder.item_tiktok_video, true)
                viewHolder.media_controller.addView(mVideoView, 0)
                mVideoView.start()
                mCurPos = position

                // 及时刷新的数据, 比如浏览量
                lookCount(viewHolder.tiktok_dianzan_count, tiktokAdapter.mVideoBeans[position])
                return@repeat
            }
        }
    }

    private fun lookCount(lookTextView: TextView?, data: TiktokBean) {
        val itemData = data.expandData as? TikTokExt
        itemData?.likeCount = (itemData?.likeCount ?: 0) + 1
        lookTextView?.text = "${itemData?.likeCount ?: 0}"
    }

    /**
     * 将View从父控件中移除
     */
    private fun removeViewFormParent(v: View) {
        val parent = v.parent
        if (parent is FrameLayout) parent.removeView(v)
    }

    override fun onResume() {
        super.onResume()
        mVideoView.resume()
    }

    override fun onPause() {
        super.onPause()
        mVideoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView.release()
        mPreloadManager.removeAllPreloadTask()
    }

    private fun initData() {
        refreshLayout.finishRefresh()
        refreshLayout.setOnRefreshListener { getNewData() }
        getNewData()
    }

    private fun getNewData() {
        tiktokAdapter.reset()
        currentPosition = null
        refreshLayout.setEnableLoadMore(false)
        getData()
    }

    /**
     * 静默加载更多
     */
    private var currentPosition: Int? = null
    private fun getMoreData(position: Int) {
        if (currentPosition == position) return
        currentPosition = position
        getData()
    }


    private fun getData() {

        // 请求网络
        MainScope().launch {
            listOf(1).asFlow()
                .onEach {
                    delay(1000)
                }
                .flowOn(Dispatchers.IO)
                .catch {

                    // TODO 数据请求失败
                    if(tiktokAdapter.isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
                .collect {

                    // TODO 获取结果
                    val list = ArrayList<TiktokBean>()
                    repeat(10) {
                        list.add(TiktokBean("https://t7.baidu.com/it/u=4036010509,3445021118&fm=193&f=GIF", "http://data.luzhuo.me/data/video.mp4", TikTokExt(0)))
                    }

                    if (tiktokAdapter.isRefresh) { // 刷新
                        mVideoView.release()

                        tiktokAdapter.setData(list)
                        tiktokAdapter.isRefresh = false
                        refreshLayout.finishRefresh(true)

                        Toast.makeText(this@TiktokActivityDemo, "刷新视频列表", Toast.LENGTH_SHORT).show()
                    } else { // 加载更多, 使用隐藏式预加载
                        tiktokAdapter.addData(list)
                        refreshLayout.finishLoadMore(true)

                        Toast.makeText(this@TiktokActivityDemo, "加载更多视频文件: ${tiktokAdapter.currentPage}", Toast.LENGTH_SHORT).show()
                    }

                    if(list.size <= 0) {
                        refreshLayout.setEnableLoadMore(true)
                        refreshLayout.finishLoadMoreWithNoMoreData()
                    }
                    tiktokAdapter.addPage()
                    if (!mVideoView.isPlaying) vvp.post { startPlay(index) }
                }
        }
    }

    private fun tiktokLike(data: TiktokBean?) {
        val itemData = data?.expandData as? TikTokExt
        Log.e(TAG, "tiktokLike: ${itemData?.likeCount}" );
    }

}