package com.example.tiktokdemo;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.jincai.lib_refresh.WansuoClassicsFooter;
import com.jincai.lib_refresh.WansuoClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshInitializer;

import me.luzhuo.lib_tiktok.TikTokManager;

public class MyApplication extends Application {

    static {
        // ======================================== SmartRefreshLayout ↓ ========================================
        SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
            @Override
            public void initialize(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setEnableLoadMoreWhenContentNotFull(false);
                layout.autoRefresh();
            }
        });
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new WansuoClassicsHeader(context);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new WansuoClassicsFooter(context);
            }
        });
        // ======================================== SmartRefreshLayout ↑ ========================================
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TikTokManager.INSTANCE.init();
    }
}
