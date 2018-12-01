package com.android.home.threelibs.development;

public class CardItem {

    private String mTextResource;
    private String mTitleResource;

    public CardItem(String title, String text) {
        mTitleResource = title;
        mTextResource = text;
    }

    public String getText() {
        return mTextResource;
    }

    public String getTitle() {
        return mTitleResource;
    }
}
