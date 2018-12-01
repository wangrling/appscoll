package com.android.home.threelibs.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import com.android.home.R;

public class BitmapDecodeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return new BitmapDecodeView(getActivity());
    }

    private static class BitmapDecodeView extends View {

        private Bitmap mBitmap;
        private Bitmap mBitmap2;
        private Bitmap mBitmap3;
        private Bitmap mBitmap4;
        private Drawable mDrawable;

        private Movie mMovie;
        private long mMovieStart;

        public BitmapDecodeView(Context context) {
            super(context);
            setFocusable(true);

            java.io.InputStream is;
            is = context.getResources().openRawResource(R.raw.beach);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm;

            opts.inJustDecodeBounds = true;
            bm = BitmapFactory.decodeStream(is, null, opts);

            // now opts.outWidth and opts.outHeight are the dimension of the
            // bitmap, even though bm is null

            opts.inJustDecodeBounds = false;    // this will request the bm
            opts.inSampleSize = 4;             // scaled down by 4
            // 第一次decodeStream已经读完，所以第二次读取为null.
            is = context.getResources().openRawResource(R.raw.beach);
            bm = BitmapFactory.decodeStream(is, null, opts);

            mBitmap = bm;

            // Decode an image with transparency.
            is = context.getResources().openRawResource(R.raw.beach);
            mBitmap2 = BitmapFactory.decodeStream(is);

            // Create a deep copy of it using getPixels() into different configs.
            int w = mBitmap2.getWidth();
            int h = mBitmap2.getHeight();
            int[] pixels = new int[w * h];
            mBitmap2.getPixels(pixels, 0, w, 0, 0, w, h);

            mBitmap3 = Bitmap.createBitmap(pixels, 0, w, w, h,
                    Bitmap.Config.ARGB_8888);
            mBitmap4 = Bitmap.createBitmap(pixels, 0, w, w,
                    h, Bitmap.Config.ARGB_4444);

            mDrawable = context.getResources().getDrawable(R.drawable.button_normal);
            mDrawable.setBounds(150, 20, 300, 100);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFCCCCCC);

            Paint p = new Paint();
            p.setAntiAlias(true);

            canvas.drawBitmap(mBitmap, 10, 10, null);

            canvas.drawBitmap(mBitmap2, 10, 170, null);
            canvas.drawBitmap(mBitmap3, 110, 170, null);
            canvas.drawBitmap(mBitmap4, 210, 170, null);

            mDrawable.draw(canvas);
        }
    }
}
