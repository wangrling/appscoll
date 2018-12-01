package com.android.home.threelibs.autofill.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BuildCompat;
import com.android.home.R;

public class NavigationItem extends FrameLayout {

    private static final String TAG = "NavigationItem";

    public NavigationItem(@NonNull Context context) {
        this(context, null);
    }

    public NavigationItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NavigationItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationItem,
                defStyleAttr, defStyleRes);

        int activityMinSdk = typedArray.getInteger(R.styleable.NavigationItem_minSdk, 26);

        // TODO: remove BuildCompat.isAtLeastP() check when API 28 is finalized.
        int deviceMinSdk = BuildCompat.isAtLeastP() ? 28 : Build.VERSION.SDK_INT;
        if (deviceMinSdk < activityMinSdk) {
            // If device's SDK is lower than the minSdk specified by the NavigationItem, hide it.
            Log.e(TAG, "sdk < 26, just return.");
            setVisibility(View.GONE);
            return ;
        }

    }
}
