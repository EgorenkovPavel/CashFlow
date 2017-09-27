package com.epipasha.cashflow;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class Prefs {

    public static boolean isShowOperationMasterOnStart(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getBoolean(context.getString(R.string.pref_show_operation_master_on_start), false));
    }

}
