package phoenixCorp.taka;

import android.content.Context;
import android.preference.PreferenceManager;

public class USPreferences {
    private static final String PREF_USER_NAME = "userName";
    public static String getStoredUS(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_USER_NAME, null);
    }
    public static void setStoredUS(Context context, String userName) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_USER_NAME, userName).apply();
    }
}
