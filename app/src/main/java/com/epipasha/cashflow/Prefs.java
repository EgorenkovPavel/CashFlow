package com.epipasha.cashflow;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.epipasha.cashflow.objects.OperationType;

public final class Prefs {

    public static boolean isShowOperationMasterOnStart(Context context){
        return getBooleanPref(context, R.string.pref_show_operation_master_on_start, false);
    }

    public static int getSelectedTab(Context context){
        return getIntPref(context, R.string.pref_selected_tab, 0);
    }

    public static void setSelectedTab(Context context, int selectedTab){
        saveIntPref(context, R.string.pref_selected_tab, selectedTab);
    }

    public static class OperationMasterPrefs {

        public static OperationType getOperationType(Context context){
            return OperationType.toEnum(
                    getIntPref(context, R.string.pref_operation_master_operation_type_pos, OperationType.IN.toDbValue()));
        }

        public static int getAccountId(Context context){
            return getIntPref(context, R.string.pref_operation_master_account_pos, 0);
        }

        public static int getAnalyticId(Context context, OperationType type){
            return getIntPref(context, getAnalyticPref(type), 0);
        }

        public static void saveOperationType(Context context, OperationType type){
            saveIntPref(context, R.string.pref_operation_master_operation_type_pos, type.toDbValue());
        }

        public static void saveAccountId(Context context, int accountId){
            saveIntPref(context, R.string.pref_operation_master_account_pos, accountId);
        }

        public static void saveAnalyticId(Context context, int analyticId, OperationType type){
            saveIntPref(context, getAnalyticPref(type), analyticId);
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

    public static class AnalyticChartPrefs{

        public static boolean showInGraphic(Context context){
            return getBooleanPref(context, R.string.pref_analytic_chart_show_in_graphic, true);
        }

        public static boolean showOutGraphic(Context context){
            return getBooleanPref(context, R.string.pref_analytic_chart_show_out_graphic, true);
        }

        public static boolean showInBudgetGraphic(Context context){
            return getBooleanPref(context, R.string.pref_analytic_chart_show_in_budget_graphic, true);
        }

        public static boolean showOutBudgetGraphic(Context context){
            return getBooleanPref(context, R.string.pref_analytic_chart_show_out_budget_graphic, true);
        }

        public static boolean showCashflowGraphic(Context context){
            return getBooleanPref(context, R.string.pref_analytic_chart_show_cashflow_graphic, true);
        }

        public static boolean showDeltaGraphic(Context context){
            return getBooleanPref(context, R.string.pref_analytic_chart_show_delta_graphic, true);
        }

        public static void saveShowInGraphic(Context context, Boolean value){
            saveBooleanPref(context, R.string.pref_analytic_chart_show_in_graphic, value);
        }

        public static void saveShowOutGraphic(Context context, Boolean value){
            saveBooleanPref(context, R.string.pref_analytic_chart_show_out_graphic, value);
        }

        public static void saveShowInBudgetGraphic(Context context, Boolean value){
            saveBooleanPref(context, R.string.pref_analytic_chart_show_in_budget_graphic, value);
        }

        public static void saveShowOutBudgetGraphic(Context context, Boolean value){
            saveBooleanPref(context, R.string.pref_analytic_chart_show_out_budget_graphic, value);
        }

        public static void saveShowCashflowGraphic(Context context, Boolean value){
            saveBooleanPref(context, R.string.pref_analytic_chart_show_cashflow_graphic, value);
        }

        public static void saveShowDeltaGraphic(Context context, Boolean value){
            saveBooleanPref(context, R.string.pref_analytic_chart_show_delta_graphic, value);
        }

    }

    private static boolean getBooleanPref(Context context, int pref, boolean defValue){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getBoolean(context.getString(pref), defValue));
    }

    private static void saveBooleanPref(Context context, int pref, boolean value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(pref), value);
        editor.apply();
    }

    private static int getIntPref(Context context, int pref, int defValue){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getInt(context.getString(pref), defValue));
    }

    private static void saveIntPref(Context context, int pref, int value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context.getString(pref), value);
        editor.apply();
    }

}
