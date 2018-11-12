package com.android.home.displaybitmaps.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import com.android.home.displaybitmaps.util.RecyclingBitmapDrawable;

/**
 * Sub-class of ImageView which automatically notifies the drawable when it is
 * being displayed.
 *
 * 不知道notify有什么用，可能更方便回收资源。
 */

public class RecyclingImageView extends androidx.appcompat.widget.AppCompatImageView {
    public RecyclingImageView(Context context) {
        super(context);
    }

    public RecyclingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {

        // This has been detached from Window, so clear the drawable.
        setImageDrawable(null);

        super.onDetachedFromWindow();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {

        // Keep hold of previous Drawable.
        final Drawable previousDrawable = getDrawable();

        // Call super to set new Drawable.
        super.setImageDrawable(drawable);

        // Notify new Drawable that it is being displyed.
        notifyDrawable(drawable, true);

        // Notify old Drawable so it is no longer being displayed.
        notifyDrawable(previousDrawable, false);
    }

    /**
     * Notifies the drawable that it's displayed state has changed.
     *
     * @param drawable
     * @param isDisplayed
     */

    // 这个迭代写得真隐晦。
    private static void notifyDrawable(Drawable drawable, final boolean isDisplayed) {
        if (drawable instanceof  RecyclingBitmapDrawable) {
            // The drawable is a CountingBitmapDrawable, so notify it.
            ((RecyclingBitmapDrawable) drawable).setIsDisplayed(isDisplayed);
        } else if (drawable instanceof LayerDrawable) {

            // The drawable is a LayerDrawable, so recurse on each layer.

            // A Drawable that manages an array of other Drawables. These are drawn in array
            // order, so the element with the largest index will be drawn on top.

            LayerDrawable layerDrawable = (LayerDrawable) drawable;

            for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                notifyDrawable(layerDrawable.getDrawable(i), isDisplayed);
            }
        }
    }
}
