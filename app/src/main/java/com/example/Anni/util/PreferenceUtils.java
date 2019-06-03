package com.example.Anni.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceUtils {
    private Context mContext;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private static final String PREFS_NAME = "PREF_ANNI";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";


    public PreferenceUtils(Context context) {
        this.mContext = context;
        prefs = mContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }

    private SharedPreferences getPrefs() {
        return prefs;
    }

    private SharedPreferences.Editor getPrefsEditor() {
        return prefsEditor;
    }

    public void saveStringToPref(String key, String value) {
        getPrefsEditor().putString(key, value);
        getPrefsEditor().apply();
    }

    public String getStringFromPref(String key, String value) {
        return getPrefs().getString(key, value);
    }

    public void saveBooleanToPref(String key, boolean value) {
        getPrefsEditor().putBoolean(key, value);
        getPrefsEditor().apply();
    }

    public boolean getBooleanFromPref(String key, boolean value) {
        return getPrefs().getBoolean(key, value);
    }

    public void saveIntegerToPref(String key, int value) {
        getPrefsEditor().putInt(key, value);
        getPrefsEditor().apply();
    }

    public void clearPreferences() {
        getPrefsEditor().clear();
        getPrefsEditor().apply();
    }

    public int getIntegerFromPref(String key, int value) {
        return getPrefs().getInt(key, value);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        getPrefsEditor().putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        getPrefsEditor().apply();
    }

    public boolean isFirstTimeLaunch() {
        return getPrefs().getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


}