package com.android.home.rxjavasamples;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.android.home.R;
import com.android.home.rxjavasamples.retrofit.Contributor;
import com.android.home.rxjavasamples.retrofit.GithubApi;
import com.android.home.rxjavasamples.retrofit.GithubService;
import com.android.home.rxjavasamples.retrofit.User;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;
import static java.lang.String.format;

public class RetrofitFragment extends android.support.v4.app.Fragment {


    private GithubApi githubService;
    private CompositeDisposable disposables;

    private Unbinder unbinder;

    private ArrayAdapter<String> adapter;

    @BindView(R.id.log_list)
    ListView resultList;

    @BindView(R.id.retrofit_contributors_username)
    EditText username;

    @BindView(R.id.retrofit_contributors_repository)
    EditText repo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String githubToken = getResources().getString(R.string.github_oauth_token);

        githubService = GithubService.createGithubService(githubToken);

        disposables = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_retrofit, container, false);

        unbinder = ButterKnife.bind(this, layout);

        adapter = new ArrayAdapter<>(getActivity(), R.layout.item_log, R.id.item_log, new ArrayList<>());

        // Adapter.setNotifyOnChange(true);
        resultList.setAdapter(adapter);

        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disposables.dispose();
    }

    @OnClick(R.id.btn_retrofit_contributors)
    public void onListContributorsClicked() {
        adapter.clear();

        disposables.add(
                githubService
                .contributors(username.getText().toString(), repo.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Contributor>>() {
                    @Override
                    public void onComplete() {
                        Timber.d("Retrofit call 1 completed.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Woops we got an error while getting the list of contributors.");
                    }

                    @Override
                    public void onNext(List<Contributor> contributors) {
                        for (Contributor c : contributors) {
                            adapter.add(
                                    format("%s has made %d contributions to %s",
                                            c.login, c.contributions, repo.getText().toString()));

                            Timber.d(
                                    "%s has made %d contributions to %s",
                                    c.login, c.contributions, repo.getText().toString());
                        }
                    }
                }));
    }

    @OnClick(R.id.btn_retrofit_contributors_with_user_info)
    public void onListContributorsWithFullUserInfoCicked() {
        adapter.clear();

        disposables.add(
                githubService
                .contributors(username.getText().toString(), repo.getText().toString())
                .flatMap(Observable::fromIterable)
                .flatMap(contributor -> {
                    Observable<User> userObservable =
                            githubService
                                    .user(contributor.login)
                            .filter(user -> !isEmpty(user.name) &&
                            !isEmpty(user.email));

                    return Observable.zip(userObservable, Observable.just(contributor), Pair::new);
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(
                        new DisposableObserver<Pair<User, Contributor>>() {
                            @Override
                            public void onComplete() {
                                Timber.d("Retrofit call 2 completed.");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "Error while getting the list of contributors" +
                                        "along with full names.");
                            }

                            @Override
                            public void onNext(Pair<User, Contributor> pair) {
                                User user = pair.first;

                                Contributor contributor = pair.second;

                                adapter.add(
                                        format("%s(%s) has made %d contributions to %s",
                                                user.name, user.email, contributor.contributions,
                                                repo.getText().toString()));

                                adapter.notifyDataSetChanged();

                                Timber.d("%s(%s) has made %d contributions to %s.",
                                        user.name, user.email, contributor.contributions,
                                        repo.getText().toString());
                            }
                        }
                ));
    }
}


