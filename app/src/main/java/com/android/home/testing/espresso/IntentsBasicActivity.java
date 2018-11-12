package com.android.home.testing.espresso;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.android.home.R;

/**
 * Simple Dialer Activity which shows an {@link EditText} field to enter a phone number. Upon
 * pressing the call button the number entered in the input field is send to the native Android
 * Dialer app via {@link Intent#ACTION_CALL}.
 *
 * <p>
 * Furthermore this Activity contains a pick number button to starts dummy contacts activity to
 * demonstrate Intent stubbing.
 */


public class IntentsBasicActivity extends Activity {

    private static final int REQUEST_CODE_PICK = 16;
    private static final int REQUEST_CALL_PHONE = 17;

    private EditText mCallerNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intents_basic);
        mCallerNumber = findViewById(R.id.edit_text_caller_number);
    }

    public void onCall(View view) {
        boolean hasCallPhonePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;

        if (hasCallPhonePermission)
            startActivity(createCallIntentFromNumber());
        else {
            // Toast.makeText(this, R.string.warning_call_phone_permission, Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
            REQUEST_CALL_PHONE);
        }
    }

    public void onPickContact(View view) {
        final Intent pickContactIntent = new Intent(this, ContactsActivity.class);
        startActivityForResult(pickContactIntent, REQUEST_CODE_PICK);
    }

    private Intent createCallIntentFromNumber() {
        final Intent intentToCall = new Intent(Intent.ACTION_CALL);

        String number = mCallerNumber.getText().toString();

        intentToCall.setData(Uri.parse("tel:" + number));

        return intentToCall;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK) {
            if (resultCode == RESULT_OK) {
                mCallerNumber.setText(data.getExtras()
                        .getString(ContactsActivity.KEY_PHONE_NUMBER));
            }
        }
    }
}
