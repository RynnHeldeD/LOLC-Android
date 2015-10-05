package org.ema.utils;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;

public class SettingsManager extends PreferenceActivity {
    public static final String PREFERENCES_FILE_NAME = "preferences";

    public String get(Activity activity, String varName){
        SharedPreferences settings = activity.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getString(varName, "");
    }

    public void set(Activity activity, String varName, String value){
        SharedPreferences settings = activity.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(varName, value);
        editor.commit();
    }
}
