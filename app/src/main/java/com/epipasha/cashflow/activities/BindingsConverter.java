package com.epipasha.cashflow.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.databinding.InverseMethod;

public class BindingsConverter {
    @InverseMethod("convertIntToString")
    public static int convertStringToInt(String value) {
        if(value.isEmpty()) return 0;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String convertIntToString(int value) {
        if(value == 0){
            return "";
        }
        return String.valueOf(value);
    }

    public static String convertDateToString(Date date){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return format.format(date);
    }

    public static String convertTimeToString(Date date){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(date);
    }
}
