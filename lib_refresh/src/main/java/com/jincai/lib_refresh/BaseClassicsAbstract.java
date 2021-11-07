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
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.scwang.smart.drawable.PaintDrawable;
import com.scwang.smart.refresh.layout.api.RefreshComponent;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import static android.view.View.MeasureSpec.EXACTLY;

public abstract class BaseClassicsAbstract<T extends BaseClassicsAbstract>  extends SimpleComponent implements RefreshComponent {

    protected TextView mTitleText;
    protected ImageView mArrowView;
    protected ImageView mProgressView;

    protected RefreshKernel mRefreshKernel;
    protected PaintDrawable mArrowDrawable;
    protected PaintDrawable mProgressDrawable;

    protected boolean mSetAccentColor;
    protected boolean mSetPrimaryColor;
    protected int mBackgroundColor;
    protected int mFinishDuration = 500;
    protected int mPaddingTop = 20;
    protected int mPaddingBottom = 20;
    protected int mMinHeightOfContent = 0;

    //<editor-fold desc="RelativeLayout">
    public BaseClassicsAbstract(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSpinnerStyle = SpinnerStyle.Translate;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final View thisView = this;
        if (mMinHeightOfContent == 0) {
            mPaddingTop = thisView.getPaddingTop();
            mPaddingBottom = thisView.getPaddingBottom();
            if (mPaddingTop == 0 || mPaddingBottom == 0) {
                int paddingLeft = thisView.getPaddingLeft();
                int paddingRight = thisView.getPaddingRight();
                mPaddingTop = mPaddingTop == 0 ? SmartUtil.dp2px(20) : mPaddingTop;
                mPaddingBottom = mPaddingBottom == 0 ? SmartUtil.dp2px(20) : mPaddingBottom;
                thisView.setPadding(paddingLeft, mPaddingTop, paddingRight, mPaddingBottom);
            }
            final ViewGroup thisGroup = this;
            thisGroup.setClipToPadding(false);
        }
        if (View.MeasureSpec.getMode(heightMeasureSpec) == EXACTLY) {
            final int parentHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            if (parentHeight < mMinHeightOfContent) {
                final int padding = (parentHeight - mMinHeightOfContent) / 2;
                thisView.setPadding(thisView.getPaddingLeft(), padding, thisView.getPaddingRight(), padding);
            } else {
                thisView.setPadding(thisView.getPaddingLeft(), 0, thisView.getPaddingRight(), 0);
            }
        } else {
            thisView.setPadding(thisView.getPaddingLeft(), mPaddingTop, thisView.getPaddingRight(), mPaddingBottom);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMinHeightOfContent == 0) {
            final ViewGroup thisGroup = this;
            for (int i = 0; i < thisGroup.getChildCount(); i++) {
                final int height = thisGroup.getChildAt(i).getMeasuredHeight();
                if (mMinHeightOfContent < height) {
                    mMinHeightOfContent = height;
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mArrowView != null) mArrowView.animate().cancel();
        if(mProgressView != null){
            mProgressView.animate().cancel();
            final Drawable drawable = mProgressView.getDrawable();
            if (drawable instanceof Animatable) {
                if (((Animatable) drawable).isRunning()) {
                    ((Animatable) drawable).stop();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgroundFor(this, mBackgroundColor);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        final View progressView = mProgressView;
        if(progressView != null) {
            if (progressView.getVisibility() != View.VISIBLE) {
                progressView.setVisibility(View.VISIBLE);
                Drawable drawable = mProgressView.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                } else {
                    progressView.animate().rotation(36000).setDuration(100000);
                }
            }
        }
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        onStartAnimator(refreshLayout, height, maxDragHeight);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        final View progressView = mProgressView;
        if(progressView != null) {
            Drawable drawable = mProgressView.getDrawable();
            if (drawable instanceof Animatable) {
                if (((Animatable) drawable).isRunning()) {
                    ((Animatable) drawable).stop();
                }
            } else {
                progressView.animate().rotation(0).setDuration(0);
            }
            progressView.setVisibility(View.GONE);
        }
        return mFinishDuration;//延迟500毫秒之后再弹回
    }

    @Override
    public void setPrimaryColors(@ColorInt int... colors) {
        if (colors.length > 0) {
            final View thisView = this;
            if (!(thisView.getBackground() instanceof BitmapDrawable) && !mSetPrimaryColor) {
                setPrimaryColor(colors[0]);
                mSetPrimaryColor = false;
            }
            if (!mSetAccentColor) {
                if (colors.length > 1) {
                    setAccentColor(colors[1]);
                }
                mSetAccentColor = false;
            }
        }
    }

    public T setPrimaryColor(@ColorInt int primaryColor) {
        mSetPrimaryColor = true;
        mBackgroundColor = primaryColor;
        if (mRefreshKernel != null) {
            mRefreshKernel.requestDrawBackgroundFor(this, primaryColor);
        }
        return self();
    }

    public T setAccentColor(@ColorInt int accentColor) {
        mSetAccentColor = true;
        if(mTitleText != null) mTitleText.setTextColor(accentColor);
        if (mArrowDrawable != null) {
            mArrowDrawable.setColor(accentColor);
            if(mArrowView != null) mArrowView.invalidateDrawable(mArrowDrawable);
        }
        if (mProgressDrawable != null) {
            mProgressDrawable.setColor(accentColor);
            if(mProgressView != null) mProgressView.invalidateDrawable(mProgressDrawable);
        }
        return self();
    }
}