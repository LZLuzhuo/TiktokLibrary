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
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;

public class WansuoClassicsFooter extends BaseClassicsAbstract<WansuoClassicsFooter> implements RefreshFooter {

    protected String mTextPulling = "上拉加载更多";//"上拉加载更多";
    protected String mTextRelease = "松开加载更多";//"释放立即加载";
    protected String mTextLoading = "正在加载...";//"正在加载...";
    protected String mTextRefreshing = "正在刷新...";//"正在刷新...";
    protected String mTextFinish = "加载完成";//"加载完成";
    protected String mTextFailed = "加载失败";//"加载失败";
    protected String mTextNothing = "我是有底线的";//"没有更多数据了";

    protected boolean mNoMoreData = false;
    private View noDataView;

    public WansuoClassicsFooter(Context context) {
        this(context, null);
    }

    public WansuoClassicsFooter(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        View.inflate(context, R.layout.wansuo_classics_footer, this);

        final View thisView = this;
        noDataView = thisView.findViewById(R.id.srl_classics_nodata);

        mTitleText = thisView.findViewById(R.id.srl_classics_title);
        mTitleText.setText(thisView.isInEditMode() ? mTextLoading : mTextPulling);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {  }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        /*
         * 2020-5-15 修复BUG
         * https://github.com/scwang90/SmartRefreshLayout/issues/1003
         * 修复 没有更多数据之后 loading 还在显示问题
         */
        // super.onFinish(layout, success);
        if (!mNoMoreData) {
            mTitleText.setText(success ? mTextFinish : mTextFailed);
            return mFinishDuration;
        }
        return 0;
    }

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mNoMoreData != noMoreData) {
            mNoMoreData = noMoreData;
            if (noMoreData) {
                noDataView.setVisibility(View.VISIBLE);
                mTitleText.setVisibility(View.INVISIBLE);
                mTitleText.setText(mTextNothing);
            } else {
                noDataView.setVisibility(View.INVISIBLE);
                mTitleText.setVisibility(View.VISIBLE);
                mTitleText.setText(mTextPulling);
            }
        }
        return true;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        if (!mNoMoreData) {
            switch (newState) {
                case None:
                case PullUpToLoad:
                    mTitleText.setText(mTextPulling);
                    break;
                case Loading:
                case LoadReleased:
                    mTitleText.setText(mTextLoading);
                    break;
                case ReleaseToLoad:
                    mTitleText.setText(mTextRelease);
                    break;
                case Refreshing:
                    mTitleText.setText(mTextRefreshing);
                    break;
            }
        }
    }

}
