package com.android.home.development.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.internal.ListenerClass;
import com.android.home.R;

import java.text.ChoiceFormat;

public class SimpleFragment extends Fragment {

    private static final int YES = 0;
    private static final int NO = 1;
    private static final int NONE = 2;

    // Initialize the choice to the default (no choice)
    private int mRadioButtonChoice = NONE;

    // The "choice" key for the bundle.
    private static final String CHOICE = "choice";

    public SimpleFragment() {

        // Required empty public constructor.
    }

    /*
    public static SimpleFragment newInstance() {
        return new SimpleFragment();
    }
    */

    public static SimpleFragment newInstance(int choice) {
        SimpleFragment fragment = new SimpleFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(CHOICE, choice);

        fragment.setArguments(arguments);

        return fragment;
    }

    // The listener interface.
    OnFragmentInteractionListener mListener;

    interface OnFragmentInteractionListener {

        void onRadioButtonChoice(int choice);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implements OnFramgentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        // return inflater.inflate(R.layout.fragment_simple, container, false);

        final View rootView = inflater.inflate(R.layout.fragment_simple, container, false);
        final RadioGroup radioGroup = rootView.findViewById(R.id.radio_group);

        // If user reopens the fragment after making a choice, the bundle
        // contains "choice".
        if (getArguments().containsKey(CHOICE)) {
            // A choice was mode, so get the choice.
            mRadioButtonChoice = getArguments().getInt(CHOICE);

            // Check the radio button choice.
            if (mRadioButtonChoice != NONE) {
                radioGroup.check(radioGroup.getChildAt(mRadioButtonChoice).getId());
            }
        }

        // TODO: Set the radioGroup onCheckedChanged listener.
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);

                TextView textView =
                        rootView.findViewById(R.id.fragment_header);

                switch (index) {
                    case YES:       // User chose "Yes."
                        textView.setText(R.string.yes_message);
                        mRadioButtonChoice = YES;
                        mListener.onRadioButtonChoice(YES);
                        break;
                    case NO:        // User chose "No."
                        textView.setText(R.string.no_message);
                        mRadioButtonChoice = NO;
                        mListener.onRadioButtonChoice(NO);
                        break;
                    default: // No choice made.
                             // Do nothing.
                        mRadioButtonChoice = NONE;
                        mListener.onRadioButtonChoice(NONE);
                        break;
                }
            }
        });

        RatingBar ratingBar = rootView.findViewById(R.id.ratingBar);
        // Set the rating bar onCheckedChanged listener.
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Get rating and show Toast with rating.
                String myRating = (getString(R.string.my_rating) +
                    String.valueOf(ratingBar.getRating()));

                Toast.makeText(getContext(), myRating, Toast.LENGTH_SHORT).show();
            }
        });

        // Return the View for the fragment's UI.
        return rootView;
    }
}
