package com.android.home.picasso;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.home.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PicassoActivity extends FragmentActivity {

    private ToggleButton showHide;
    private FrameLayout sampleContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.picasso_activity);

        sampleContent = findViewById(R.id.sample_content);

        final ListView activityList = findViewById(R.id.activity_list);
        final PicassoSampleAdapter adapter = new PicassoSampleAdapter(this);
        activityList.setAdapter(adapter);
        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                adapter.getItem(position).launch(PicassoActivity.this);
            }
        });

        showHide = findViewById(R.id.faux_action_bar_control);
        showHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                activityList.setVisibility(checked ? VISIBLE : GONE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        if (showHide.isChecked()) {
            showHide.setChecked(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override public void setContentView(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, sampleContent);
    }

    @Override public void setContentView(View view) {
        sampleContent.addView(view);
    }

    @Override public void setContentView(View view, ViewGroup.LayoutParams params) {
        sampleContent.addView(view, params);
    }
}
