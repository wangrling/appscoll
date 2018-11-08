package com.android.home.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

public class XfermodesFragment extends Fragment {

    // Create a bitmap with a circle, used for the "dst" image.
    static Bitmap makeDst(int w, int h) {

        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bm);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFFFFCC44);

        c.drawOval(new RectF(0, 0, w * 3/4,
                h * 3/4), p);

        return bm;
    }

    // Create a bitmap with a rect, used for the "src" image.
    static Bitmap makeSrc(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bm);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFF66AAFF);

        c.drawRect(w/3, h/3, w * 19/20, h* 19/20, p);

        return bm;
    }

    @Nullable
    @android.support.annotation.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable @android.support.annotation.Nullable ViewGroup container, Bundle savedInstanceState) {


        return new XfermodesView(getActivity());
    }

    private static class XfermodesView extends View {

        private static final int W = 128;
        private static final int H = 128;

        // Number of samples per row.
        private static final int ROW_MAX = 4;

        private Bitmap mSrcB;
        private Bitmap mDstB;

        // background checker-board pattern.
        // Shader应该是指笔尖的图案。
        private Shader mBG;

        private static final Xfermode[] sModes = {
                new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
                new PorterDuffXfermode(PorterDuff.Mode.SRC),
                new PorterDuffXfermode(PorterDuff.Mode.DST),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
                new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
                new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
                new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
                new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
                new PorterDuffXfermode(PorterDuff.Mode.XOR),
                new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
                new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
                new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
                new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
        };

        private static final String[] sLabels = {
                "Clear", "Src", "Dst", "SrcOver",
                "DstOver", "SrcIn", "DstIn", "SrcOut",
                "DstOut", "SrcATop", "DstATop", "Xor",
                "Darken", "Lighten", "Multiply", "Screen"
        };

        public XfermodesView(Context context) {
            super(context);

            mSrcB = makeSrc(W, H);
            mDstB = makeDst(W, H);

            // Make a checker-board pattern.
            // 四像素，每个像素都用颜色填充。
            Bitmap bm = Bitmap.createBitmap(new int[] { 0xFFFFFFFF, 0xFFCCCCCC,
                            0xFFCCCCCC, 0xFFFFFFFF }, 2, 2,
                    Bitmap.Config.RGB_565);

            mBG = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

            Matrix m = new Matrix();
            m.setScale(6, 6);

            // 长和高都放大六倍。
            mBG.setLocalMatrix(m);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            // 锯齿的形状。
            Paint labelP = new Paint(Paint.ANTI_ALIAS_FLAG);
            // 中间对齐。
            labelP.setTextAlign(Paint.Align.CENTER);


            Paint paint = new Paint();
            paint.setFilterBitmap(false);

            // 平移(15, 35)像素。
            canvas.translate(15, 35);

            int x = 0;
            int y = 0;
            for (int i = 0; i < sModes.length; i++) {
                // Draw the border.
                // 绘制边框，和STROKE相对应的FILL模式。
                paint.setStyle(Paint.Style.STROKE);
                paint.setShader(null);
                canvas.drawRect(x - 0.5f, y - 0.5f,
                        x + W + 0.5f, y + H + 0.5f, paint);

                // Draw the checker-board pattern.
                paint.setStyle(Paint.Style.FILL);
                paint.setShader(mBG);
                canvas.drawRect(x, y,x + W, y + H, paint);

                // Draw the src/dst example into our offscreen bitmap.
                int sc = canvas.saveLayer(x, y, x + W, y + H, null,
                                Canvas.ALL_SAVE_FLAG);

                canvas.translate(x, y);

                canvas.drawBitmap(mDstB, 0, 0, paint);
                paint.setXfermode(sModes[i]);

                canvas.drawBitmap(mSrcB, 0, 0, paint);
                paint.setXfermode(null);

                canvas.restoreToCount(sc);

                // Draw the label.
                canvas.drawText(sLabels[i], x + W/2, y -labelP.getTextSize(), labelP);

                x += W + 10;

                // Wrap around when we've drawn enough for one row.
                if ((i % ROW_MAX) == ROW_MAX - 1) {
                    x = 0;
                    y += H + 30;
                }
            }
        }
    }
}
