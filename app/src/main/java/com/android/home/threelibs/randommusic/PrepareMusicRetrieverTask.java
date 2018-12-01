package com.android.home.threelibs.randommusic;

import android.os.AsyncTask;

public class PrepareMusicRetrieverTask extends AsyncTask<Void, Void, Void> {

    MusicRetriever mRetriever;

    MusicRetrieverPreparedListener mListener;

    public interface MusicRetrieverPreparedListener {
        public void onMusicRetrieverPrepared();
    }

    public PrepareMusicRetrieverTask(MusicRetriever retriever,
                                     MusicRetrieverPreparedListener listener) {
        mRetriever = retriever;
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mRetriever.prepare();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListener.onMusicRetrieverPrepared();
    }
}
