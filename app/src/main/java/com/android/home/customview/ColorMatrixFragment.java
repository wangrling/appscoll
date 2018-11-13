package com.android.home.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import com.android.home.R;

public class ColorMatrixFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return new ColorMatrixView(getActivity());
    }

    private static class ColorMatrixView extends View {

        private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Bitmap mBitmap;
        private float mAngle;

        private static void setTranslate(ColorMatrix cm, float dr, float dg,
                                         float db, float da) {
            cm.set(new float[] {
                    2, 0, 0, 0, dr,
                    0, 2, 0, 0, dg,
                    0, 0, 2, 0, db,
                    0, 0, 0, 1, da });
        }

        private static void setContrast(ColorMatrix cm, float contrast) {
            float scale = contrast + 1.f;
            float translate = (-.5f * scale + .5f) * 255.f;
            cm.set(new float[] {
                    scale, 0, 0, 0, translate,
                    0, scale, 0, 0, translate,
                    0, 0, scale, 0, translate,
                    0, 0, 0, 1, 0 });
        }

        private static void setContrastTranslateOnly(ColorMatrix cm, float contrast) {
            float scale = contrast + 1.f;
            float translate = (-.5f * scale + .5f) * 255.f;
            cm.set(new float[] {
                    1, 0, 0, 0, translate,
                    0, 1, 0, 0, translate,
                    0, 0, 1, 0, translate,
                    0, 0, 0, 1, 0 });
        }

        private static void setContrastScaleOnly(ColorMatrix cm, float contrast) {
            float scale = contrast + 1.f;
            float translate = (-.5f * scale + .5f) * 255.f;
            cm.set(new float[] {
                    scale, 0, 0, 0, 0,
                    0, scale, 0, 0, 0,
                    0, 0, scale, 0, 0,
                    0, 0, 0, 1, 0 });
        }

        ColorMatrixView(Context context) {
            super(context);

            mBitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.balloons);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint = mPaint;
            float x = 20;
            float y = 20;

            canvas.drawColor(Color.WHITE);

            paint.setColorFilter(null);
            canvas.drawBitmap(mBitmap, x, y, paint);

            ColorMatrix cm = new ColorMatrix();

            mAngle += 2;
            if (mAngle > 180) {
                mAngle = 0;
            }

            //convert our animated angle [-180...180] to a contrast value of [-1..1]
            float contrast = mAngle / 180.f;

            setContrast(cm, contrast);
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(mBitmap, x + mBitmap.getWidth() + 10, y, paint);

            setContrastScaleOnly(cm, contrast);
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(mBitmap, x, y + mBitmap.getHeight() + 10, paint);

            setContrastTranslateOnly(cm, contrast);
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(mBitmap, x, y + 2*(mBitmap.getHeight() + 10),
                    paint);

            invalidate();
        }
    }
}
