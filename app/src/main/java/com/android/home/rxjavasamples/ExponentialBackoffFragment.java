package com.android.home.rxjavasamples;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BuildCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.android.home.R;
import hu.akarnokd.rxjava2.math.MathFlowable;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.DisposableSubscriber;
import org.reactivestreams.Publisher;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExponentialBackoffFragment extends androidx.fragment.app.Fragment {

    @BindView(R.id.list_threading_log)
    ListView logsList;

    Unbinder unbinder;

    private LogAdapter adapter;

    private CompositeDisposable disposables = new CompositeDisposable();
    private List<String> logs;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_exponential_backoff, container, false);
        unbinder = ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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

    @Override
    public void onPause() {
        super.onPause();

        disposables.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @OnClick(R.id.btn_eb_retry)
    public void startRetryinWithExponentialBackoffStrategy() {
        logs = new ArrayList<>();
        adapter.clear();

        // 在后台运行。
        DisposableSubscriber<Object> disposableSubscriber =
                new DisposableSubscriber<Object>() {
                    @Override
                    public void onNext(Object o) {
                        Timber.d("on Next");
                    }

                    @Override
                    public void onError(Throwable t) {
                        log("Error: I give up!");
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("on Completed");
                    }
                };

        Flowable.error(new RuntimeException("testing"))     //always fails.
            .retryWhen(new RetryWithDelay(5, 1000))     // notice this is called only onError (onNext
                        // values sent are ignored)
            .doOnSubscribe(subscription -> log("Attempting the impossible 5 times in intervals of 1s"))
                .subscribe(disposableSubscriber);

        disposables.add(disposableSubscriber);
    }

    // CAUTION:
    // --------------------------------------
    // THIS notificationHandler class HAS NO BUSINESS BEING non-static
    // I ONLY did this cause i wanted access to the `_log` method from inside here
    // for the purpose of demonstration. In the real world, make it static and LET IT BE!!

    // It's 12am in the morning and i feel lazy dammit !!!

    //public static class RetryWithDelay
    public class RetryWithDelay implements Function<Flowable<? extends Throwable>, Publisher<?>> {

        private final int _maxRetries;
        private final int _retryDelayMillis;
        private int _retryCount;

        public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
            _maxRetries = maxRetries;
            _retryDelayMillis = retryDelayMillis;
            _retryCount = 0;
        }

        // this is a notification handler, all that is cared about here
        // is the emission "type" not emission "content"
        // only onNext triggers a re-subscription (onError + onComplete kills it)

        @Override
        public Publisher<?> apply(Flowable<? extends Throwable> inputObservable) {

            // it is critical to use inputObservable in the chain for the result
            // ignoring it and doing your own thing will break the sequence

            return inputObservable.flatMap(
                    new Function<Throwable, Publisher<?>>() {
                        @Override
                        public Publisher<?> apply(Throwable throwable) {
                            if (++_retryCount < _maxRetries) {

                                // When this Observable calls onNext, the original
                                // Observable will be retried (i.e. re-subscribed)

                                Timber.d("Retrying in %d ms", _retryCount * _retryDelayMillis);
                                log(String.format("Retrying in %d ms", _retryCount * _retryDelayMillis));

                                return Flowable.timer(_retryCount * _retryDelayMillis, TimeUnit.MILLISECONDS);
                            }

                            Timber.d("Argh! I give up");

                            // Max retries hit. Pass an error so the chain is forcibly completed
                            // only onNext triggers a re-subscription (onError + onComplete kills it)
                            return Flowable.error(throwable);
                        }
                    });
        }
    }

    @OnClick(R.id.btn_eb_delay)
    public void startExecutingWithExponentialBackoffDelay() {
        logs = new ArrayList<>();
        adapter.clear();

        DisposableSubscriber<Integer> disposableSubscriber =
                new DisposableSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        Timber.d("executing Task %d [xx:%02d]", integer, getSecondHand());
                        log(String.format("executing Task %d  [xx:%02d]", integer, getSecondHand()));
                    }

                    @Override
                    public void onError(Throwable t) {
                        Timber.d(t, "arrrr. Error");
                        log("Error");
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onCompleted");
                        log("Completed");
                    }
                };

        Flowable.range(1, 4)
                .delay(integer -> {
                    // Rx-y way of doing the Fibonnaci :P
                    return MathFlowable.sumInt(Flowable.range(1, integer))
                            .flatMap(
                                    targetSecondDelay ->
                                            Flowable.just(integer).delay(targetSecondDelay, TimeUnit.SECONDS));
                })
                .doOnSubscribe(
                        s -> log(
                                        String.format(
                                                "Execute 4 tasks with delay - time now: [xx:%02d]", getSecondHand())))
                .subscribe(disposableSubscriber);

        disposables.add(disposableSubscriber);
    }

    private int getSecondHand() {
        long millis = System.currentTimeMillis();

        return (int)(TimeUnit.MILLISECONDS.toSeconds(millis)
                 - TimeUnit.MINUTES.toSeconds(TimeUnit.MICROSECONDS.toMinutes(millis)));
    }
}
