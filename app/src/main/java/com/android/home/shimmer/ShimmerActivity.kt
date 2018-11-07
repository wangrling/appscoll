package com.android.home.shimmer

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.android.home.R
import kotlinx.android.synthetic.main.shimmer_activity.*

class ShimmerActivity : Activity(), View.OnClickListener {

    private lateinit var shimmerViewContainer: ShimmerFrameLayout
    private lateinit var presetButtons: Array<Button>
    private var currentPreset = -1;
    private var toast: Toast? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.shimmer_activity)
        shimmerViewContainer = shimmer_view_container

        presetButtons = arrayOf(
            preset_button0,
            preset_button1,
            preset_button2,
            preset_button3,
            preset_button4,
            preset_button5)

        presetButtons.forEach {
            it.setOnClickListener(this@ShimmerActivity)
        }

        selectPreset(0, false);
    }


    override fun onClick(v: View) {
        selectPreset(presetButtons.indexOf(v as Button), true)
    }

    override fun onResume() {
        super.onResume()

        shimmerViewContainer.startShimmer()
    }


    public override fun onPause() {
        shimmerViewContainer.stopShimmer()
        super.onPause()
    }

    private fun selectPreset(preset: Int, showToast: Boolean) {

        if (currentPreset == preset) {
            return
        }

        if (currentPreset >= 0) {
            presetButtons[currentPreset].setBackgroundResource(R.color.preset_button_background)
        }
        currentPreset = preset
        presetButtons[currentPreset].setBackgroundResource(R.color.preset_button_background_selected)

        // If a toast is already showing, hide it
        toast?.cancel()

        val shimmerBuilder = Shimmer.AlphaHighlightBuilder()

        shimmerViewContainer.setShimmer(
            when (preset) {
                1 -> {
                    // Slow and reverse
                    toast = Toast.makeText(this, "Slow and reverse", Toast.LENGTH_SHORT)
                    shimmerBuilder.setDuration(5000L).setRepeatMode(ValueAnimator.REVERSE)
                }
                2 -> {
                    // Thin, straight and transparent
                    toast = Toast.makeText(this, "Thin, straight and transparent", Toast.LENGTH_SHORT)
                    shimmerBuilder.setBaseAlpha(0.1f).setDropoff(0.1f).setTilt(0f)
                }
                3 -> {
                    // Sweep angle 90
                    toast = Toast.makeText(this, "Sweep angle 90", Toast.LENGTH_SHORT)
                    shimmerBuilder.setDirection(Shimmer.Direction.TOP_TO_BOTTOM).setTilt(0f)

                }
                4 -> {
                    // Spotlight
                    toast = Toast.makeText(this, "Spotlight", Toast.LENGTH_SHORT)
                    shimmerBuilder.setBaseAlpha(0f)
                        .setDuration(2000L)
                        .setDropoff(0.1f)
                        .setIntensity(0.35f)
                        .setShape(Shimmer.Shape.RADIAL)
                }
                5 -> {
                    // Off
                    toast = Toast.makeText(this, "Off", Toast.LENGTH_SHORT)
                    null
                }
                else -> {
                    toast = Toast.makeText(this, "Default", Toast.LENGTH_SHORT)
                    shimmerBuilder
                }
            }?.build()
        )

        // Show toast describing the chosen preset, if necessary
        if (showToast) {
            toast?.show()
        }
    }
}