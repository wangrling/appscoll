package com.android.home.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

public class UnicodeChartFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,@androidx.annotation.Nullable ViewGroup container, Bundle savedInstanceState) {
        return new UnicodeChartView(getActivity());
    }

    private static class UnicodeChartView extends View {

        private Paint mBigCharPaint;
        private Paint mLabelPaint;

        private final char[] mChars = new char[256];
        private final float[] mPos = new float[512];

        private int mBase;

        private static final int XMUL = 40;
        private static final int YMUL = 56;
        private static final int YBASE = 36;

        public UnicodeChartView(Context context) {
            super(context);

            setFocusable(true);
            setFocusableInTouchMode(true);

            mBigCharPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBigCharPaint.setTextSize(30);
            mBigCharPaint.setTextAlign(Paint.Align.CENTER);

            mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLabelPaint.setTextSize(16);
            mLabelPaint.setTextAlign(Paint.Align.CENTER);

            // the position array is the same for all charts
            float[] pos = mPos;
            int index = 0;
            for (int col = 0; col < 16; col++) {
                final float x = col * XMUL + 10;
                for (int row = 0; row < 16; row++) {
                    pos[index++] = x;
                    pos[index++] = row * YMUL + YBASE;
                }
            }
        }


        private float computeX(int index) {
            return (index >> 4) * XMUL + 10;
        }

        private float computeY(int index) {
            return (index & 0xF) * YMUL + YMUL;
        }

        private void drawChart(Canvas canvas, int base) {

            char[] chars = mChars;
            for (int i = 0; i < 256; i++) {
                int unichar = base + i;
                chars[i] = (char) unichar;

                canvas.drawText(Integer.toHexString(unichar),
                        computeX(i), computeY(i), mLabelPaint);
            }

            canvas.drawPosText(chars, 0, 256, mPos, mBigCharPaint);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            canvas.translate(4,4 );
            drawChart(canvas, mBase * 256);
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mBase > 0) {
                        mBase -= 1;
                        invalidate();
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    mBase += 1;
                    invalidate();
                    return true;

                default:
                    break;
            }
            return super.onKeyDown(keyCode, event);
        }
    }
}
