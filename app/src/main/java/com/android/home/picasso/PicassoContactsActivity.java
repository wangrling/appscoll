package com.android.home.picasso;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.home.R;
import com.squareup.picasso.Picasso;

import static android.Manifest.permission.READ_CONTACTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PicassoContactsActivity extends PicassoActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private PicassoContactsAdapter adapter;

    private static final int REQUEST_READ_CONTACTS = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.picasso_contacts_activity);

        adapter = new PicassoContactsAdapter(this);

        ListView lv = findViewById(android.R.id.list);
        lv.setAdapter(adapter);
        lv.setOnScrollListener(new PicassoScrollListener(this));

        if (ActivityCompat.checkSelfPermission(this, READ_CONTACTS) == PERMISSION_GRANTED) {
            loadContacts();
        } else {
            ActivityCompat.requestPermissions(this, new String[] { READ_CONTACTS },
                    REQUEST_READ_CONTACTS);
        }
    }

    private void loadContacts() {
        getSupportLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        if (id == ContactsQuery.QUERY_ID) {
            return new CursorLoader(this,
                    ContactsQuery.CONTENT_URI,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    interface ContactsQuery {
        int QUERY_ID = 1;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

        String SELECTION = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                + "<>''"
                + " AND "
                + ContactsContract.Contacts.IN_VISIBLE_GROUP
                + "=1";

        String SORT_ORDER = ContactsContract.Contacts.SORT_KEY_PRIMARY;

        String[] PROJECTION = {
                ContactsContract.Contacts._ID, //
                ContactsContract.Contacts.LOOKUP_KEY, //
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, //
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI, //
                SORT_ORDER
        };

        int ID = 0;
        int LOOKUP_KEY = 1;
        int DISPLAY_NAME = 2;
    }

    private class PicassoContactsAdapter extends CursorAdapter {
        private final LayoutInflater inflater;

        public PicassoContactsAdapter(Context context) {
            super(context, null, 0);

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View itemLayout = inflater.inflate(R.layout.picasso_contacts_activity_item, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.text1 = itemLayout.findViewById(android.R.id.text1);
            holder.icon = itemLayout.findViewById(android.R.id.icon);

            itemLayout.setTag(holder);

            return itemLayout;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Uri contactUri = ContactsContract.Contacts.getLookupUri(cursor.getLong(ContactsQuery.ID),
                    cursor.getString(ContactsQuery.LOOKUP_KEY));

            ViewHolder holder = (ViewHolder) view.getTag();
            holder.text1.setText(cursor.getString(ContactsQuery.DISPLAY_NAME));
            holder.icon.assignContactUri(contactUri);

            Picasso.get()
                    .load(contactUri)
                    .placeholder(R.drawable.contact_picture_placeholder)
                    .tag(context)
                    .into(holder.icon);
        }

        @Override
        public int getCount() {
            return getCursor() == null ? 0 : super.getCount();
        }
    }

    private static class ViewHolder {
        TextView text1;
        QuickContactBadge icon;
    }
}
