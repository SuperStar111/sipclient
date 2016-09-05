package scn.com.sipclient.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class UserProvider extends ContentProvider {
    private static final String AUTHORITY =
            "scn.com.sipclient.db.UserProvider";
    public static final String USER_TABLE_NAME = "users";
    public static final String FAVORITE_TABLE_NAME = "favorites";
    public static final String HISTORY_TABLE_NAME = "history";

    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + USER_TABLE_NAME);
    public static final Uri FAVORITE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + FAVORITE_TABLE_NAME);
    public static final Uri HISTORY_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + HISTORY_TABLE_NAME);

    public static final int USERS = 1;
    public static final int USER_ID = 2;
    public static final int FAVORITES = 3;
    public static final int FAVORITE_ID = 4;
    public static final int HISTORYS = 5;
    public static final int HISTORY_ID = 6;

    private static final UriMatcher sURIMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, USER_TABLE_NAME, USERS);
        sURIMatcher.addURI(AUTHORITY, USER_TABLE_NAME + "/#",
                USER_ID);
        sURIMatcher.addURI(AUTHORITY, FAVORITE_TABLE_NAME, FAVORITES);
        sURIMatcher.addURI(AUTHORITY, FAVORITE_TABLE_NAME + "/#",
                FAVORITE_ID);
        sURIMatcher.addURI(AUTHORITY, HISTORY_TABLE_NAME, HISTORYS);
        sURIMatcher.addURI(AUTHORITY, HISTORY_TABLE_NAME + "/#",
                HISTORY_ID);
    }

    private DBHelper dbHelper;

    public UserProvider() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        String id;
        switch (uriType) {
            case USERS:
                rowsDeleted = sqlDB.delete(DBHelper.CONTACTS_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case USER_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DBHelper.CONTACTS_TABLE_NAME,
                            DBHelper.USER_COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(DBHelper.CONTACTS_TABLE_NAME,
                            DBHelper.USER_COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (sURIMatcher.match(uri)){
            /**
             * Get all student records
             */
            case USERS:
                return "vnd.android.cursor.dir/vnd.example.users";

            /**
             * Get a particular student
             */
            case USER_ID:
                return "vnd.android.cursor.item/vnd.example.users";

            case FAVORITES:
                return "vnd.android.cursor.dir/vnd.example.favorites";

            /**
             * Get a particular student
             */
            case FAVORITE_ID:
                return "vnd.android.cursor.item/vnd.example.favorites";

            case HISTORYS:
                return "vnd.android.cursor.dir/vnd.example.historys";

            /**
             * Get a particular student
             */
            case HISTORY_ID:
                return "vnd.android.cursor.item/vnd.example.historys";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();

        long id = 0;
        switch (uriType) {
            case USERS:
                id = sqlDB.insert(DBHelper.CONTACTS_TABLE_NAME,
                        null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(USER_TABLE_NAME + "/" + id);


            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        dbHelper = new DBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case USER_ID:
                queryBuilder.appendWhere(DBHelper.USER_COLUMN_ID + "="
                        + uri.getLastPathSegment());
                queryBuilder.setTables(DBHelper.CONTACTS_TABLE_NAME);
                break;
            case USERS:
                queryBuilder.setTables(DBHelper.CONTACTS_TABLE_NAME);
                break;


            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case USERS:
                rowsUpdated = sqlDB.update(DBHelper.CONTACTS_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case USER_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            sqlDB.update(DBHelper.CONTACTS_TABLE_NAME,
                                    values,
                                    DBHelper.USER_COLUMN_ID + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            sqlDB.update(DBHelper.CONTACTS_TABLE_NAME,
                                    values,
                                    DBHelper.USER_COLUMN_ID + "=" + id
                                            + " and "
                                            + selection,
                                    selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
