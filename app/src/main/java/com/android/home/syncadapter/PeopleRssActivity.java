package com.android.home.syncadapter;

import android.os.Bundle;
import android.view.Menu;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import com.android.home.R;

/**
 * Activity for holding EntryListFragment.
 */

// 直接通过网址获取Lutou的信息。

//

public class PeopleRssActivity extends AppCompatActivity {

    public static final String RSS_URL = "http://www.people.com.cn/rss/finance.xml";

    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.basic_sync_adapter);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
    }


}
