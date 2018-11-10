package com.android.home.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * View确定绘制的曲线，Fragment设置绘制的属性。
 */

public class FingerPaintFragment extends Fragment
        implements ColorPickerDialog.OnColorChangedListener,
        View.OnClickListener {

    @Override
    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.finger_paint, container, false);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.finger_container);
        layout.addView(new FingerPaintView(getContext()));

        view.findViewById(R.id.finger_color).setOnClickListener(this);
        view.findViewById(R.id.finger_emboss).setOnClickListener(this);
        view.findViewById(R.id.finger_blur).setOnClickListener(this);
        view.findViewById(R.id.finger_erase).setOnClickListener(this);
        view.findViewById(R.id.finger_top).setOnClickListener(this);

        return view;
    }

    private Paint mPaint;

    // 浮雕
    private MaskFilter mEmboss;

    // 模糊
    private MaskFilter mBlur;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1},
                0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finger_color: {
                new ColorPickerDialog(getContext(), this, mPaint.getColor()).show();
                break;
            }
            case R.id.finger_emboss: {
                if (mPaint.getMaskFilter() != mEmboss) {
                    mPaint.setMaskFilter(mEmboss);
                } else {
                    mPaint.setMaskFilter(null);
                }
                break;
            }
            case R.id.finger_blur: {
                if (mPaint.getMaskFilter() != mBlur) {
                    mPaint.setMaskFilter(mBlur);
                } else {
                    mPaint.setMaskFilter(null);
                }
                break;
            }
            case R.id.finger_erase: {
                mPaint.setXfermode(new PorterDuffXfermode(
                        PorterDuff.Mode.CLEAR
                ));
                break;
            }
            case R.id.finger_top: {
                mPaint.setXfermode(new PorterDuffXfermode(
                        PorterDuff.Mode.SRC_ATOP
                ));
                mPaint.setAlpha(0x80);
                break;
            }
        }
    }

    public class FingerPaintView extends View {

        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;

        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;

        public FingerPaintView(Context c) {
            super(c);

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        public FingerPaintView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFAAAAAA);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            // 重点。
            canvas.drawPath(mPath, mPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);

            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

                // 中间点的曲线。
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);

                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);

            // Commit the path to our offscreen.
            mCanvas.drawPath(mPath, mPaint);

            // Kill this so we don't double draw.
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}
