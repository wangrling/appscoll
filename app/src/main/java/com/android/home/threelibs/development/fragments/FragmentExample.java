package com.android.home.threelibs.development.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.android.home.R;

import static android.widget.Toast.LENGTH_SHORT;

public class FragmentExample extends Activity implements
        SimpleFragment.OnFragmentInteractionListener {

    private Button mButton;

    private boolean isFragmentDisplayed = false;

    // Saved instance state key.
    static final String STATE_FRAGMENT = "state_of_fragment";

    static final String STATE_CHOICE = "user_choice";

    // The radio button choice default is 2 = (no choice).
    // Initialize the radio button choice to the default.
    private int mRadioButtonChoice = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_example);

        mButton = findViewById(R.id.open_button);

        // Get the button for opening and closing, get the
        // fragment state and set the button text.

        if (savedInstanceState != null) {
            isFragmentDisplayed = savedInstanceState.getBoolean(STATE_FRAGMENT);
            mRadioButtonChoice = savedInstanceState.getInt(STATE_CHOICE);

            if (isFragmentDisplayed) {
                // If the fragment is displayed, change button to "close"
                mButton.setText(R.string.close);
            }
        }

        // Set the click listener for the button.
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFragmentDisplayed) {
                    displayFragment();
                } else {
                    closeFragment();
                }
            }
        });
    }

    /**
     * This method is called when the user clicks the button
     * to open the fragment.
     */
    public void displayFragment() {
        // Instantiate the fragment.
        // SimpleFragment simpleFragment = SimpleFragment.newInstance();
        SimpleFragment simpleFragment = SimpleFragment.newInstance(mRadioButtonChoice);

        // Get the FragmentManager and start a transaction.
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add the SimpleFragment.
        fragmentTransaction.add(R.id.fragment_container,
                simpleFragment).addToBackStack(null).commit();

        // Update the Button text.
        mButton.setText(R.string.close);

        // Set boolean flag to indicate fragmetn is open.
        isFragmentDisplayed = true;
    }

    /**
     * This method is called when the user clicks the button to
     * close the fragment.
     */
    public void closeFragment() {
        // Get the FragmentManager.
        FragmentManager fragmentManager = getFragmentManager();

        // Check to see if the fragment is already showing.
        SimpleFragment simpleFragment = (SimpleFragment) fragmentManager
                .findFragmentById(R.id.fragment_container);

        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();

            fragmentTransaction.remove(simpleFragment).commit();
        }

        // Update the Button text.
        mButton.setText(R.string.open);

        // Set boolean flag to indicate fragment is closed.
        isFragmentDisplayed = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Save the state of the fragment (true = open, false = closed).
        outState.putBoolean(STATE_FRAGMENT, isFragmentDisplayed);
        outState.putInt(STATE_CHOICE, mRadioButtonChoice);

        super.onSaveInstanceState(outState);
    }

    /**
     * This method keeps in memory the radio button choice the user selected
     * and displays a Toast to show it.
     *
     * @param choice The user's radio button choice.
     */
    @Override
    public void onRadioButtonChoice(int choice) {
        // Keep the radio button choice to pass it back to the fragment.
        mRadioButtonChoice = choice;

        // Show a Toast with the radio button choice.
        Toast.makeText(this, "Choice is " + Integer.toString(choice),
                LENGTH_SHORT).show();
    }
}
