package com.android.home.customview;

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

import java.io.InputStream;

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
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFCCCCCC);

            Paint p = new Paint();
            p.setAntiAlias(true);

            canvas.drawBitmap(mBitmap, 10, 10, null);
        }
    }
}
