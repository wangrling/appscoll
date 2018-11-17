package com.android.home.calculator;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * TextView adapted for displaying the formula and allowing pasting.
 */

public class CalculatorFormula extends AlignedTextView implements
        MenuItem.OnMenuItemClickListener,
        ClipboardManager.OnPrimaryClipChangedListener {

    public static final String TAG_ACTION_MODE = "ACTION_MODE";

    // Temporary paint for use in layout methods.
    private final TextPaint mTempPaint = new TextPaint();

    private final float mMaximumTextSize;
    private final float mMinimumTextSize;
    private final float mStepTextSize;

    private final ClipboardManager mClipboardManager;

    private int mWidthConstraint = -1;
    private ActionMode mActionMode;
    private ActionMode.Callback mPasteActionModeCallback;
    // 6.0以前使用。
    private ContextMenu mContextMenu;

    private OnTextSizeChangeListener mOnTextSizeChangeListener;
    private OnFormulaContextMenuClickListener mOnContextMenuClickListener;
    private Calculator.OnDisplayMemoryOperationsListener mOnDisplayMemoryOperationsListener;


    public CalculatorFormula(Context context) {
        this(context, null);
    }

    public CalculatorFormula(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalculatorFormula(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CalculatorFormula, defStyleAttr, 0);

        mMaximumTextSize = a.getDimension(R.styleable.CalculatorFormula_maxTextSize, getTextSize());
        mMinimumTextSize = a.getDimension(
                R.styleable.CalculatorFormula_minTextSize, getTextSize());
        mStepTextSize = a.getDimension(R.styleable.CalculatorFormula_stepTextSize,
                (mMaximumTextSize - mMinimumTextSize) / 3);
        a.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupActionMode();
        } else {
            setupContextMenu();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isLaidOut()) {
            // Value is raw pixels.
            setTextSizeInternal(TypedValue.COMPLEX_UNIT_PX, mMaximumTextSize,
                    false   /* notifyListener */);

            setMinimumHeight(getLineHeight() + getCompoundPaddingBottom()
                    + getCompoundPaddingTop());
        }

        // Ensure we are at least as big as our parent.
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        if (getMinimumWidth() != width) {
            setMinimumWidth(width);
        }

        // Re-calculate our textSize based on new width.
        // 超过固定的宽度之后字体应该自动缩小。
        mWidthConstraint = MeasureSpec.getSize(widthMeasureSpec) -
                getPaddingLeft() - getPaddingRight();

        final float textSize = getVariableTextSize(getText());
        if (getTextSize() != textSize) {
            setTextSizeInternal(TypedValue.COMPLEX_UNIT_PX, textSize, false);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float getVariableTextSize(CharSequence text) {
        return 0;
    }

    private void setTextSizeInternal(int complexUnitPx, float mMaximumTextSize, boolean b) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mClipboardManager.addPrimaryClipChangedListener(this);

        onPrimaryClipChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mClipboardManager.removePrimaryClipChangedListener(this);
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    /**
     * Use ActionMode for paste support on M and higher.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void setupActionMode() {

    }

    /**
     * Use ContextMenu for paste support on L and lower.
     */
    private void setupContextMenu() {
        setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                            ContextMenu.ContextMenuInfo contextMenuInfo) {
                final MenuInflater inflater = new MenuInflater(getContext());
                createContextMenu(inflater, contextMenu);
                mContextMenu = contextMenu;
                for (int i = 0; i < contextMenu.size(); i++) {
                    contextMenu.getItem(i).setOnMenuItemClickListener(CalculatorFormula.this);
                }
            }
        });
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return showContextMenu();
            }
        });
    }

    private boolean createContextMenu(MenuInflater inflater, Menu menu) {
        final boolean isPasteEnabled = isPasteEnabled();
        final boolean isMemoryEnabled = isMemoryEnabled();
        if (!isPasteEnabled && !isMemoryEnabled) {
            return false;
        }

        bringPointIntoView(length());
        inflater.inflate(R.menu.menu_formula, menu);
        final MenuItem pasteItem = menu.findItem(R.id.menu_paste);
        final MenuItem memoryRecallItem = menu.findItem(R.id.memory_recall);
        pasteItem.setEnabled(isPasteEnabled);
        memoryRecallItem.setEnabled(isMemoryEnabled);
        return true;
    }

    private boolean isMemoryEnabled() {
        return mOnDisplayMemoryOperationsListener != null
                && mOnDisplayMemoryOperationsListener.shouldDisplayMemory();
    }

    private boolean isPasteEnabled() {
        final ClipData clip = mClipboardManager.getPrimaryClip();
        if (clip == null || clip.getItemCount() == 0) {
            return false;
        }
        CharSequence clipText = null;
        try {
            clipText = clip.getItemAt(0).coerceToText(getContext());
        } catch (Exception e) {
            Log.i("Calculator", "Error reading clipboard:", e);
        }
        return !TextUtils.isEmpty(clipText);
    }

    @Override
    public void onPrimaryClipChanged() {

    }

    public interface OnTextSizeChangeListener {
        void onTextSizeChanged(TextView textView, float oldSize);
    }

    public interface OnFormulaContextMenuClickListener {
        boolean onPaste(ClipData clip);

        void onMemoryRecall();
    }
}
