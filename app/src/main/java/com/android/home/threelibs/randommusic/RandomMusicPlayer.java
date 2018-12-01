package com.android.home.threelibs.randommusic;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.home.Home;
import com.android.home.R;

import static com.android.home.threelibs.randommusic.MusicService.*;

/**
 * This activity shows the media player buttons and lest the user click them.
 * No media handling is done here -- everything is done by passing Intents
 * to our {@link MusicService}.
 */

public class RandomMusicPlayer extends Activity implements View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 1;
    /**
     * The URL we suggest as default when adding by URL. This is just so that the user doesn't
     * have to find an URL to test this sample.
     */
    final String SUGGESTED_URL = Home.APP_SERVER + "audiolibrary/Good_For_The_Soul.mp3";

    Button mPlayButton;
    Button mPauseButton;
    Button mSkipButton;
    Button mRewindButton;
    Button mStopButton;
    Button mEjectButton;

    // Send the correct intent to the MusicService, according to the button that was clicked
    Intent intent;

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.random_music_player);

        intent = new Intent(this, MusicService.class);

        mPlayButton = (Button) findViewById(R.id.play_button);
        mPauseButton = (Button) findViewById(R.id.pause_button);
        mSkipButton = (Button) findViewById(R.id.skip_button);
        mRewindButton = (Button) findViewById(R.id.rewind_button);
        mStopButton = (Button) findViewById(R.id.stop_button);
        mEjectButton = (Button) findViewById(R.id.eject_button);

        mPlayButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        mSkipButton.setOnClickListener(this);
        mRewindButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        mEjectButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == mPlayButton) {
            intent.setAction(ACTION_PLAY);
            startService(intent);
        } else if (v == mPauseButton) {
            intent.setAction(ACTION_PAUSE);
            startService(intent);
        } else if (v == mSkipButton) {
            intent.setAction(ACTION_SKIP);
            startService(intent);
        } else if (v == mRewindButton) {
            intent.setAction(ACTION_REWIND);
            startService(intent);
        } else if (v == mStopButton) {
            intent.setAction(ACTION_STOP);
            startService(intent);
        } else if (v == mEjectButton) {
            showUrlDialog();
        }

    }

    /**
     * Shows an alert dialog where the user can input a URL. After showing the dialog, if the user
     * confirms, sends the appropriate intent to the {@link MusicService} to cause that URL to be
     * played.
     */
    private void showUrlDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Manual Input");
        alertBuilder.setMessage("Enter a URL (must be http://)");
        final EditText input = new EditText(this);
        alertBuilder.setView(input);

        input.setText(SUGGESTED_URL);

        alertBuilder.setPositiveButton("Play!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int whichButton) {
                // Send an intent with the URL of the song to play. This is expected by
                // MusicService.
                intent.setAction(MusicService.ACTION_URL);
                Uri uri = Uri.parse(input.getText().toString());
                intent.setData(uri);
                startService(intent);
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int whichButton) {}
        });

        alertBuilder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)  {
            Log.i(TAG, "Read external storage permission has NOT been granted.");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                            @androidx.annotation.NonNull String[] permissions,
                                           @androidx.annotation.NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION && grantResults[0] == RESULT_OK) {
            Log.d(TAG, "Read external storage permission is granted.");
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
                intent.setAction(ACTION_TOGGLE_PLAYBACK);
                startService(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
