package com.epipasha.cashflow.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.epipasha.cashflow.data.CashFlowContract.AccountBalanceEntry;
import com.epipasha.cashflow.data.CashFlowContract.AccountEntry;
import com.epipasha.cashflow.data.CashFlowContract.CategoryCostEntry;
import com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
import com.epipasha.cashflow.data.CashFlowContract.OperationEntry;

class CashFlowDbHelper extends SQLiteOpenHelper{

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

}
