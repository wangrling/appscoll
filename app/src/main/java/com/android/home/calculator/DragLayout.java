package com.android.home.calculator;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.drawerlayout.widget.DrawerLayout;
import com.android.home.R;
import com.android.home.calculator.Calculator.*;

public class DragLayout extends ViewGroup {

    // 在onViewReleased作判断使用，具体语义不清楚。
    private static final double AUTO_OPEN_SPEED_LIMIT = 600.0;

    // 保存mIsOpen的键值对。
    private static final String KEY_IS_OPEN = "IS_OPEN";

    // 保存父类状态的键值对。
    private static final String KEY_SUPER_STATE = "SUPER_STATE";

    // 显示历史记录的界面。
    private FrameLayout mHistoryFrame;

    private ViewDragHelper mDragHelper;

    // No concurrency; allow modifications while iterating.
    // 用List表示拖拽的子View所有回调。
    private final List<DragCallback> mDragCallbacks =
            new CopyOnWriteArrayList<>();       // Creates an empty list.

    // 显示HistoryFragment的回调。
    private CloseCallback mCloseCallback;

    private final Map<Integer, PointF> mLastMotionPoints = new HashMap<>();
    private final Rect mHitRect = new Rect();

    private int mVerticalRange;

    private boolean mIsOpen;

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        Log.d(Calculator.TAG, "onFinishInflate");
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        mHistoryFrame = (FrameLayout) findViewById(R.id.history_frame);

        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(Calculator.TAG, "onMeasure width = " + widthMeasureSpec +", " +
                "height = " + heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Ask all of the children of this view to measure themselves, taking into
        // account both the MeasureSpec requirements for this view and its padding.
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }




    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int displayHeight = 0;

        for (DragCallback c : mDragCallbacks) {
            displayHeight = Math.max(displayHeight, c.getDisplayHeight());
        }
        mVerticalRange = getHeight() - displayHeight;
        Log.d(Calculator.TAG, "mVerticalRange = " + mVerticalRange);

        // 获取子界面的数量。
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = getChildAt(i);

            // 在干嘛？
            // 规定子界面的位置。
            // Top position of this view relative to its parent.
            int top = 0;
            // 为什么HistoryFrame特殊对待。
            if (child == mHistoryFrame) {
                if (mDragHelper.getCapturedView() == mHistoryFrame
                        && mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
                    top = child.getTop();
                    Log.d(Calculator.TAG, "onLayout mHistoryFrame top = " + top);
                } else {
                    top = mIsOpen ? 0 : -mVerticalRange;
                }
            }
            // 子界面的位置。
            child.layout(0, top, child.getMeasuredWidth(),
                    top + child.getMeasuredHeight());
        }
    }

    /**
     * Callback when the layout is closed.
     * We use this to pop the HistoryFragment off the backstack.
     * We can't use a method in DragCallback because we get ConcurrentModificationExceptions on
     * mDragCallbacks when executePendingTransactions() is called for popping the fragment off the
     * backstack.
     */
    public interface CloseCallback {
        void onClose();
    }

    /**
     * Callback for coordinating with the RecyclerView or HistoryFragment.
     */
    public interface DragCallback {
        // Callback when a drag to open begins.
        void onStartDraggingOpen();

        // Callback in onRestoreInstanceState.
        // 恢复状态的时候判断是打开还是关闭的状态。
        void onInstanceStateRestored(boolean isOpen);

        // Animate the RecyclerView text.
        void whileDragging(float yFraction);

        // Whether we should allow the view to be dragged.
        // 根据位置判断是否允许进行拖拽。
        boolean shouldCaptureView(View view, int x, int y);

        int getDisplayHeight();
    }

    public class DragHelperCallback extends ViewDragHelper.Callback {

        // 拖拽的状态发生变化。
        @Override
        public void onViewDragStateChanged(int state) {
            Log.d(Calculator.TAG, "onViewDragStateChanged state = " + state);
            // The view stopped moving.
            if (state == ViewDragHelper.STATE_IDLE &&
                    mDragHelper.getCapturedView().getTop() < -(mVerticalRange / 2)) {

                setClosed();
            }
        }

        // 拖拽的位置发生变化。

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            Log.d(Calculator.TAG, "onViewPositionChanged top = " + top);
            for (DragCallback c : mDragCallbacks) {
                // Top is between [-mVerticalRange, 0].
                c.whileDragging(1f + (float) top / mVerticalRange);
            }
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return mVerticalRange;
        }


        // pointerId是什么值？
        @Override
        public boolean tryCaptureView(@NonNull View view, int pointerId) {
            Log.d(Calculator.TAG, "tryCaptureView pointerId = " + pointerId);
            final PointF point = mLastMotionPoints.get(pointerId);
            if (point == null) {
                return false;
            }

            final int x = (int) point.x;
            final int y = (int) point.y;

            for (DragCallback c : mDragCallbacks) {
                if (!c.shouldCaptureView(view, x, y)) {
                    return false;
                }
            }

            return true;
        }

        // Restrict the motion of the dragged child view along the vertical axis.
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            Log.d(Calculator.TAG, "clampViewPositionVertical top = " + top);
            return Math.max(Math.min(top, 0), -mVerticalRange);
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);

            if (!mIsOpen) {
                mIsOpen = true;
                onStartDragging();
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            final boolean settleToOpen;

            if (yvel > AUTO_OPEN_SPEED_LIMIT) {
                // Speed has priority over position.
                settleToOpen = true;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else {
                settleToOpen = releasedChild.getTop() > -(mVerticalRange / 2);
            }

            // If the view is not visible, then settle is closed, not open.
            if (mDragHelper.settleCapturedViewAt(0, settleToOpen && mIsOpen ?
                    0 : -mVerticalRange)) {
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }
        }
    }
}
