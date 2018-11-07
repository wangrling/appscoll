package com.android.home.rssimage;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 *
 * Defines constants for accessing the content provider defined in DataProvider. A content provider
 * contract assists in accessing the provider's available content URIs, column names, MIME types,
 * and so forth, without having to know the actual values.
 */

public class DataProviderContract implements BaseColumns {

    // The content provider database name
    public static final String DATABASE_NAME = "PictureDataDB";

    // The starting version of the database
    public static final int DATABASE_VERSION = 1;

    // The URI scheme used for content URIs.
    public static final String SCHEME = "content";

    // The provider's authority.
    public static final String AUTHORITY = "com.android.home.rssimage";

    /**
     * The DataProvider content URI
     */
    public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);

    /**
     *  The MIME type for a content URI that would return multiple rows.
     *  <P>Type: TEXT</P>
     */
    public static final String MIME_TYPE_ROWS =
            "vnd.android.cursor.dir/vnd.com.android.home.rssimage";

    /**
     * The MIME type for a content URI that would return a single row.
     *  <P>Type: TEXT</P>
     *
     */
    public static final String MIME_TYPE_SINGLE_ROW =
            "vnd.android.cursor.item/vnd.com.android.home.rssimage";

    /**
     * Picture table primary key column name
     */
    public static final String ROW_ID = BaseColumns._ID;

    /**
     * Picture table name.
     */
    public static final String PICTURE_URL_TABLE_NAME = "PictureUrlData";

    /**
     * Picture table content URI.
     */
    public static final Uri PICTURE_URL_TABLE_CONTENT_URI =
            Uri.withAppendedPath(CONTENT_URI, PICTURE_URL_TABLE_NAME);

    /**
     * Picture table thumbnail URL column name
     */
    public static final String IMAGE_THUMB_URL_COLUMN = "ThumbUrl";

    /**
     * Picture table thumbnail filename column name
     */
    public static final String IMAGE_THUMB_NAME_COLUMN = "ThumbUrlName";

    /**
     * Picture table full picture URL column name
     */
    public static final String IMAGE_URL_COLUMN = "ImageUrl";

    /**
     * Picture table full picture filename column name
     */
    public static final String IMAGE_PICTURE_NAME_COLUMN = "ImageName";

    // 定义第二个table，修改日期。
    /**
     * Modification date table name.
     */
    public static final String DATE_TABLE_NAME = "DateMetadataData";

    /**
     * Content URI modification date table.
     */
    public static final Uri DATE_TABLE_CONTENT_URI =
            Uri.withAppendedPath(CONTENT_URI, DATE_TABLE_NAME);

    /**
     * Modification date table date column name
     */
    public static final String DATA_DATE_COLUMN = "DownloadDate";

}
