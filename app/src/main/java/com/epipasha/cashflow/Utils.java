package com.epipasha.cashflow;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class Utils {

    public static final int EMPTY_ID = -1;

    public static String formatNumber(String number){
        return String.format(Locale.getDefault(),"%,d", getLong(number));
    }

    public static String formatNumber(int number){
        return String.format(Locale.getDefault(),"%,d", number);
    }

    public static long getLong(String in){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        String str = in.replaceAll(String.valueOf(symbols.getGroupingSeparator()), "");

        if(str.equals("")){
            return 0L;
        }else{
            return Long.parseLong(str);
        }
    }

}
