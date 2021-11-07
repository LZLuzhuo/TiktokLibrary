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
package com.jincai.lib_refresh;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;

public class WansuoClassicsHeader extends BaseClassicsAbstract<WansuoClassicsHeader> implements RefreshHeader {
    protected String mTextPulling = "下拉刷新";//"下拉可以刷新";
    protected String mTextRefreshing = "正在刷新";//"正在刷新...";
    protected String mTextLoading = "正在加载";//"正在加载...";
    protected String mTextRelease = "松开刷新";//"释放立即刷新";
    protected String mTextFinish = "刷新完成";//"刷新完成";
    protected String mTextFailed = "刷新失败";//"刷新失败";
    protected String mTextSecondary = "更上一层楼";//"释放进入二楼";

    public WansuoClassicsHeader(Context context) {
        this(context, null);
    }

    public WansuoClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        View.inflate(context, R.layout.wansuo_classics_header, this);

        final View thisView = this;
//        mProgressView = thisView.findViewById(R.id.srl_classics_progress);

        mTitleText = thisView.findViewById(R.id.srl_classics_title);
        mTitleText.setText(thisView.isInEditMode() ? mTextRefreshing : mTextPulling);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        type = 停止;
        if (success) {
            mTitleText.setText(mTextFinish);
        } else {
            mTitleText.setText(mTextFailed);
        }
        return mFinishDuration;
    }

    private static final int 停止 = 0, 慢慢转 = 1, 快快转 = 2;
    private int type = 停止;
//    Handler mainThread = new Handler(Looper.getMainLooper());
//    private void startAnim(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(type != 停止) {
//                    SystemClock.sleep(10);
//                    mainThread.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mProgressView.setRotation(mProgressView.getRotation() + (type == 慢慢转 ? 5 : 10 /*50*/));
//                        }
//                    });
//                }
//            }
//        }).start();
//    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
//        mProgressView.setVisibility(View.VISIBLE);
        switch (newState) {
            case None:
                break;
            case PullDownToRefresh:
                mTitleText.setText(mTextPulling);
                type = 慢慢转;
//                startAnim();
                break;
            case Refreshing:
                type = 快快转;
                break;
            case RefreshReleased:
                mTitleText.setText(mTextRefreshing);
                type = 快快转;
                break;
            case ReleaseToRefresh:
                mTitleText.setText(mTextRelease);
                type = 慢慢转;
                break;
            case ReleaseToTwoLevel:
                mTitleText.setText(mTextSecondary);
                type = 慢慢转;
                break;
            case Loading:
                mTitleText.setText(mTextLoading);
                type = 快快转;
                break;
        }
    }
}
