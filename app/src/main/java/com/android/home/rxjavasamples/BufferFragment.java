package com.android.home.rxjavasamples;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.android.home.R;
import com.jakewharton.rxbinding2.view.RxView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BufferFragment extends Fragment {
    @BindView(R.id.btn_start_operation)
    Button tapBtn;


    @BindView(R.id.list_threading_log)
    ListView logsList;
    private LogAdapter adapter;
    private List<String> logs;

    private Disposable disposable;

    private Unbinder unbinder;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        setupLogger();
    }

    @Override
    public void onPause() {
        super.onPause();
        disposable.dispose();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_buffer, container, false);

        unbinder = ButterKnife.bind(this, layout);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        disposable = getBufferedDisposable();
    }

    // Main Rx entities.
    private Disposable getBufferedDisposable() {
        return RxView.clicks(tapBtn)
                .map(
                        onClickEvent -> {
                            Timber.d("GOT A TAP");
                            log("GOT A TAP");
                            return 1;
                        })
                .buffer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Integer>>() {
                    @Override
                    public void onNext(List<Integer> integers) {
                        Timber.d("onNext");
                        if (integers.size() > 0) {
                            log(String.format("%d taps", integers.size()));
                        } else {
                            Timber.d("No taps received.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Woops on error!");
                        log("Dang error! check your logs.");
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onCompleted");
                    }
                });
    }

    private void setupLogger() {
        logs = new ArrayList<>();
        adapter = new LogAdapter(getActivity(), new ArrayList<>());
        if (logsList != null) {
            logsList.setAdapter(adapter);
        }
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
}
