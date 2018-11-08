package com.android.home.stepsensor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.android.home.R;

public class BatchStepSensor extends FragmentActivity {

    public static final String FRAGTAG = "BatchStepSensorFragment";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.batch_step_sensor);

        FragmentManager fm = getSupportFragmentManager();

        BatchStepSensorFragment fragment =
                (BatchStepSensorFragment) fm.findFragmentByTag(FRAGTAG);

        if (fragment == null) {
            FragmentTransaction transaction = fm.beginTransaction();

            fragment = new BatchStepSensorFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }
    }
}
