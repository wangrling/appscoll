package com.android.home.camerabasic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;


/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */

public class AutoFitTextureView extends TextureView {

    private int mRatioWidth = 0;

    private int mRatioHeight = 0;


    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the
     * ratio calculated from the parameters. Note that the actual sizes of parameters don't
     * matter, that is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     * @param width     Relative horizontal size.
     * @param height    Relative vertical size.
     */

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        mRatioWidth = width;
        mRatioHeight = height;

        //　需要进行重新布局。
        invalidate();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // 记着整形和浮点型的计算！！！
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            // 如果传递进来的值有为零的，则还是使用原来系统设置好的值。
            setMeasuredDimension(width, height);
        } else {
            if (((double)width) / mRatioWidth < ((double)height) / mRatioHeight) {
                // 说明宽度很小，需要将高度裁剪。
                setMeasuredDimension(width, (int)(((double)width * mRatioHeight) / mRatioWidth));
            } else {
                setMeasuredDimension((int)(((double)height * mRatioWidth) / mRatioHeight), height);
            }
        }
    }
}
