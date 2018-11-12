package com.android.home.rxjavasamples;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.android.home.R;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import org.reactivestreams.Publisher;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PollingFragment extends Fragment {

    private static final int INITIAL_DELAY = 0;
    private static final int POLLING_INTERVAL = 1000;
    private static final int POLL_COUNT = 8;

    private int counter;

    @BindView(R.id.list_threading_log)
    ListView logsList;
    private LogAdapter adapter;
    private List<String> logs;

    // 处理
    private CompositeDisposable disposables;

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disposables = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_polling, container, false);

        unbinder = ButterKnife.bind(this, layout);

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupLogger();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disposables.clear();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_start_simple_polling)
    public void onStartSimplePollingClicked() {

        final int pollCount = POLL_COUNT;

        Disposable d =
                Flowable.interval(INITIAL_DELAY, POLLING_INTERVAL, TimeUnit.MILLISECONDS)
                .map(this::doNetworkCallAndGetStringResult)
                .take(pollCount)
                        // 只执行一次。
                .doOnSubscribe(subscription -> {
                    log(String.format("Start simple polling -%s", counter));
                })
                        // 可以执行很多次。
                .subscribe(
                        taskName -> {
                            log(String.format(Locale.US,
                                    "Executing polled task [%s] now time: [xx:%02d]",
                                    taskName, getSecondHand()));
                        });

        disposables.add(d);
    }

    @OnClick(R.id.btn_start_increasingly_delayed_polling)
    public void onStartIncreasingDelayedPolling() {
        setupLogger();

        final int pollingInterval = POLLING_INTERVAL;
        final int pollCount = POLL_COUNT;

        log(String.format(
                Locale.US, "Start increasing delayed polling now time: [xx:%02d]", getSecondHand()));

        disposables.add(
                Flowable.just(1L)
                .repeatWhen(new RepeatWithDelay(pollCount, pollingInterval))
                .subscribe(
                        taskName -> {
                            log(String.format(Locale.US,
                                    "Executing polled task [%s] now time: [xx:%02d]",
                                    taskName, getSecondHand()));
                        }
                )
        );
    }

    public class RepeatWithDelay implements Function<Flowable<Object>, Publisher<Long>> {

        private final int repeatLimit;
        private final int pollingInterval;
        private int repeatCount;

        RepeatWithDelay(int repeatLimit, int pollingInterval) {
            this.pollingInterval = pollingInterval;
            this.repeatLimit = repeatLimit;
        }

        // this is a notificationhandler, all we care about is
        // the emission "type" not emission "content"
        // only onNext triggers a re-subscription
        @Override
        public Publisher<Long> apply(Flowable<Object> inputFlowable) throws Exception {
            // it is critical to use inputObservable in the chain for the result
            // ignoring it and doing your own thing will break the sequence
            return inputFlowable.flatMap(
                    new Function<Object, Publisher<Long>>() {
                        @Override
                        public Publisher<Long> apply(Object o) {
                            if (repeatCount >= repeatLimit) {
                                log("Completing sequence");
                                return Flowable.empty();
                            }

                            // Since we don't get an input.
                            // We store state in this handler to tell us the point of time
                            // we're firing.
                            repeatCount++;

                            return Flowable.timer(repeatCount * pollingInterval, TimeUnit.MILLISECONDS);
                        }
                    }
            );
        }
    }

    // -----------------------------------------------------------------------------------
    // Method that help wiring up the example (irrelevant to RxJava)

    private int getSecondHand() {
        long millis = System.currentTimeMillis();
        return (int)
                (TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }


    // -----------------------------------------------------------------------------------

    // CAUTION:
    // --------------------------------------
    // THIS notificationHandler class HAS NO BUSINESS BEING non-static
    // I ONLY did this cause i wanted access to the `_log` method from inside here
    // for the purpose of demonstration. In the real world, make it static and LET IT BE!!

    // It's 12am in the morning and i feel lazy dammit !!!
    private String doNetworkCallAndGetStringResult(long attempt) {
        try {
            if (attempt == 4) {
                // randomly make one event super long so we test that the repeat logic waits
                // and accounts for this.
                Thread.sleep(9000);
            } else {
                Thread.sleep(3000);
            }

        } catch (InterruptedException e) {
            Timber.d("Operation was interrupted");
        }
        counter++;

        return String.valueOf(counter);
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
