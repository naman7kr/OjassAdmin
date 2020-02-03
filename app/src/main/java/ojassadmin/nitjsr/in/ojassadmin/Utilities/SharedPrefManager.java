package ojassadmin.nitjsr.in.ojassadmin.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Abhishek on 26-Jan-18.
 */

public class SharedPrefManager {

    private static final String ADMIN_STATUS = "MainAdmin";
    private static final String EMAIL_ID = "email";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private static final String SHARED_PREF = "SharedPref";
    private static final String IS_FIRST_OPEN = "isFirstOpen";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String IS_REGISTERED = "isRegistered";
    private static final String ACCESS_LEVEL = "accessLevel";

    public SharedPrefManager(Context context){
        sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void setIsFirstOpen(boolean isFirstOpen){
        editor.putBoolean(IS_FIRST_OPEN, isFirstOpen).apply();
    }
    public void setEmail(String email){
        editor.putString(EMAIL_ID,email).apply();
    }
    public String getEmail(){
        return sharedPref.getString(EMAIL_ID,null);
    }
    public boolean isFirstOpen(){
        return sharedPref.getBoolean(IS_FIRST_OPEN, true);
    }

    public boolean isLoggedIn() {
        return sharedPref.getBoolean(IS_LOGGED_IN, false);
    }

    public boolean isRegistered(){
        return sharedPref.getBoolean(IS_REGISTERED, false);
    }

    public void setIsLoggedIn(boolean isLoggedIn){
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn).apply();
    }

    public void setIsRegistered(boolean isRegistered){
        editor.putBoolean(IS_REGISTERED, isRegistered).apply();
    }

    public void setAccessLevel(int accessLevel){
        editor.putInt(ACCESS_LEVEL, accessLevel).apply();
    }

    public int getAccessLevel(){
        return sharedPref.getInt(ACCESS_LEVEL, 3);
    }
    public void setAdminStatus(boolean status){
        editor.putBoolean(ADMIN_STATUS,status).apply();
    }
    public boolean getAdminStatus(){
        return  sharedPref.getBoolean(ADMIN_STATUS,false);
    }
}