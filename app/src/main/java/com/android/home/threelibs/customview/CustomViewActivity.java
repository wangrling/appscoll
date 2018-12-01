package com.android.home.threelibs.customview;

import android.app.Fragment;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CustomViewActivity extends ListActivity {

    private View view;

    Fragment[] fragments;

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = getListView();

        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[] {
                "Xfermodes", "Regions", "TextAlign", "FingerPaint", "UnicodeChart",
                "BitmapDecode", "Arcs", "ColorFilters", "Density", "ShapeDrawable", "Pictures", "ColorMatrix", "Typefaces",
                "Compass",
                "BitmapPixels", "Clipping"
        }));

        fragments = new Fragment[] {
                new XfermodesFragment(),
                new RegionsFragment(),
                new TextAlignFragment(),
                new FingerPaintFragment(),
                new UnicodeChartFragment(),
                new BitmapDecodeFragment(),
                new ArcsFragment(),
                new ColorFiltersFragment(),
                new DensityFragment(),
                new ShapeDrawableFragment(),
                new PictureFragment(),
                new ColorMatrixFragment(),
                new TypefacesFragment(),
                new CompassFragment(),
                new BitmapPixelsFragment(),
                new ClippingFragment()
        };
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        view.setVisibility(View.INVISIBLE);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragments[position])
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onBackPressed() {
        if (view.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.VISIBLE);
        }

        super.onBackPressed();
    }
}
