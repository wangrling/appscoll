package com.android.home.stepsensor;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A Layout that contains a stream of card views.
 */

public class CardStreamLinearLayout extends LinearLayout {
    public static final int ANIMATION_SPEED_SLOW = 1001;
    public static final int ANIMATION_SPEED_NORMAL = 1002;
    public static final int ANIMATION_SPEED_FAST = 1003;

    private static final String TAG = "CardStreamLinearLayout";

    private final ArrayList<View> mFixedViewList = new ArrayList<>();

    private final Rect mChildRect = new Rect();

    private CardStreamAnimator mAnimators;


    public CardStreamLinearLayout(Context context) {
        super(context);
    }

    public CardStreamLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardStreamLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
