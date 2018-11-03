package com.android.home.rxjavasamples;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.android.home.R;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

public class ConcurrencyWithSchedulerFragment extends Fragment {

    @BindView(R.id.progress_operation_running)
    ProgressBar progress;

    @BindView(R.id.list_threading_log)
    ListView logsList;

    private Unbinder unbinder;

    private LogAdapter adapter;
    private List<String> logs;


    private CompositeDisposable disposables = new CompositeDisposable();


    @Override
    public void onActivityCreated(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLogger();
    }

    private void setupLogger() {
        logs = new ArrayList<>();
        adapter = new LogAdapter(getActivity(), new ArrayList<>());
        if (logsList != null) {
            logsList.setAdapter(adapter);
        }
    }

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_concurrency_schedulers, container, false);
        unbinder = ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        disposables.clear();
    }

    @OnClick(R.id.btn_start_operation)
    public void startLongOperation() {
        progress.setVisibility(View.VISIBLE);
        log("Button Clicked");

        // 定义一个观察者！
        DisposableObserver<Boolean> d = getDisposableObserver();

        // 获得被观察的对象！
        getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d);

        disposables.add(d);
    }

    private Observable<Boolean> getObservable() {
        return Observable.just(true)
                .map(
                        aBoolean -> {
                            log("Within Observable");
                            doSomeLongOperationThatBlocksCurrentThread();
                            return aBoolean;
                        });
    }

    /**
     * Method that help wiring up the example (irrelevant to RxJava)
     */
    private void doSomeLongOperationThatBlocksCurrentThread() {
        log("Performing long operation");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Observer that handles the result through the 3 important actions:
     *
     * 1. onCompleted
     * 2. onError
     * 3. onNext
     */
    private DisposableObserver<Boolean> getDisposableObserver() {
        return new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean bool) {
                log(String.format("onNext with return value \'%b\"", bool));
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error in RxJava Demo concurrency");
                log(String.format("Boo! Error %s", e.getMessage()));
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onComplete() {
                log("On complete");
                progress.setVisibility(View.INVISIBLE);
            }
        };
    }

    private void log(String logMsg) {
        if (isCurrentOnMainThread()) {
            logs.add(0, logMsg + " (main thread) ");
            adapter.clear();
            adapter.addAll(logs);
        } else {
            logs.add(0, logMsg + " (NOT main thread) ");

            // You can only de below stuff on main thread.
            new Handler(Looper.getMainLooper())
                    .post(() -> {
                                adapter.clear();
                                adapter.addAll(logs);
                            });
        }
    }

    private boolean isCurrentOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    private class LogAdapter extends ArrayAdapter<String> {
        public LogAdapter(Context context, List<String> logs) {
            super(context, R.layout.item_log, R.id.item_log, logs);
        }
    }
}
