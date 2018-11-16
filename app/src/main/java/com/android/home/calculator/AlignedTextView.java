package com.android.home.calculator;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.Nullable;

/**
 * Extended {@link TextView} that supports ascent/baseline alignment.
 */

@SuppressLint("AppCompatCustomView")
public class AlignedTextView extends TextView {

    private static final String LATIN_CAPITAL_LETTER = "H";

    // Temporary rect for use during layout.
    private final Rect mTempRect = new Rect();

    private int mTopPaddingOffset;

    private int mBottomPaddingOffset;

    public AlignedTextView(Context context) {
        this(context, null);
    }

    public AlignedTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public AlignedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Disable any included font padding by default.
        setIncludeFontPadding(false);
    }

    // Measure是个怎样的过程？
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Paint paint = getPaint();

        // Always align text to the default capital letter height.
        paint.getTextBounds(LATIN_CAPITAL_LETTER, 0, 1, mTempRect);

        mTopPaddingOffset = Math.min(getPaddingTop(),
                (int) Math.ceil(mTempRect.top - paint.ascent()));

        mBottomPaddingOffset = Math.min(getPaddingBottom(), (int) Math.ceil(paint.descent()));

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public int getCompoundPaddingTop() {
        return super.getCompoundPaddingTop() - mTopPaddingOffset;
    }

    @Override
    public int getCompoundPaddingBottom() {
        return super.getCompoundPaddingBottom() - mBottomPaddingOffset;
    }
}
