package com.android.home.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

public class ClippingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return new ClippingView(getActivity());
    }

    private static class ClippingView extends View {

        private Paint mPaint;
        private Path mPath;

        public ClippingView(Context context) {
            super(context);

            setFocusable(true);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(6);
            mPaint.setTextSize(16);
            mPaint.setTextAlign(Paint.Align.RIGHT);

            mPath = new Path();
        }

        private void drawScene(Canvas canvas) {
            canvas.clipRect(0, 0, 100, 100);

            canvas.drawColor(Color.WHITE);

            mPaint.setColor(Color.RED);
            canvas.drawLine(0, 0, 100, 100, mPaint);

            mPaint.setColor(Color.GREEN);
            canvas.drawCircle(30, 70, 30, mPaint);

            mPaint.setColor(Color.BLUE);
            canvas.drawText("Clipping", 100, 30, mPaint);
        }


        @Override protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.GRAY);

            canvas.save();
            canvas.translate(10, 10);
            drawScene(canvas);
            canvas.restore();

            canvas.save();
            canvas.translate(160, 10);
            canvas.clipRect(10, 10, 90, 90);
            canvas.clipRect(30, 30, 70, 70, Region.Op.DIFFERENCE);
            drawScene(canvas);
            canvas.restore();

            canvas.save();
            canvas.translate(10, 160);
            mPath.reset();
            canvas.clipPath(mPath); // makes the clip empty
            mPath.addCircle(50, 50, 50, Path.Direction.CCW);
            canvas.clipPath(mPath, Region.Op.REPLACE);
            drawScene(canvas);
            canvas.restore();

            canvas.save();
            canvas.translate(160, 160);
            canvas.clipRect(0, 0, 60, 60);
            canvas.clipRect(40, 40, 100, 100, Region.Op.UNION);
            drawScene(canvas);
            canvas.restore();

            canvas.save();
            canvas.translate(10, 310);
            canvas.clipRect(0, 0, 60, 60);
            canvas.clipRect(40, 40, 100, 100, Region.Op.XOR);
            drawScene(canvas);
            canvas.restore();

            canvas.save();
            canvas.translate(160, 310);
            canvas.clipRect(0, 0, 60, 60);
            canvas.clipRect(40, 40, 100, 100, Region.Op.REVERSE_DIFFERENCE);
            drawScene(canvas);
            canvas.restore();
        }
    }
}
