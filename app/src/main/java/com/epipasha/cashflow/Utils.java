package com.epipasha.cashflow;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.widget.Spinner;

public class Utils {

    public static void setPositionById(Spinner spinner, int rowId){
        for (int i = 0; i < spinner.getCount(); i++) {
            Cursor value = (Cursor) spinner.getItemAtPosition(i);
            long id = value.getLong(value.getColumnIndex("_id"));
            if (id == rowId) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public static int getSelectedId(Spinner spinner){
        Cursor cursor = (Cursor) spinner.getSelectedItem();
        return cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
    }
}
