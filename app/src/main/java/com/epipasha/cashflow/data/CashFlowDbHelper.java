package com.epipasha.cashflow.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.epipasha.cashflow.data.CashFlowContract.AccountBalanceEntry;
import com.epipasha.cashflow.data.CashFlowContract.AccountEntry;
import com.epipasha.cashflow.data.CashFlowContract.CategoryCostEntry;
import com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
import com.epipasha.cashflow.data.CashFlowContract.OperationEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class CashFlowDbHelper extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String DB_CASHFLOW = "cashflow.db";

    public CashFlowDbHelper(Context context) {
        super(context, DB_CASHFLOW, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTables(SQLiteDatabase db){

        final String SQL_CREATE_ACCOUNT_TABLE = "CREATE TABLE " + AccountEntry.TABLE_NAME + " (" +
                AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AccountEntry.COLUMN_TITLE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_ACCOUNT_TABLE);

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CategoryEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                CategoryEntry.COLUMN_TYPE + " INTEGER NOT NULL," +
                CategoryEntry.COLUMN_BUDGET + " INTEGER);";

        db.execSQL(SQL_CREATE_CATEGORY_TABLE);

        final String SQL_CREATE_OPERATION_TABLE = "CREATE TABLE " + OperationEntry.TABLE_NAME + " (" +
                OperationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                OperationEntry.COLUMN_DATE + " INTEGER NOT NULL," +
                OperationEntry.COLUMN_TYPE + " INTEGER NOT NULL," +
                OperationEntry.COLUMN_ACCOUNT_ID + " INTEGER NOT NULL," +
                OperationEntry.COLUMN_CATEGORY_ID + " INTEGER," +
                OperationEntry.COLUMN_RECIPIENT_ACCOUNT_ID + " INTEGER," +
                OperationEntry.COLUMN_SUM + " INTEGER NOT NULL," +

                " FOREIGN KEY (" + OperationEntry.COLUMN_ACCOUNT_ID + ") REFERENCES " +
                AccountEntry.TABLE_NAME + " (" + AccountEntry._ID + "), " +
                " FOREIGN KEY (" + OperationEntry.COLUMN_CATEGORY_ID + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "), " +
                " FOREIGN KEY (" + OperationEntry.COLUMN_RECIPIENT_ACCOUNT_ID + ") REFERENCES " +
                AccountEntry.TABLE_NAME + " (" + AccountEntry._ID + "));";

        db.execSQL(SQL_CREATE_OPERATION_TABLE);

        final String SQL_CREATE_ACCOUNT_BALANCE_TABLE = "CREATE TABLE " + AccountBalanceEntry.TABLE_NAME + " (" +
                AccountBalanceEntry.COLUMN_DATE + " INTEGER NOT NULL," +
                AccountBalanceEntry.COLUMN_OPERATION_ID + " INTEGER NOT NULL," +
                AccountBalanceEntry.COLUMN_ACCOUNT_ID + " INTEGER NOT NULL," +
                AccountBalanceEntry.COLUMN_SUM + " INTEGER NOT NULL," +

                " FOREIGN KEY (" + AccountBalanceEntry.COLUMN_OPERATION_ID + ") REFERENCES " +
                OperationEntry.TABLE_NAME + " (" + OperationEntry._ID + "), " +
                " FOREIGN KEY (" + AccountBalanceEntry.COLUMN_ACCOUNT_ID + ") REFERENCES " +
                AccountEntry.TABLE_NAME + " (" + AccountEntry._ID + "));";

        db.execSQL(SQL_CREATE_ACCOUNT_BALANCE_TABLE);

        final String SQL_CREATE_CATEGORY_COST_TABLE = "CREATE TABLE " + CategoryCostEntry.TABLE_NAME + " (" +
                CategoryCostEntry.COLUMN_DATE + " INTEGER NOT NULL," +
                CategoryCostEntry.COLUMN_OPERATION_ID + " INTEGER NOT NULL," +
                CategoryCostEntry.COLUMN_ACCOUNT_ID + " INTEGER NOT NULL," +
                CategoryCostEntry.COLUMN_CATEGORY_ID + " INTEGER NOT NULL," +
                CategoryCostEntry.COLUMN_SUM + " INTEGER NOT NULL," +

                " FOREIGN KEY (" + CategoryCostEntry.COLUMN_OPERATION_ID + ") REFERENCES " +
                OperationEntry.TABLE_NAME + " (" + OperationEntry._ID + "), " +
                " FOREIGN KEY (" + CategoryCostEntry.COLUMN_ACCOUNT_ID + ") REFERENCES " +
                AccountEntry.TABLE_NAME + " (" + AccountEntry._ID + "), " +
                " FOREIGN KEY (" + CategoryCostEntry.COLUMN_CATEGORY_ID + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "));";

        db.execSQL(SQL_CREATE_CATEGORY_COST_TABLE);
    }

    public void dropTables(SQLiteDatabase db){
        String[] tables = new String[]{
                AccountEntry.TABLE_NAME,
                CategoryEntry.TABLE_NAME,
                OperationEntry.TABLE_NAME,
                AccountBalanceEntry.TABLE_NAME,
                CategoryCostEntry.TABLE_NAME};

        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            db.execSQL(dropQuery);
        }
    }



    public static String backupDb(Context context) throws JSONException {

        JSONObject backup = new JSONObject();

        backup.put(AccountEntry.TABLE_NAME, backupTable(context, AccountEntry.CONTENT_URI));
        backup.put(CategoryEntry.TABLE_NAME, backupTable(context, CategoryEntry.CONTENT_URI));
        backup.put(OperationEntry.TABLE_NAME, backupTable(context, OperationEntry.CONTENT_URI));

        return backup.toString();
    }

    private static JSONArray backupTable(Context context, Uri uri){
        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            Log.d("TAG_NAME", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();

        return resultSet;
    }

    public static void restoreDb(Context context, String data) throws JSONException {

        JSONObject obj = new JSONObject(data);

        Iterator<String> keys = obj.keys();
        while(keys.hasNext()){
            String key = keys.next();

            switch (key){
                case AccountEntry.TABLE_NAME:{
                    restoreTable(context, obj.getJSONArray(key), AccountEntry.CONTENT_URI);
                    break;
                }
                case CategoryEntry.TABLE_NAME:{
                    restoreTable(context, obj.getJSONArray(key), CategoryEntry.CONTENT_URI);
                    break;
                }
                case OperationEntry.TABLE_NAME:{
                    restoreTable(context, obj.getJSONArray(key), OperationEntry.CONTENT_URI);
                    break;
                }
            }
        }
    }

    private static void restoreTable(Context context, JSONArray rows, Uri uri) throws JSONException {

        ContentResolver resolver = context.getContentResolver();

        resolver.delete(uri, null, null);

        ContentValues[] values = new ContentValues[rows.length()];

        for (int i = 0; i < rows.length(); i++) {
            JSONObject row = rows.getJSONObject(i);

            ContentValues value = new ContentValues();

            Iterator<String> iterator = row.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                try {
                    value.put(key, Integer.parseInt((String) row.get(key)));
                } catch (Exception e) {
                    value.put(key, (String) row.get(key));
                }
            }

            values[i] = value;

        }

        int insertedRows = resolver.bulkInsert(uri, values);
        if(insertedRows != rows.length()){
            //TODO
        }
    }

}
