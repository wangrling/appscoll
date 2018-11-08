package com.android.home.stepsensor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.home.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;



public class CardStreamFragment extends Fragment {

    private static final int INITIAL_SIZE = 15;
    private CardStreamLinearLayout mLayout = null;

    private LinkedHashMap<String, Card> mVisibleCards =
            new LinkedHashMap<String, Card>(INITIAL_SIZE);
    private HashMap<String, Card> mHiddenCards =
            new HashMap<String, Card>(INITIAL_SIZE);
    private HashSet<String> mDismissibleCards =
            new HashSet<String>(INITIAL_SIZE);

    // Set the listener to handle dismissed cars by moving them to
    // the hidden cards map.
    private CardStreamLinearLayout.OnDismissListener mCardDismissListener =
            new CardStreamLinearLayout.OnDismissListener() {
                @Override
                public void onDismiss(String tag) {

                }
            };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cardstream, container, false);
        mLayout = (CardStreamLinearLayout) view.findViewById(R.id.card_stream);
        mLayout.setOnDismisListener(mCardDismissListener);

        return view;
    }

    public void restoreState(CardStreamState state, OnCardClickListener clickListener) {

    }

    public CardStreamState dumpState() {
        return null;
    }
}
