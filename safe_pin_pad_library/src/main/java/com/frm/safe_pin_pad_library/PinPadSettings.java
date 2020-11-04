package com.frm.safe_pin_pad_library;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;
import android.util.Log;

import java.util.Set;

public class PinPadSettings {

    private static final String PREFS_NAME = "pin_pad_settings";
    private static final String PREF_PIN = "pin";
    private static final String PREF_PIN_TYPE = "pin_type";
    private static final String PREF_PINLESS_TIME = "pinless_time";
    private static final String PREF_LAST_PIN_TIMESTAMP = "pref_last_pin_timestamp";

    static void setPin(Context context, String pin) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        prefs.edit().putString(PREF_PIN, pin).apply();
    }

    static String getPin(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String pinAsString = prefs.getString(PREF_PIN, "");
        return pinAsString;
    }

    static void setPinType(Context context, String type) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_PIN_TYPE, type).apply();
    }

    static String getPinType(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_PIN_TYPE, "");
    }

    static void setPinlessTime(Context context, int seconds) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(PREF_PINLESS_TIME, seconds).apply();
    }

    static int getPinlessTime(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_PINLESS_TIME, 0);
    }

    static void setLastPinTimestamp(Context context, long seconds) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(PREF_LAST_PIN_TIMESTAMP, seconds).apply();
    }

    static long getLastPinTimestamp(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(PREF_LAST_PIN_TIMESTAMP, 0);
    }

    static boolean isPinExist(Context context) {
        return !"".equals(getPin(context));
    }
}
