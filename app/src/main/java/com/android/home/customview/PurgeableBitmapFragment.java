package com.android.home.customview;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

public class PurgeableBitmapFragment extends Fragment {

    private PurgeableBitmapView mView;

    private final RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            int index = mView.update(this);
            if (index > 0) {
                showAlertDialog(getDialogMessage(true, index));
            } else if (index < 0){
                mView.invalidate();
                showAlertDialog(getDialogMessage(false, -index));
            } else {
                mView.invalidate();
            }
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = new PurgeableBitmapView(getActivity(),  detectIfPurgeableRequest());


        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mRedrawHandler.sleep(0);
    }

    private boolean detectIfPurgeableRequest() {
        PackageManager pm = getContext().getPackageManager();
        CharSequence labelSeq = null;
        try {
            ActivityInfo info = pm.getActivityInfo(getActivity().getComponentName(),
                    PackageManager.GET_META_DATA);
            labelSeq = info.loadLabel(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        String[] components = labelSeq.toString().split("/");
        if (components[components.length - 1].equals("Purgeable")) {
            return true;
        } else {
            return false;
        }
    }

    private String getDialogMessage(boolean isOutOfMemory, int index) {
        StringBuilder sb = new StringBuilder();
        if (isOutOfMemory) {
            sb.append("Out of memery occurs when the ");
            sb.append(index);
            sb.append("th Bitmap is decoded.");
        } else {
            sb.append("Complete decoding ")
                    .append(index)
                    .append(" bitmaps without running out of memory.");
        }
        return sb.toString();
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
