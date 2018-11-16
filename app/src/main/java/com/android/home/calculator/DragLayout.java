package com.android.home.calculator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.android.home.R;


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
            Log.d(Calculator.TAG, "onLayout displayHeight = " + displayHeight);
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

    // 保存和恢复数据。
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState());
        bundle.putBoolean(KEY_IS_OPEN, mIsOpen);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mIsOpen = bundle.getBoolean(KEY_IS_OPEN);
            mHistoryFrame.setVisibility(mIsOpen ? View.VISIBLE : View.INVISIBLE);

            for (DragCallback c : mDragCallbacks) {
                c.onInstanceStateRestored(mIsOpen);
            }

            state = bundle.getParcelable(KEY_SUPER_STATE);
        }

        super.onRestoreInstanceState(state);
    }

    private void saveLastMotion(MotionEvent event) {
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // pointer index.
                final int actionIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(actionIndex);
                // 通过Index值获取位置。
                final PointF point = new PointF(event.getX(actionIndex), event.getY(actionIndex));
                Log.d(Calculator.TAG, "pointerId = " + pointerId);
                mLastMotionPoints.put(pointerId, point);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                for (int i = event.getPointerCount() - 1; i >= 0; --i) {
                    final int pointerId = event.getPointerId(i);
                    final PointF point = mLastMotionPoints.get(pointerId);

                    if (point != null) {
                        point.set(event.getX(i), event.getY(i));
                    }
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int actionIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(actionIndex);
                mLastMotionPoints.remove(pointerId);
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mLastMotionPoints.clear();
                break;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d(Calculator.TAG, "onInterceptTouchEvent");
        saveLastMotion(event);

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Workaround: do not process the error case where multi-touch would cause a crash.
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE &&
                mDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING &&
                mDragHelper.getActivePointerId() != ViewDragHelper.INVALID_POINTER &&
                event.findPointerIndex(mDragHelper.getActivePointerId()) == -1) {

            mDragHelper.cancel();
            Log.d(Calculator.TAG, "onTouchEvent multi-touch");
            return false;
        }

        Log.d(Calculator.TAG, "onTouchEvent");
        saveLastMotion(event);
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        // Settling表示一种状态，比如fling（滑动）屏幕之后的某个位置，而不是拖拽。
        if (mDragHelper.continueSettling(true)) {
            // Cause an invalidate to happen on the next animation time step.
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void onStartDragging() {
        for (DragCallback c : mDragCallbacks) {
            c.onStartDraggingOpen();
        }

        // 开始显示历史界面。
        mHistoryFrame.setVisibility(VISIBLE);
    }

    public boolean isViewUnder(View view, int x, int y) {
        view.getHitRect(mHitRect);
        // 将view在container中的位置转换为坐标轴的位置。
        offsetDescendantRectToMyCoords((View) view.getParent(), mHitRect);

        return mHitRect.contains(x, y);
    }

    public boolean isMoving() {
        final int draggingState = mDragHelper.getViewDragState();

        return draggingState == ViewDragHelper.STATE_DRAGGING ||
                draggingState == ViewDragHelper.STATE_SETTLING;
    }

    public boolean isOpen() {
        return mIsOpen;
    }

    public void setClosed() {
        mIsOpen = false;
        mHistoryFrame.setVisibility(View.INVISIBLE);

        if (mCloseCallback != null) {
            mCloseCallback.onClose();
        }
    }

    public Animator createAnimator(boolean toOpen) {
        if (mIsOpen == toOpen) {
            return ValueAnimator.ofFloat(0f, 1f).setDuration(0L);
        }

        mIsOpen = toOpen;
        mHistoryFrame.setVisibility(VISIBLE);

        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mDragHelper.cancel();
                // 像下拉窗帘一样下拉History界面。
                mDragHelper.smoothSlideViewTo(mHistoryFrame, 0, mIsOpen ? 0 : -mVerticalRange);
            }
        });
        return animator;
    }

    public void setCloseCallback(CloseCallback callback) {
        mCloseCallback = callback;
    }

    public void addDragCallback(DragCallback callback) {
        mDragCallbacks.add(callback);
    }

    public void removeDragCallback(DragCallback callback) {
        mDragCallbacks.remove(callback);
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
