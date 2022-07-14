package rasel.neo.crushr;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class BaseUtils {
    @SuppressLint("MutatingSharedPrefs")
    protected static void addItem(Context ctx, String item, int id) {
        SharedPreferences prefs = ctx.getSharedPreferences(CrushrProvider.SHARED_PREF_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = prefs.getStringSet(CrushrProvider.SHARED_PREF_LIST+id, new HashSet<>());
        set.add(item);
        editor.remove(CrushrProvider.SHARED_PREF_LIST+id);
        editor.apply();
        editor.putStringSet(CrushrProvider.SHARED_PREF_LIST+id, set);
        editor.apply();
    }

    @SuppressLint("MutatingSharedPrefs")
    protected static void removeItem(Context ctx, String item, int id) {
        SharedPreferences prefs = ctx.getSharedPreferences(CrushrProvider.SHARED_PREF_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = prefs.getStringSet(CrushrProvider.SHARED_PREF_LIST+id, new HashSet<>());
        set.remove(item);
        editor.remove(CrushrProvider.SHARED_PREF_LIST+id);
        editor.apply();
        editor.putStringSet(CrushrProvider.SHARED_PREF_LIST+id, set);
        editor.apply();
        if(ExtraUtils.notificationExist(ctx)) {
            NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NotificationReceiver.NOTIFY_ID);
        }
    }

    protected static void setPrimaryColor(Context ctx, int color, int id) {
        SharedPreferences prefs = ctx.getSharedPreferences(CrushrProvider.SHARED_PREF_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(CrushrProvider.SHARED_PREF_PRIMARY_COLOR + id, color);
        editor.apply();
    }

    protected static void setSecondaryColor(Context ctx, int color, int id) {
        SharedPreferences prefs = ctx.getSharedPreferences(CrushrProvider.SHARED_PREF_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(CrushrProvider.SHARED_PREF_SECONDARY_COLOR + id, color);
        editor.apply();
    }
}
