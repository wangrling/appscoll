package com.android.home.stepsensor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class StreamRetentionFragment extends Fragment {

    CardStreamState mState;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
    }

    public void storeCardStream(CardStreamState state) {

        mState = state;
    }

    public CardStreamState getCardStream() {
        return mState;
    }
}
