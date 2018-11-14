package com.android.home.customview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

public class TypefacesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return new TypefacesView(getActivity());
    }
    private static class TypefacesView extends View {
        private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Typeface mFace;

        TypefacesView(Context context) {
            super(context);

            mFace = Typeface.createFromAsset(getContext().getAssets(), "samplefont.ttf");
            mPaint.setTextSize(64);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
            mPaint.setTypeface(null);
            canvas.drawText("Draw with Default:", 10, 100, mPaint);
            canvas.drawText("  SAMPLE TEXT", 10, 200, mPaint);
            canvas.drawText("Draw with Custom Font", 10, 400, mPaint);
            canvas.drawText("('A' with solid triangle.)", 10, 500, mPaint);
            mPaint.setTypeface(mFace);
            canvas.drawText("  SAMPLE TEXT", 10, 600, mPaint);
        }
    }
}
