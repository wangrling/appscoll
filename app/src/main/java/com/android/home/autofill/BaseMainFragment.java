package com.android.home.autofill;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public abstract class BaseMainFragment extends Fragment {

    public abstract @StringRes int getPageTitleResId();
}
