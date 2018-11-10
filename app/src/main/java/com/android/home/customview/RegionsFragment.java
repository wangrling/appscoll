package com.android.home.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 不同的区域进行交互绘制。
 */

public class RegionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return new RegionsView(getContext());
    }

    private static class RegionsView extends View {

        private final Paint mPaint = new Paint();
        private final Rect mRect1 = new Rect();
        private final Rect mRect2 = new Rect();

        public RegionsView(Context context) {

            super(context);

            setFocusable(true);

            mPaint.setAntiAlias(true);
            mPaint.setTextSize(16);
            mPaint.setTextAlign(Paint.Align.CENTER);

            mRect1.set(10, 10, 100, 80);
            mRect2.set(50, 50, 130, 110);
        }

        private void drawOriginalRects(Canvas canvas, int alpha) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.RED);
            mPaint.setAlpha(alpha);
            drawCentered(canvas, mRect1, mPaint);

            mPaint.setColor(Color.BLUE);
            mPaint.setAlpha(alpha);
            drawCentered(canvas, mRect2, mPaint);

            // Restore style.
            mPaint.setStyle(Paint.Style.FILL);
        }


        private static void drawCentered(Canvas c, Rect r, Paint p) {
            float inset = p.getStrokeWidth() * 0.5f;
            if (inset == 0) {
                inset = 0.5f;
            }
            c.drawRect(r.left + inset, r.top + inset,
                    r.right - inset, r.bottom - inset, p);
        }

        private void drawRgn(Canvas canvas, int color, String str, Region.Op op) {
            if (str != null) {
                mPaint.setColor(Color.BLACK);
                canvas.drawText(str, 80, 24, mPaint);
            }

            Region rgn = new Region();
            rgn.set(mRect1);
            rgn.op(mRect2, op);

            mPaint.setColor(color);
            RegionIterator iter = new RegionIterator(rgn);
            Rect r = new Rect();

            canvas.translate(0, 30);
            mPaint.setColor(color);
            while (iter.next(r)) {
                canvas.drawRect(r, mPaint);
            }
            drawOriginalRects(canvas, 0x80);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.GRAY);

            canvas.save();
            canvas.translate(80, 5);
            drawOriginalRects(canvas, 0xFF);
            // 恢复到save时的状态。
            canvas.restore();

            mPaint.setStyle(Paint.Style.FILL);

            canvas.save();
            canvas.translate(0, 140);
            drawRgn(canvas, Color.RED, "Union", Region.Op.UNION);
            canvas.restore();

            canvas.save();
            canvas.translate(0, 280);
            drawRgn(canvas, Color.BLUE, "Xor", Region.Op.XOR);
            canvas.restore();

            canvas.save();
            canvas.translate(160, 140);
            drawRgn(canvas, Color.GREEN, "Difference", Region.Op.DIFFERENCE);
            canvas.restore();

            canvas.save();
            canvas.translate(160, 280);
            drawRgn(canvas, Color.WHITE, "Intersect", Region.Op.INTERSECT);
            canvas.restore();
        }
    }
}
