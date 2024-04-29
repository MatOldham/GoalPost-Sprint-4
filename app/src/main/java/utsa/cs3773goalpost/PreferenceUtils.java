package utsa.cs3773goalpost;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceUtils {
    private static final String PREFS_NAME = "UserData";
    private static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";

    public static void saveLoggedInEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public static String getLastLoggedInEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String email = prefs.getString(KEY_EMAIL, null);
        Log.d("PreferenceUtils", "Retrieved email: " + email);
        return email;
    }

    public static void saveLoggedInUser(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public static String getLastLoggedInUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = prefs.getString(KEY_USERNAME, null);
        Log.d("PreferenceUtils", "Retrieved username: " + username);
        return username;
    }
}