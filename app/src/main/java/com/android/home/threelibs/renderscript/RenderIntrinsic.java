package com.android.home.threelibs.renderscript;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.renderscript.*;
import com.android.home.R;

public class RenderIntrinsic extends AppCompatActivity {

    /**
     * Number of bitmaps that is used for renderScript thread and UI thread synchronization.
     */
    private final int NUM_BITMAPS = 2;
    private int mCurrentBitmap = 0;

    private Bitmap mBitmapIn;
    private Bitmap[] mBitmapsOut;
    private ImageView mImageView;

    private RenderScript mRS;
    private Allocation mInAllocation;
    private Allocation[] mOutAllocations;

    private ScriptIntrinsicBlur mScriptBlur;
    private ScriptIntrinsicConvolve5x5 mScriptConvolve;
    private ScriptIntrinsicColorMatrix mScriptMatrix;

    private final int MODE_BLUR = 0;
    private final int MODE_CONVOLVE = 1;
    private final int MODE_COLORMATRIX = 2;

    private int mFilterMode = MODE_BLUR;

    private RenderScriptTask mLatestTask = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.render_intrinsic);

        // Set up main image view
        mBitmapIn = loadBitmap(R.drawable.data);
        mBitmapsOut = new Bitmap[NUM_BITMAPS];
        for (int i = 0; i < NUM_BITMAPS; ++i) {
            mBitmapsOut[i] = Bitmap.createBitmap(mBitmapIn.getWidth(),
                    mBitmapIn.getHeight(), mBitmapIn.getConfig());
        }

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageBitmap(mBitmapsOut[mCurrentBitmap]);
        mCurrentBitmap += (mCurrentBitmap + 1) % NUM_BITMAPS;

        //Set up seekbar
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar1);
        seekbar.setProgress(50);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                updateImage(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Setup effect selector
        RadioButton radio0 = (RadioButton) findViewById(R.id.radio0);
        radio0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterMode = MODE_BLUR;
                    updateImage(seekbar.getProgress());
                }
            }
        });
        RadioButton radio1 = (RadioButton) findViewById(R.id.radio1);
        radio1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterMode = MODE_CONVOLVE;
                    updateImage(seekbar.getProgress());
                }
            }
        });
        RadioButton radio2 = (RadioButton) findViewById(R.id.radio2);
        radio2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFilterMode = MODE_COLORMATRIX;
                    updateImage(seekbar.getProgress());
                }
            }
        });

        // Create renderScript
        createScript();

        // Create thumbnails
        createThumbnail();

        // Invoke renderScript kernel and update imageView
        mFilterMode = MODE_BLUR;
        updateImage(50);
    }

    /**
     * Invoke AsyncTask and cancel previous task.
     *
     * <p>When AsyncTasks are piled up (typically in slow device with heavy kernel),
     * Only the latest (and already started) task invokes RenderScript operation.</p>
     */
    private void updateImage(int progress) {
        float f = getFilterParameter(progress);

        if (mLatestTask != null)
            mLatestTask.cancel(false);
        mLatestTask = new RenderScriptTask();

        mLatestTask.execute(f);
    }


    public void createScript() {
        mRS = RenderScript.create(this);

        mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn);
        mOutAllocations = new Allocation[NUM_BITMAPS];

        for (int i = 0; i < NUM_BITMAPS; ++i) {
            mOutAllocations[i] = Allocation.createFromBitmap(mRS, mBitmapsOut[i]);
        }

        // Create intrinsics.
        // RenderScript has built-in features such as blur, convolve filter etc.
        // These intrinsics are handy for specific operations without writing RenderScript kernel.
        // In the sample, it's creating blur, convolve and matrix intrinsics.
        mScriptBlur = ScriptIntrinsicBlur.create(mRS, Element.U8_4(mRS));
        mScriptConvolve = ScriptIntrinsicConvolve5x5.create(mRS,
                Element.U8_4(mRS));
        mScriptMatrix = ScriptIntrinsicColorMatrix.create(mRS,
                Element.U8_4(mRS));
    }

    private void performFilter(Allocation inAllocation, Allocation outAllocation,
            Bitmap bitmapOut, float value) {
        switch (mFilterMode) {
            case MODE_BLUR:
                // Set blur kernel size
                mScriptBlur.setRadius(value);

                // Invoke filter kernel
                mScriptBlur.setInput(inAllocation);
                mScriptBlur.forEach(outAllocation);
                break;
            case MODE_CONVOLVE: {
                @SuppressWarnings("UnnecessaryLocalVariable")
                float f1 = value;
                float f2 = 1.0f - f1;

                // Emboss filter kernel
                float coefficients[] = {-f1 * 2, 0, -f1, 0, 0, 0, -f2 * 2, -f2, 0,
                        0, -f1, -f2, 1, f2, f1, 0, 0, f2, f2 * 2, 0, 0, 0, f1, 0,
                        f1 * 2,};
                // Set kernel parameter
                mScriptConvolve.setCoefficients(coefficients);

                // Invoke filter kernel
                mScriptConvolve.setInput(inAllocation);
                mScriptConvolve.forEach(outAllocation);
                break;
            }
            case MODE_COLORMATRIX: {
                // Set HUE rotation matrix
                // The matrix below performs a combined operation of,
                // RGB->HSV transform * HUE rotation * HSV->RGB transform
                float cos = (float) Math.cos((double) value);
                float sin = (float) Math.sin((double) value);
                Matrix3f mat = new Matrix3f();
                mat.set(0, 0, (float) (.299 + .701 * cos + .168 * sin));
                mat.set(1, 0, (float) (.587 - .587 * cos + .330 * sin));
                mat.set(2, 0, (float) (.114 - .114 * cos - .497 * sin));
                mat.set(0, 1, (float) (.299 - .299 * cos - .328 * sin));
                mat.set(1, 1, (float) (.587 + .413 * cos + .035 * sin));
                mat.set(2, 1, (float) (.114 - .114 * cos + .292 * sin));
                mat.set(0, 2, (float) (.299 - .3 * cos + 1.25 * sin));
                mat.set(1, 2, (float) (.587 - .588 * cos - 1.05 * sin));
                mat.set(2, 2, (float) (.114 + .886 * cos - .203 * sin));
                mScriptMatrix.setColorMatrix(mat);

                // Invoke filter kernel
                mScriptMatrix.forEach(inAllocation, outAllocation);
            }
            break;
        }

        // Copy to bitmap and invalidate image view
        outAllocation.copyTo(bitmapOut);
    }

    /**
     * In the AsyncTask, it invokes RenderScript intrinsics to do a filtering.
     *
     * <p>After the filtering is done, an operation blocks at Allocation.copyTo() in AsyncTask
     * thread. Once all operation is finished at onPostExecute() in UI thread, it can invalidate
     * and
     * update ImageView UI.</p>
     */
    private class RenderScriptTask extends AsyncTask<Float, Integer, Integer> {

        private boolean mIssued;

        @Override
        protected Integer doInBackground(Float... floats) {
            int index = -1;
            if (!isCancelled()) {
                mIssued = true;
                index = mCurrentBitmap;

                performFilter(mInAllocation, mOutAllocations[index], mBitmapsOut[index], floats[0]);
                mCurrentBitmap = (mCurrentBitmap + 1) % NUM_BITMAPS;
            }

            return index;
        }

        void updateView(Integer result) {
            if (result != -1) {
                // Request UI update
                mImageView.setImageBitmap(mBitmapsOut[result]);
                mImageView.invalidate();
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            updateView(integer);
        }

        @Override
        protected void onCancelled(Integer integer) {
            if (mIssued) {
                updateView(integer);
            }
        }
    }

    /**
     * Convert seekBar progress parameter (0-100 in range) to parameter for each intrinsic filter.
     * (e.g. 1.0-25.0 in Blur filter)
     */
    private float getFilterParameter(int i) {
        float f = 0.f;
        switch (mFilterMode) {
            case MODE_BLUR: {
                final float max = 25.0f;
                final float min = 1.f;
                f = (float) ((max - min) * (i / 100.0) + min);
            }
            break;
            case MODE_CONVOLVE: {
                final float max = 2.f;
                final float min = 0.f;
                f = (float) ((max - min) * (i / 100.0) + min);
            }
            break;
            case MODE_COLORMATRIX: {
                final float max = (float) Math.PI;
                final float min = (float) -Math.PI;
                f = (float) ((max - min) * (i / 100.0) + min);
            }
            break;
        }
        return f;
    }

    /**
     * Helper to load Bitmap from resource.
     */
    private Bitmap loadBitmap(int resource) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeResource(getResources(), resource, options);
    }

    /**
     * Create thumbnail for UI. It invokes RenderScript kernel synchronously in UI-thread,
     * which is OK for small thumbnail (but not ideal).
     */
    private void createThumbnail() {
        int width = 72;
        int height = 96;
        float scale = getResources().getDisplayMetrics().density;
        int pixelsWidth = (int) (width * scale + 0.5f);
        int pixelsHeight = (int) (height * scale + 0.5f);

        // Temporary image
        Bitmap tempBitmap = Bitmap.createScaledBitmap(mBitmapIn, pixelsWidth, pixelsHeight, false);
        Allocation inAllocation = Allocation.createFromBitmap(mRS, tempBitmap);

        // Create thumbnail with each RS intrinsic and set it to radio buttons
        int[] modes = {MODE_BLUR, MODE_CONVOLVE, MODE_COLORMATRIX};
        int[] ids = {R.id.radio0, R.id.radio1, R.id.radio2};
        int[] parameter = {50, 100, 25};
        for (int mode : modes) {
            mFilterMode = mode;
            float f = getFilterParameter(parameter[mode]);

            Bitmap destBitmap = Bitmap.createBitmap(tempBitmap.getWidth(),
                    tempBitmap.getHeight(), tempBitmap.getConfig());
            Allocation outAllocation = Allocation.createFromBitmap(mRS, destBitmap);

            performFilter(inAllocation, outAllocation, destBitmap, f);

            // 最终设置。
            ThumbnailRadioButton button = (ThumbnailRadioButton) findViewById(ids[mode]);
            button.setThumbnail(destBitmap);
        }
    }
}
