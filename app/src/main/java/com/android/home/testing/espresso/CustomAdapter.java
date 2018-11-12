package com.android.home.testing.espresso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.home.R;

import java.util.List;

/**
 * Provides views to {@link RecyclerView} with data from a data set.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final List<String> mDataSet;

    private final Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == mDataSet.size() / 2 /* calculate middle element position */) {
            holder.setIsInTheMiddle(true);
            holder.getTextView().setText(mContext.getResources().getString(R.string.middle));
        } else {
            holder.setIsInTheMiddle(false);
            holder.getTextView().setText(mDataSet.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom {@link RecyclerView.ViewHolder}).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        // We'll use this field to showcase matching the holder from the test.
        private boolean mIsInTheMiddle = false;

        ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.textView);
        }

        TextView getTextView() {
            return textView;
        }

        public boolean getIsInTheMiddle() {
            return mIsInTheMiddle;
        }

        void setIsInTheMiddle(boolean isInTheMiddle) {
            mIsInTheMiddle = isInTheMiddle;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    CustomAdapter(List<String> dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;
    }
}
