package com.jge.letsgo.database;

import android.content.Context;
import android.content.SharedPreferences;


public class GoLocationPreference {
    public static final String GOLOCATION_PREF = "goLocationsSharedPrefs";

    public static void savePreferenceNetworkLoaded(Context context, Boolean networkLoaded){
        SharedPreferences.Editor pref = context.getSharedPreferences(GOLOCATION_PREF, Context.MODE_PRIVATE).edit();
        pref.putBoolean("placeholder", networkLoaded);
        pref.apply();
    }

    public static boolean getPreferenceNetworkLoaded(Context context){
        SharedPreferences pref = context.getSharedPreferences(GOLOCATION_PREF, Context.MODE_PRIVATE);
        return pref.getBoolean("placeholder", false);

    }
}
