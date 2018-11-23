package com.android.home.renderscript;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

import static com.android.home.displaybitmaps.util.AsyncTask.init;

/**
 * A button with Thumbnail which extends Radio Button.
 *
 * <p>The widget override a background drawable of Radio Button with a StateList Drawable. Each
 * state has a LayerDrawable with a Thumbnail image and a Focus rectangle. It's using original
 * Radio Buttons text as a label, because LayerDrawable showed some issues with
 * Canvas.drawText().</p>
 */

@SuppressLint("AppCompatCustomView")
public class ThumbnailRadioButton extends RadioButton {
    public ThumbnailRadioButton(Context context) {
        super(context);
        init();
    }

    public ThumbnailRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThumbnailRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setButtonDrawable(android.R.color.transparent);
    }

    public void setThumbnail(Bitmap bitmap) {
        // Bitmap drawable
        BitmapDrawable bmp = new BitmapDrawable(getResources(), bitmap);
        bmp.setGravity(Gravity.CENTER);

        int strokeWidth = 24;

        // Checked state
        ShapeDrawable rectChecked = new ShapeDrawable(new RectShape());
        rectChecked.getPaint().setColor(0xFFFFFFFF);
        rectChecked.getPaint().setStyle(Paint.Style.STROKE);
        rectChecked.getPaint().setStrokeWidth(strokeWidth);
        rectChecked.setIntrinsicWidth(bitmap.getWidth() + strokeWidth);
        rectChecked.setIntrinsicHeight(bitmap.getHeight() + strokeWidth);
        Drawable drawableArray[] = new Drawable[]{bmp, rectChecked};
        LayerDrawable layerChecked = new LayerDrawable(drawableArray);

        // Unchecked state
        ShapeDrawable rectUnchecked = new ShapeDrawable(new RectShape());
        rectUnchecked.getPaint().setColor(0x0);
        rectUnchecked.getPaint().setStyle(Paint.Style.STROKE);
        rectUnchecked.getPaint().setStrokeWidth(strokeWidth);
        rectUnchecked.setIntrinsicWidth(bitmap.getWidth() + strokeWidth);
        rectUnchecked.setIntrinsicHeight(bitmap.getHeight() + strokeWidth);
        Drawable drawableArray2[] = new Drawable[]{bmp, rectUnchecked};
        LayerDrawable layerUnchecked = new LayerDrawable(drawableArray2);

        // StateList drawable
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_checked}, layerChecked);
        states.addState(new int[]{}, layerUnchecked);

        setBackground(states);

        //Offset text to center/bottom of the checkbox
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getTextSize());
        paint.setTypeface(getTypeface());
        float w = paint.measureText(getText(), 0, getText().length());
        setPadding(getPaddingLeft() + (int) ((bitmap.getWidth() - w) / 2.f + .5f),
                getPaddingTop() + (int) (bitmap.getHeight() * 0.70),
                getPaddingRight(),
                getPaddingBottom());

        setShadowLayer(5, 0, 0, Color.BLACK);
    }
}
