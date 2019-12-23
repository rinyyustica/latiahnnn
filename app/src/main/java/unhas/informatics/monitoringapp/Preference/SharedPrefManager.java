package unhas.informatics.monitoringapp.Preference;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_APP = "pref_app";

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sp = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void setPrefString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getPrefString(String key) {
        return sp.getString(key, "");
    }

    public void setPrefInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getPrefInt(String key) {
        return sp.getInt(key, 0);
    }

    public void setPrefBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getPrefBoolean(String key) {
        return sp.getBoolean(key, false);
    }
}
