package com.android.home.stepsensor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.android.home.R;

public class BatchStepSensor extends FragmentActivity {

    static final String TAG = "BatchStepSensor";

    public static final String FRAGTAG = "BatchStepSensorFragment";

    private CardStreamFragment mCardStreamFragment;

    private StreamRetentionFragment mRetentionFragment;

    private static final String RETENTION_TAG = "retention";
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

        // Use fragment as click listener for cards, but
        // must implement correct interface.
        if (!(fragment instanceof OnCardClickListener)) {
            throw new ClassCastException("BatchStepSensorFragment must " +
                    "implement OnCardClickListener interface.");
        }

        OnCardClickListener clickListener =
                (OnCardClickListener) fm.findFragmentByTag(FRAGTAG);

        mRetentionFragment =
                (StreamRetentionFragment) fm.findFragmentByTag(RETENTION_TAG);

        if (mRetentionFragment == null) {
            mRetentionFragment = new StreamRetentionFragment();
            fm.beginTransaction().add(mRetentionFragment, RETENTION_TAG).commit();
        } else {

            // If the retention fragment already existed, we need to pull some state.
            // pull state out.
            CardStreamState state = mRetentionFragment.getCardStream();

            // Dump it in CardStreamFragment.
            mCardStreamFragment =
                    (CardStreamFragment) fm.findFragmentById(R.id.fragment_cardstream);
            mCardStreamFragment.restoreState(state, clickListener);
        }
    }

    public CardStreamFragment getCardStream() {

        if (mCardStreamFragment == null) {
            mCardStreamFragment = (CardStreamFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_cardstream);
        }

        return mCardStreamFragment;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        CardStreamState state = getCardStream().dumpState();

        mRetentionFragment.storeCardStream(state);
    }
}
