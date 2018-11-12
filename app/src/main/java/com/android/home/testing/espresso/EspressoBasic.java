package com.android.home.testing.espresso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * An {@link Activity} that gets a text string from the user and displays it back when the user
 * clicks on one of the two buttons. The first one shows it in the same activity and the second
 * one opens another activity and displays the message.
 */


public class EspressoBasic extends Activity implements View.OnClickListener {

    // The TextView used to display the message inside the Activity.
    private TextView mTextView;

    // The EditText where the user types the message.
    private EditText mEditText;

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.espresso_basic);

        // Set the listeners for the buttons.
        findViewById(R.id.changeTextBt).setOnClickListener(this);
        findViewById(R.id.activityChangeTextBtn).setOnClickListener(this);

        mTextView = (TextView) findViewById(R.id.textToBeChanged);
        mEditText = (EditText) findViewById(R.id.editTextUserInput);
    }

    @Override
    public void onClick(View view) {
        // Get the text from the EditTextView.
        final String text = mEditText.getText().toString();

        final int changeTextBtId = R.id.changeTextBt;
        final int activityChangeTextBtnId = R.id.activityChangeTextBtn;

        if (view.getId() == changeTextBtId) {
            // First button's interaction: set a text in a text view.
            mTextView.setText(text);
        } else if (view.getId() == activityChangeTextBtnId) {
            // Second button's interaction: start an activity and send a message to it.
            Intent intent = com.android.home.testing.espresso.ShowTextActivity
                    .newStartIntent(this, text);
            startActivity(intent);
        }
    }
}
