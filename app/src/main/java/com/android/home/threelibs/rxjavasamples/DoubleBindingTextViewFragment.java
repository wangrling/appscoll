package com.android.home.threelibs.rxjavasamples;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import com.android.home.R;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

import static android.text.TextUtils.isEmpty;

public class DoubleBindingTextViewFragment extends Fragment {

    private Unbinder unbinder;

    PublishProcessor<Float> resultEmitterSubject;

    Disposable disposable;

    @BindView(R.id.double_binding_num1)
    EditText number1;

    @BindView(R.id.double_binding_num2)
    EditText number2;

    @BindView(R.id.double_binding_result)
    TextView result;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_double_binding_textview, container, false);

        unbinder = ButterKnife.bind(this, layout);

        resultEmitterSubject = PublishProcessor.create();

        disposable = resultEmitterSubject.subscribe(
                aFloat -> {
                    result.setText(String.valueOf(aFloat));
                }
        );

        onNumberChanged();
        number2.requestFocus();

        return layout;
    }

    @OnTextChanged({R.id.double_binding_num1, R.id.double_binding_num2})
    public void onNumberChanged() {
        float num1 = 0;
        float num2 = 0;

        if (!isEmpty(number1.getText().toString())) {
            num1 = Float.parseFloat(number1.getText().toString());
        }

        if (!isEmpty(number2.getText().toString())) {
            num2 = Float.parseFloat(number2.getText().toString());
        }

        resultEmitterSubject.onNext(num1 + num2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.dispose();
        unbinder.unbind();
    }
}
