package com.android.home.autofill.commoncases;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.home.R;
import com.android.home.autofill.BaseMainFragment;

public class CommonCasesFragment extends BaseMainFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_common_case, container, false);
    }

    @Override
    public int getPageTitleResId() {
        return R.string.common_cases_page_title;
    }
}
