package com.android.home.threelibs.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class PictureFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return new PictureView(getActivity());
    }

    private static class PictureView extends View {

        private Picture mPicture;
        private Drawable mDrawable;

        public PictureView(Context context) {
            super(context);

            setFocusable(true);
            setFocusableInTouchMode(true);

            mPicture = new Picture();

            drawSomething(mPicture.beginRecording(200, 100));
            mPicture.endRecording();

            mDrawable = new PictureDrawable(mPicture);
        }

        static void drawSomething(Canvas canvas) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

            p.setColor(0x88FF0000);
            canvas.drawCircle(50, 50, 40, p);

            p.setColor(Color.GREEN);
            p.setTextSize(30);
            canvas.drawText("Pictures", 60, 60, p);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            canvas.drawPicture(mPicture);

            canvas.drawPicture(mPicture, new RectF(0, 100, getWidth(), 200));

            mDrawable.setBounds(0, 200, getWidth(), 300);
            mDrawable.draw(canvas);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            mPicture.writeToStream(os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            canvas.translate(0, 300);
            canvas.drawPicture(Picture.createFromStream(is));
        }
    }
}
