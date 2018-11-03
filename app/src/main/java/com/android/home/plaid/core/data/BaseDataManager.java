package com.android.home.plaid.core.data;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseDataManager<T> implements DataLoadingSubject {

    private final AtomicInteger loadingCount;

    private OnDataLoadedCallback<T> onDataLoadedCallback;

    public interface OnDataLoadedCallback<T> {
        void onDataLoaded(T data);
    }

    public BaseDataManager() {
        loadingCount = new AtomicInteger(0);
    }

    @Override
    public boolean isDataLoading() {
        return loadingCount.get() > 0;
    }


}
