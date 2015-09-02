package com.dralong.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jerry on 2015/8/30.
 */
public class PreferenceCache {
    private static PreferenceCache mInstance;
    private SharedPreferences mPreferences;
    public static final String NAME = "PREFERENCE_API_CACHE";

    public synchronized static PreferenceCache getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PreferenceCache(context);
        }
        return mInstance;
    }

    private PreferenceCache(Context context) {
        mPreferences = context.getSharedPreferences(NAME, 0);
    }

    public void putCache(String key, String value) {
        mPreferences.edit().putString(key, value).commit();
    }

    public String getCache(String key) {
        return mPreferences.getString(key, null);
    }

    public void clearCache() {
        mPreferences.edit().clear().commit();
    }
}
