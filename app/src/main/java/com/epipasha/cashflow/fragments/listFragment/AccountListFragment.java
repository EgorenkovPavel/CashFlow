package com.epipasha.cashflow.fragments.listFragment;

import static com.epipasha.cashflow.db.CashFlowContract.*;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.db.CashFlowDbHelper;
import com.epipasha.cashflow.db.CashFlowDbManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AccountListFragment extends AbstractListFragment {

    @Override
    int getLayout() {
        return R.layout.list_item_account;
    }

    @Override
    HashMap<Integer, View> findViewHolder(View v) {

        HashMap<Integer, View> views = new HashMap<>();
        views.put(R.id.account_list_item_name, v.findViewById(R.id.account_list_item_name));
        views.put(R.id.account_list_item_sum, v.findViewById(R.id.account_list_item_sum));

        return views;
    }

    @Override
    int getIdFromCursor(Cursor cursor) {
         return cursor.getInt(0);
    }

    @Override
    void loadDataFromCursor(HashMap<Integer, View> views, Cursor cursor) {

        for(Map.Entry<Integer, View> entry : views.entrySet()) {
            Integer key = entry.getKey();
            View v = entry.getValue();

            switch (key){
                case R.id.account_list_item_name: {
                    ((TextView) v).setText(cursor.getString(1));
                    break;
                }
                case R.id.account_list_item_sum:{
                    ((TextView) v).setText(String.format(Locale.getDefault(),"%,d",cursor.getInt(2)));
                    break;
                }
            }

        }
   }

    @Override
    String getQuery() {
        return "SELECT " +
                        AccountEntry.TABLE_NAME + "." + AccountEntry._ID + ", " +
                        AccountEntry.TABLE_NAME + "." + AccountEntry.COLUMN_TITLE + ", " +
                        AccountBalanceEntry.TABLE_NAME + ".sum " +
                        "FROM " + AccountEntry.TABLE_NAME + " " +
                        "LEFT OUTER JOIN " +
                        "(SELECT " +
                        AccountBalanceEntry.TABLE_NAME + "." + AccountBalanceEntry.COLUMN_ACCOUNT_ID + ", " +
                        "SUM(" + AccountBalanceEntry.TABLE_NAME + "." + AccountBalanceEntry.COLUMN_SUM  + ") as sum " +
                        "FROM " + AccountBalanceEntry.TABLE_NAME + " " +
                        "GROUP BY " + AccountBalanceEntry.TABLE_NAME + "." + AccountBalanceEntry.COLUMN_ACCOUNT_ID + ") " +
                        "as " + AccountBalanceEntry.TABLE_NAME + " " +
                        "ON " + AccountEntry.TABLE_NAME + "." + AccountEntry._ID + " = " +
                        AccountBalanceEntry.TABLE_NAME + "." + AccountBalanceEntry.COLUMN_ACCOUNT_ID + " " +
                        "ORDER BY " + AccountEntry.TABLE_NAME + "." + AccountEntry.COLUMN_TITLE +";";
    }

    @Override
    public void openDialog(int id) {



    }

}
