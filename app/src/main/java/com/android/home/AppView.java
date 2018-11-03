package com.android.home;

public class AppView {
    // 显示程序的图标。
    private int iconUrl;
    // 显示程序的标题。
    private String title;
    //　显示程序的介绍。
    private String intro;

    // 供Intent调用。
    private Class activityClass;

    public AppView(int iconUrl, String title, String intro, Class activityClass) {
        this.iconUrl = iconUrl;
        this.title = title;
        this.intro = intro;
        this.activityClass = activityClass;
    }

    public int getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(int iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Class getActivityClass() {
        return activityClass;
    }
}
