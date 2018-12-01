package com.android.home.threelibs.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class BitmapPixelsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return new BitmapPixelsView(getActivity());
    }

    private static class BitmapPixelsView extends View {

        private Bitmap mBitmap1;
        private Bitmap mBitmap2;

        // access the red component from a premultiplied color
        private static int getR32(int c) { return (c >>  0) & 0xFF; }
        // access the red component from a premultiplied color
        private static int getG32(int c) { return (c >>  8) & 0xFF; }
        // access the red component from a premultiplied color
        private static int getB32(int c) { return (c >> 16) & 0xFF; }
        // access the red component from a premultiplied color
        private static int getA32(int c) { return (c >> 24) & 0xFF; }

        /**
         * This takes components that are already in premultiplied form, and
         * packs them into an int in the correct device order.
         */
        private static int pack8888(int r, int g, int b, int a) {
            return (r << 0) | ( g << 8) | (b << 16) | (a << 24);
        }

        private static short pack565(int r, int g, int b) {
            return (short)((r << 11) | ( g << 5) | (b << 0));
        }

        private static short pack4444(int r, int g, int b, int a) {
            return (short)((a << 0) | ( b << 4) | (g << 8) | (r << 12));
        }

        private static int mul255(int c, int a) {
            int prod = c * a + 128;
            return (prod + (prod >> 8)) >> 8;
        }

        /**
         * Turn a color int into a premultiplied device color
         */
        private static int premultiplyColor(int c) {
            int r = Color.red(c);
            int g = Color.green(c);
            int b = Color.blue(c);
            int a = Color.alpha(c);
            // now apply the alpha to r, g, b
            r = mul255(r, a);
            g = mul255(g, a);
            b = mul255(b, a);
            // now pack it in the correct order
            return pack8888(r, g, b, a);
        }

        private static void makeRamp(int from, int to, int n,
                                     int[] ramp8888, short[] ramp565,
                                     short[] ramp4444) {
            int r = getR32(from) << 23;
            int g = getG32(from) << 23;
            int b = getB32(from) << 23;
            int a = getA32(from) << 23;
            // now compute our step amounts per componenet (biased by 23 bits)
            int dr = ((getR32(to) << 23) - r) / (n - 1);
            int dg = ((getG32(to) << 23) - g) / (n - 1);
            int db = ((getB32(to) << 23) - b) / (n - 1);
            int da = ((getA32(to) << 23) - a) / (n - 1);

            for (int i = 0; i < n; i++) {
                ramp8888[i] = pack8888(r >> 23, g >> 23, b >> 23, a >> 23);
                ramp565[i] = pack565(r >> (23+3), g >> (23+2), b >> (23+3));
                ramp4444[i] = pack4444(r >> (23+4), g >> (23+4), b >> (23+4),
                        a >> (23+4));
                r += dr;
                g += dg;
                b += db;
                a += da;
            }
        }

        private static IntBuffer makeBuffer(int[] src, int n) {
            IntBuffer dst = IntBuffer.allocate(n*n);
            for (int i = 0; i < n; i++) {
                dst.put(src);
            }
            dst.rewind();
            return dst;
        }

        private static ShortBuffer makeBuffer(short[] src, int n) {
            ShortBuffer dst = ShortBuffer.allocate(n*n);
            for (int i = 0; i < n; i++) {
                dst.put(src);
            }
            dst.rewind();
            return dst;
        }



        public BitmapPixelsView(Context context) {
            super(context);

            setFocusable(true);

            final int N = 100;
            int[] data8888 = new int[N];
            short[] data565 = new short[N];
            short[] data4444 = new short[N];

            makeRamp(premultiplyColor(Color.RED), premultiplyColor(Color.GREEN),
                    N, data8888, data565, data4444);

            mBitmap1 = Bitmap.createBitmap(N, N, Bitmap.Config.ARGB_8888);
            mBitmap2 = Bitmap.createBitmap(N, N, Bitmap.Config.RGB_565);

            mBitmap1.copyPixelsFromBuffer(makeBuffer(data8888, N));
            mBitmap2.copyPixelsFromBuffer(makeBuffer(data565, N));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFCCCCCC);

            int y = 10;

            canvas.drawBitmap(mBitmap1, 10, y, null);
            y += mBitmap1.getHeight() + 10;
            canvas.drawBitmap(mBitmap2, 10, y, null);
        }
    }
}
