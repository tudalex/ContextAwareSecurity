package com.tudalex.fingerprint;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by tudalex on 17/08/15.
 */
public class Settings {
    public static String TAG = "Settings";
    public static HashMap<String, String> cache;
    public static String prefName = "finger";
    static {
        cache = new HashMap<>();
        //cache.put("home", "webdev");
    }

    public static String getSetting(Context context, String name) {
        if (cache.containsKey(name))
            return cache.get(name);
        else {

            final String value =  context.getSharedPreferences(prefName, 0).getString(name, null);
            if (value == null) {
                return "$no value$";
            }
            return value;
        }
    }

    public static void setSetting(Context context, String name, String value) {
        Log.d(TAG, String.format("Saving key %s with value %s", name, value));
        cache.remove(name);
        SharedPreferences pref = context.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(name, value);
        editor.commit();
    }
}
