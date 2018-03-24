package com.epipasha.cashflow;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.epipasha.cashflow.objects.OperationType;

public final class Prefs {

    public static boolean isShowOperationMasterOnStart(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getBoolean(context.getString(R.string.pref_show_operation_master_on_start), false));
    }

    public static int getSelectedTab(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getInt(context.getString(R.string.pref_selected_tab), 0));
    }

    public static void setSelectedTab(Context context, int selectedTab){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context.getString(R.string.pref_selected_tab), selectedTab);
        editor.apply();
    }

    public static class OperationMasterPrefs {

        public static OperationType getOperationType(Context context){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return OperationType.toEnum(
                    prefs.getInt(
                            context.getString(R.string.pref_operation_master_operation_type_pos),
                            OperationType.IN.toDbValue()));
        }

        public static int getAccountId(Context context){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return (prefs.getInt(context.getString(R.string.pref_operation_master_account_pos), 0));
        }

        public static int getAnalyticId(Context context, OperationType type){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return (prefs.getInt(context.getString(getAnalyticPref(type)), 0));
        }

        public static void saveOperationType(Context context, OperationType type){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(context.getString(R.string.pref_operation_master_operation_type_pos), type.toDbValue());
            editor.apply();
        }

        public static void saveAccountId(Context context, int accountId){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(context.getString(R.string.pref_operation_master_account_pos), accountId);
            editor.apply();
        }

        public static void saveAnalyticId(Context context, int analyticId, OperationType type){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(context.getString(getAnalyticPref(type)), analyticId);
            editor.apply();
        }

        private static int getAnalyticPref(OperationType type){

            switch (type){
                case IN:
                    return R.string.pref_operation_master_analytic_in;
                case OUT:
                    return R.string.pref_operation_master_analytic_out;
                case TRANSFER:
                    return R.string.pref_operation_master_analytic_transfer;
                default:
                    throw new RuntimeException("Operation type not found " + type);
            }
        }
    }

}
