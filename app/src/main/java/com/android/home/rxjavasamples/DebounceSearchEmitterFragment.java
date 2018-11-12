package com.android.home.rxjavasamples;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.android.home.R;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public class DebounceSearchEmitterFragment extends Fragment {

    @BindView(R.id.list_threading_log)
    ListView logsList;

    @BindView(R.id.input_txt_debounce)
    EditText inputSearchText;

    private LogAdapter adapter;
    private List<String> logs;

    private Disposable disposable;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_debounce, container, false);
        unbinder = ButterKnife.bind(this, layout);

        return layout;
    }

    @OnClick(R.id.clr_debounce)
    public void onClearLog() {
        logs = new ArrayList<>();
        adapter.clear();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupLogger();

        disposable = RxTextView.textChangeEvents(inputSearchText)
                .debounce(400, TimeUnit.MILLISECONDS)       // Default scheduler is computation.
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getSearchObserver());
    }

    private DisposableObserver<TextViewTextChangeEvent> getSearchObserver() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                log(format("Searching for %s", textViewTextChangeEvent.text().toString()));
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Woops on error!");
                log("Dang error. check your logs");
            }

            @Override
            public void onComplete() {
                Timber.d("onComplete");
            }
        };
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


    private class LogAdapter extends ArrayAdapter<String> {
        public LogAdapter(Context context, List<String> logs) {
            super(context, R.layout.item_log, R.id.item_log, logs);
        }
    }
}
