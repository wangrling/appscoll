package com.android.home.threelibs.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

public class TextAlignFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return new TextAlignView(getContext());
    }

    private static class TextAlignView extends View {
        private Paint mPaint;

        private float mX;
        private float[] mPos;

        private Path mPath;
        private Paint mPathPaint;

        private static final int DY = 30;
        private static final String TEXT_L = "Left";
        private static final String TEXT_C = "Center";
        private static final String TEXT_R = "Right";
        private static final String POSTEXT = "Positioned";
        private static final String TEXTONPATH = "Along a path";

        private static void makePath(Path p) {
            p.moveTo(10, 0);
            p.cubicTo(100, -50, 200, 50, 300, 0);
        }

        // 每个字母都需要一个位置。
        private float[] buildTextPositions(String text, float y, Paint paint) {
            float[] widths = new float[text.length()];

            // Initially get the widths for each char.
            int n = paint.getTextWidths(text, widths);
            // Now populate the array, interleaving spaces for the Y values.
            float[] pos = new float[n * 2];
            float accumulatedX = 0;
            for (int i = 0; i < n; i++) {
                // (x, y)的值。
                pos[i*2 + 0] = accumulatedX;
                pos[i*2 + 1] = y;
                accumulatedX += widths[i];
            }

            return pos;
        }

        public TextAlignView(Context context) {
            super(context);
            setFocusable(true);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(30);
            mPaint.setTypeface(Typeface.SERIF);

            mPos = buildTextPositions(POSTEXT, 0, mPaint);

            mPath = new Path();
            makePath(mPath);

            mPathPaint = new Paint();
            mPathPaint.setAntiAlias(true);
            mPathPaint.setColor(0x800000FF);
            mPathPaint.setStyle(Paint.Style.STROKE);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            canvas.drawColor(Color.WHITE);

            Paint p = mPaint;
            float x = mX;
            float y = 0;
            float[] pos = mPos;

            // Draw the normal strings.
            p.setColor(0x80FF0000);
            canvas.drawLine(x, y, x, y + DY*3, p);
            p.setColor(Color.BLACK);

            // 左对齐
            canvas.translate(0, DY);
            p.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(TEXT_L, x, y, p);

            // 中间对齐
            canvas.translate(0, DY);
            p.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(TEXT_C, x, y, p);

            // 右对齐
            // translate会产生累加。
            canvas.translate(0, DY);
            p.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(TEXT_R, x, y, p);

            canvas.translate(100, DY*2);


            // Now draw the text on path.
            canvas.translate(-100, DY*2);

            canvas.drawPath(mPath, mPathPaint);
            p.setTextAlign(Paint.Align.LEFT);
            canvas.drawTextOnPath(TEXTONPATH, mPath, 0, 0, p);

            canvas.translate(0, DY*1.5f);
            canvas.drawPath(mPath, mPathPaint);
            p.setTextAlign(Paint.Align.CENTER);
            canvas.drawTextOnPath(TEXTONPATH, mPath, 0, 0, p);

            canvas.translate(0, DY*1.5f);
            canvas.drawPath(mPath, mPathPaint);
            p.setTextAlign(Paint.Align.RIGHT);
            canvas.drawTextOnPath(TEXTONPATH, mPath, 0, 0, p);
        }

        @Override
        protected void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh);
            // 把基准线放置在屏幕的中间。
            mX = w * 0.5f;  // remember the center of the screen
        }
    }
}
