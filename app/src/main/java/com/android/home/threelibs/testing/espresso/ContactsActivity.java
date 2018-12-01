package com.android.home.threelibs.testing.espresso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.android.home.R;

/**
 * This a placeholder Activity for a contacts screen. This activity is never opened and does not
 * contain any real contact data for keeping this sample simple and focused.
 */
public class ContactsActivity extends Activity {

    static final String KEY_PHONE_NUMBER = "key_phone_number";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts);

        setResult(Activity.RESULT_OK, createResultData("123-345-6789"));

        finish();
    }

    @VisibleForTesting
    public static Intent createResultData(String phoneNumber) {
        final Intent resultData = new Intent();
        resultData.putExtra(KEY_PHONE_NUMBER, phoneNumber);
        return resultData;
    }
}
