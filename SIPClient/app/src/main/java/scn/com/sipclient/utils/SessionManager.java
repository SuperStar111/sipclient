package scn.com.sipclient.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import scn.com.sipclient.account.LoginActivity;
import scn.com.sipclient.model.User;
import scn.com.sipclient.net.APIManager;

/**
 * Created by star on 6/29/2016.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "SIPClient";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User ID (make variable public to access from outside)
    public static final String KEY_ID = "user_id";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "user_name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "user_email";

    // Email address (make variable public to access from outside)
    public static final String KEY_TYPE = "user_type";

    // Session ID (make variable public to access from outside)
    public static final String KEY_SESSION = "session_id";



    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(int id, String name, String email,String session_id){
        editor.clear();
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putInt(KEY_ID, id);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing session in pref
//        editor.putString(KEY_SESSION, session_id);

        // commit changes
        editor.commit();

    }

    public void createLoginSession(User user){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putInt(KEY_ID, user.getUserId());

        // Storing name in pref
        editor.putString(KEY_NAME, user.getUserName());

        // Storing email in pref
        editor.putString(KEY_EMAIL, user.getUserEmail());
        // commit changes
        editor.commit();
    }

    public User getUserDetails(){
        User user = new User();

        // user id
        user.setUserId( pref.getInt(KEY_ID, -1));

        // user name
        user.setUserName( pref.getString(KEY_NAME, null));

        // user email
        user.setUserEmail(pref.getString(KEY_EMAIL, null));

        // user session
//        user.setSession_id(pref.getString(KEY_SESSION, null));

        return user;
    }

    public boolean checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
            return true;
        }
        return false;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
    }


    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
