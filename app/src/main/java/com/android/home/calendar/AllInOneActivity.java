package com.android.home.calendar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.home.R;

/**
 * 需要进行大规模的改造。
 * <ul>
 *     <li>没有ActionBar，也没有NavigationBar,所以这部分需要自己定制。</li>
 *     <li>去掉需要google account登录的限制。</li>
 *     <li>修改一些比较含糊的功能。</li>
 * </ul>
 */

public class AllInOneActivity extends AppCompatActivity {

    private ActionBar mActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.all_in_one);

        setSupportActionBar(findViewById(R.id.toolbar));
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.all_in_one_title_bar, menu);

        return true;
    }
}
