/* Copyright 2022 Luzhuo. All rights reserved.
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
package me.luzhuo.lib_tiktok.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Description: TikTok 的视频封面
 * @Author: Luzhuo
 * @Creation Date: 2022/1/10 22:42
 * @Copyright: Copyright 2022 Luzhuo. All rights reserved.
 **/
public class TikTokImageView extends AppCompatImageView {
    private Matrix matrix;
    private Paint paint;
    private Rect srcR;
    private RectF dstR;

    public TikTokImageView(Context context) {
        super(context);
        init();
    }

    public TikTokImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TikTokImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        matrix = new Matrix();
        paint = new Paint();
        srcR = new Rect();
        dstR = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        matrix.reset();

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        if (viewWidth == 0 || viewHeight == 0) return;

        Drawable drawable = getDrawable();
        if (drawable == null) return;
        if (!(drawable instanceof BitmapDrawable)) return;
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (bitmap == null) return;

        // 获取图片的宽高
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // 计算缩放比例
        if (bitmapHeight > bitmapWidth) { // CenterCrop

            // 选择最大的比例, 不论宽或高都要占满, 甚至超出
            float scaleWidth = (viewWidth * 1f) / (bitmapWidth * 1f);
            float scaleHeight = (viewHeight * 1f) / (bitmapHeight * 1f);
            float scale = Math.max(scaleWidth, scaleHeight);
            matrix.postScale(scale, scale);

            // 计算xy轴的平移点 = 间隙 / 2
            int dx = (int) ((viewWidth - bitmapWidth * scale) * 0.5f + 0.5f);
            int dy = (int) ((viewHeight - bitmapHeight * scale) * 0.5f + 0.5f);
            matrix.postTranslate(dx, dy);

        } else { // 默认大小

            // 选择最小的比例, 只让宽或高其中之一占满
            float scale = Math.min((float) viewWidth / (float) bitmapWidth, (float) viewHeight / (float) bitmapHeight);
            matrix.postScale(scale, scale);

            // 计算xy轴的平移点 = 间隙 / 2
            int dx = (int) ((viewWidth - bitmapWidth * scale) * 0.5f + 0.5f);
            int dy = (int) ((viewHeight - bitmapHeight * scale) * 0.5f + 0.5f);
            matrix.postTranslate(dx, dy);

        }

        // 判断变换后是否认为矩形, 如果不是则开启抗锯齿
        boolean transformed = !matrix.rectStaysRect();
        if (transformed) paint.setAntiAlias(true);

        srcR.set(0, 0, bitmapWidth, bitmapHeight);
        dstR.set(0, 0, bitmapWidth, bitmapHeight);
        canvas.concat(matrix);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, srcR, dstR, paint);
    }
}
