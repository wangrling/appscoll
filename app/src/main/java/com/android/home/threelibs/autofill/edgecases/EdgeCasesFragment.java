package com.android.home.threelibs.autofill.edgecases;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.home.R;
import com.android.home.threelibs.autofill.BaseMainFragment;

public class EdgeCasesFragment extends BaseMainFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edge_cases, container, false);
    }

    @Override
    public int getPageTitleResId() {
        return R.string.edge_cases_page_title;
    }
}
