package com.android.home.threelibs.customview;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.home.R;

public class DensityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final LinearLayout root = new LinearLayout(getActivity());
        root.setOrientation(LinearLayout.VERTICAL);

        LinearLayout layout = new LinearLayout(getActivity());
        addBitmapDrawable(layout, R.drawable.logonodpi120, true);
        addBitmapDrawable(layout, R.drawable.logonodpi160, true);
        addBitmapDrawable(layout, R.drawable.logonodpi240, true);
        addLabelToRoot(root, "Prescaled bitmap in drawable");
        addChildToRoot(root, layout);

        return root;
    }

    private void addLabelToRoot(LinearLayout root, String text) {
        TextView label = new TextView(getActivity());
        label.setText(text);
        root.addView(label, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void addChildToRoot(LinearLayout root, LinearLayout layout) {
        root.addView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void addBitmapDrawable(LinearLayout layout, int resource, boolean scale) {
        Bitmap bitmap;
        bitmap = loadAndPrintDpi(resource, scale);

        View view = new View(getActivity());

        final BitmapDrawable d = new BitmapDrawable(getResources(), bitmap);
        if (!scale)
            d.setTargetDensity(getResources().getDisplayMetrics());

        view.setBackgroundDrawable(d);

        view.setLayoutParams(new LinearLayout.LayoutParams(d.getIntrinsicWidth(),
                d.getIntrinsicHeight()));
        layout.addView(view);
    }

    private Bitmap loadAndPrintDpi(int id, boolean scale) {
        Bitmap bitmap;
        if (scale) {
            bitmap = BitmapFactory.decodeResource(getResources(), id);
        } else {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = false;
            bitmap = BitmapFactory.decodeResource(getResources(), id, opts);
        }

        return bitmap;
    }
}
