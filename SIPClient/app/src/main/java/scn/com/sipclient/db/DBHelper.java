package scn.com.sipclient.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Html;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

import scn.com.sipclient.model.ContactItem;

/**
 * Created by star on 6/21/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Contact.db";

    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String FAVORITE_TABLE_NAME = "contacts";
    public static final String HISTORY_TABLE_NAME = "contacts";

    public static final String USER_COLUMN_ID = "user_id";
    public static final String USER_COLUMN_CID = "user_cid";
    public static final String USER_COLUMN_NAME = "user_name";
    public static final String USER_COLUMN_EMAIL = "user_email";

    public static final String FAVORITE_COLUMN_ID = "favor_id";
    public static final String FAVORITE_COLUMN_CID = "favor_cid";
    public static final String FAVORITE_COLUMN_NAME = "favor_name";
    public static final String FAVORITE_COLUMN_EMAIL = "favor_email";

    public static final String HISTORY_COLUMN_ID = "favor_id";
    public static final String HISTORY_COLUMN_CID = "favor_cid";
    public static final String HISTORY_COLUMN_NAME = "favor_name";
    public static final String HISTORY_COLUMN_EMAIL = "favor_email";

    private ContentResolver myCR;

    // Tag table create statement
    private static final String CREATE_TABLE_CONTACTS = "CREATE TABLE " + CONTACTS_TABLE_NAME
            + "(" + USER_COLUMN_ID + " INTEGER PRIMARY KEY," + USER_COLUMN_CID + " INTEGER," + USER_COLUMN_NAME + " TEXT, " + USER_COLUMN_EMAIL + " TEXT"
            + ")";

    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE " + FAVORITE_TABLE_NAME
            + "(" + FAVORITE_COLUMN_ID + " INTEGER PRIMARY KEY," + FAVORITE_COLUMN_CID + " INTEGER," + FAVORITE_COLUMN_NAME + " TEXT, " + FAVORITE_COLUMN_EMAIL + " TEXT"
            + ")";

    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE " + HISTORY_TABLE_NAME
            + "(" + HISTORY_COLUMN_ID + " INTEGER PRIMARY KEY," + HISTORY_COLUMN_CID + " INTEGER," + HISTORY_COLUMN_NAME + " TEXT, " + HISTORY_COLUMN_EMAIL + " TEXT"
            + ")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myCR = context.getContentResolver();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_TABLE_CONTACTS);
        db.execSQL(CREATE_TABLE_FAVORITES);
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE_NAME);
        onCreate(db);
    }

    public int numberOfRows(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();

        int numRows = (int) DatabaseUtils.queryNumEntries(db, tableName);
        return numRows;
    }

    public ArrayList<ContactItem> getUserlist(String name) {
        ArrayList<ContactItem> array_list = new ArrayList<ContactItem>();

        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from "+ GROUP_TABLE_NAME, null );
        Cursor res;
        if (TextUtils.isEmpty(name) || name.equals("@ALL@")) {
            res = myCR.query(UserProvider.USER_CONTENT_URI, null, null, null, null);
        } else {
            res = myCR.query(UserProvider.USER_CONTENT_URI, null, USER_COLUMN_NAME + " like '%" + name + "%'", null, null);
        }

        res.moveToFirst();

        while (res.isAfterLast() == false) {
            ContactItem user = new ContactItem();
            user.setId(res.getInt(res.getColumnIndex(USER_COLUMN_CID)));
            user.setUsername(res.getString(res.getColumnIndex(USER_COLUMN_NAME)));
            user.setEmail(res.getString(res.getColumnIndex(USER_COLUMN_EMAIL)));
            array_list.add(user);
            res.moveToNext();
        }

        return array_list;
    }

    public boolean updateUser(ContactItem user) {
        SQLiteDatabase db = this.getWritableDatabase();

        String name = user.getUsername();
        String email = user.getEmail();
        Cursor res = db.rawQuery("select * from " + CONTACTS_TABLE_NAME + " where " + USER_COLUMN_EMAIL + " = '" + email + "' ", null);
        if (res != null && res.getCount() > 0) {
            // User exist alredy, so update group name
            res.moveToFirst();
            int user_id = res.getInt(res.getColumnIndex(USER_COLUMN_ID));
            ContentValues contentValues = new ContentValues();
            contentValues.put(USER_COLUMN_CID, user.getId());
            contentValues.put(USER_COLUMN_NAME, name);
            contentValues.put(USER_COLUMN_EMAIL, email);
            db.update(CONTACTS_TABLE_NAME, contentValues, USER_COLUMN_ID + " = ? ", new String[]{String.valueOf(user_id)});
        } else {
            //user does not exist, so add user.
            ContentValues contentValues = new ContentValues();
            contentValues.put(USER_COLUMN_CID, user.getId());
            contentValues.put(USER_COLUMN_NAME, name);
            contentValues.put(USER_COLUMN_EMAIL, email);
            db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        }
        return true;
    }


    public boolean deleteUser(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(CONTACTS_TABLE_NAME,USER_COLUMN_EMAIL + " = ? ", new String[]{userEmail});
        return true;
    }

    public ArrayList<ContactItem> getFavoritelist(String name) {
        ArrayList<ContactItem> array_list = new ArrayList<ContactItem>();

        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from "+ GROUP_TABLE_NAME, null );
        Cursor res;
        if (TextUtils.isEmpty(name) || name.equals("@ALL@")) {
            res = myCR.query(UserProvider.USER_CONTENT_URI, null, null, null, null);
        } else {
            res = myCR.query(UserProvider.USER_CONTENT_URI, null, USER_COLUMN_NAME + " like '%" + name + "%'", null, null);
        }

        res.moveToFirst();

        while (res.isAfterLast() == false) {
            ContactItem user = new ContactItem();
            user.setId(res.getInt(res.getColumnIndex(USER_COLUMN_CID)));
            user.setUsername(res.getString(res.getColumnIndex(USER_COLUMN_NAME)));
            user.setEmail(res.getString(res.getColumnIndex(USER_COLUMN_EMAIL)));
            array_list.add(user);
            res.moveToNext();
        }

        return array_list;
    }


    public boolean deleteFavorite(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(FAVORITE_TABLE_NAME,FAVORITE_COLUMN_EMAIL + " = ? ", new String[]{userEmail});
        return true;
    }


}
