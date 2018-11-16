package com.android.home.calculator;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.home.R;
import com.bumptech.glide.load.data.AssetFileDescriptorLocalUriFetcher;

/**
 * 包含两个子View，一个是sin, cos, 一个是数字，加减等。
 */

public class CalculatorPadViewPager extends ViewPager {

    private final PagerAdapter mStaticPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            // 已经在xml文件中定义。
            return getChildCount();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Log.d(Calculator.TAG, "instantiateItem position = " + position);
            // 获取它的子界面。
            final View child = getChildAt(position);

            // Set a OnClickListener to scroll to item's position when it isn't the current item.
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 平滑过渡。
                    setCurrentItem(position, true /* smoothScroll */);
                }
            });

            // Set an OnTouchListener to always return true for onTouch events so that a touch
            // sequence cannot pass through the item to the item below.
            child.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.onTouchEvent(event);
                    // 不再让touch事件继续传递。
                    return true;
                }
            });

            // Set an OnHoverListener to always return true for onHover events so that focus cannot
            // pass through the item to the item below.
            // Hover相当于鼠标放在界面上的时候。
            child.setOnHoverListener(new OnHoverListener() {
                @Override
                public boolean onHover(View v, MotionEvent event) {
                    v.onHoverEvent(event);
                    return true;
                }
            });

            // Make the item focusable so it can be selected via a11y.
            child.setFocusable(true);

            // Set the content description of the item which will by used by a11y to identify it.
            child.setContentDescription(getPageTitle(position));

            return child;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            removeViewAt(position);
        }


        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public float getPageWidth(int position) {
            // 不得其解。
            return position == 1 ? 7.0f / 9.0f : 1.0f;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            final String[] pageDescriptions = getContext().getResources()
                    .getStringArray(R.array.desc_pad_pages);

            return pageDescriptions[position];
        }
    };

    private final OnPageChangeListener mOnPageChangeListener = new SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {

            Log.d(Calculator.TAG, "onPageSelected");
            for (int i = getChildCount() - 1; i >= 0; --i) {
                final View child = getChildAt(i);

                // Only the "peeking" or covered page should be clickable.
                child.setClickable(i != position);

                // Prevent clicks and accessibility focus from going through to descendants of
                // other pages which are covered by the current page.
                if (child instanceof ViewGroup) {
                    final ViewGroup childViewGroup = (ViewGroup) child;
                    for (int j = childViewGroup.getChildCount() - 1; j >= 0; --j) {
                        childViewGroup.getChildAt(j)
                                .setImportantForAccessibility( i == position ?
                                        IMPORTANT_FOR_ACCESSIBILITY_AUTO :
                                        IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
                    }
                }
            }
        }
    };

    private final PageTransformer mPageTransformer = new PageTransformer() {
        @Override
        public void transformPage(@NonNull View view, float position) {

            Log.d(Calculator.TAG, "transformPage position = " + position);
            if (position < 0.0f) {
                // Pin the left page to the left side.
                // 左边是从0到-1.
                view.setTranslationX(getWidth() * -position);
                view.setAlpha(Math.max(1.0f + position, 0.0f));
            } else {
                // Use the default slide transition when moving to the next page.
                // 设置相对于左边缘的水平位置。
                view.setTranslationX(0.0f);
            }
        }
    };

    private final GestureDetector.SimpleOnGestureListener mGestureWatcher =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    // Return true so calls to onSingleTapUp are not blocked.
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if (mClickedItemIndex != -1) {
                        getChildAt(mClickedItemIndex).performClick();
                        mClickedItemIndex = -1;
                        return true;
                    }
                    return super.onSingleTapUp(e);
                }
            };

    private final GestureDetector mGestureDetector;

    private int mClickedItemIndex = -1;

    public CalculatorPadViewPager(@NonNull Context context) {
        this(context, null /* attrs */);
    }

    public CalculatorPadViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mGestureDetector = new GestureDetector(context, mGestureWatcher);

        mGestureDetector.setIsLongpressEnabled(false);

        setAdapter(mStaticPagerAdapter);

        setBackgroundColor(Color.BLACK);

        // 24dip.
        setPageMargin(-getResources().getDimensionPixelSize(R.dimen.pad_page_margin));
        setPageTransformer(false, mPageTransformer);

        addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Invalidate the adapter's data set since children may have been added during inflation.
        // 会通知相关的监听数据发生变化。
        getAdapter().notifyDataSetChanged();

        // Let page change listener know about our initial position.
    }

    /**
     * 事件先传递到onInterceptTouchEvent，然后传递到子界面的onTouchEvent,最后传递到这个ViewGroup的onTouchEvent函数。
     */

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(Calculator.TAG, "onInterceptTouchEvent");

        try {
            // Always intercept touch events when a11y focused since otherwise they will be
            // incorrectly offset by a11y before being dispatched to children.
            if (isAccessibilityFocused() || super.onInterceptTouchEvent(ev)) {
                return true;
            }

            // Only allow the current item to receive touch events.
            final int action = ev.getActionMasked();

            Log.d(Calculator.TAG, "onInterceptTouchEvent action = " + action);

            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                // If a child is a11y focused then we must always intercept the touch event
                // since it will be incorrectly offset by a11y.
                // 什么是a11y？
                final int childCount = getChildCount();
                for (int childIndex = childCount - 1; childIndex >= 0; --childIndex) {
                    if (getChildAt(childIndex).isAccessibilityFocused()) {
                        mClickedItemIndex = childIndex;
                        return true;
                    }
                }

                if (action == MotionEvent.ACTION_DOWN) {
                    mClickedItemIndex = -1;
                }

                // Otherwise if touch is on a non-current item then intercept.
                final int actionIndex = ev.getActionIndex();
                final float x = ev.getX(actionIndex) + getScrollX();
                final float y = ev.getY(actionIndex) + getScaleY();

                for (int i = childCount - 1; i >= 0; --i) {
                    final int childIndex = getChildDrawingOrder(childCount, i);
                    final View child = getChildAt(childIndex);
                    if (child.getVisibility() == VISIBLE &&
                            x >= child.getLeft() && x < child.getRight() &&
                            y >= child.getTop() && y < child.getBottom()) {
                        if (action == MotionEvent.ACTION_DOWN) {
                            mClickedItemIndex = childIndex;
                        }

                        Log.d(Calculator.TAG, "mClickedItemIndex = " + mClickedItemIndex);
                        // 如果返回true就直接交给下面的onTouchEvent处理。
                        return childIndex != getCurrentItem();
                    }
                }
            }

            return false;
        } catch (IllegalArgumentException e) {
            Log.e(Calculator.TAG, "Error intercepting touch event", e);
            return false;
        }
    }


    /**
     * 属于View类。
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(Calculator.TAG, "onTouchEvent");

        try {
            // Allow both the gesture detector and super to handle the touch event so they both see
            // the full sequence of events. This should be safe since the gesture detector only
            // handle clicks and super only handles swipes.
            mGestureDetector.onTouchEvent(ev);
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.e(Calculator.TAG, "Error processing touch event", e);

            return false;
        }
    }
}
