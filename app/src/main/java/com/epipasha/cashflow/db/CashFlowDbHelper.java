package com.epipasha.cashflow.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class CashFlowDbHelper extends SQLiteOpenHelper{

    private static final int VERSION = 3;
    public static final String DB_CASHFLOW = "cashflow.db";
    public static final String BACKUP = "backup.db";

    public static final String TABLE_ACCOUNT = "account";
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_OPERATION = "operation";
    public static final String TABLE_ACCOUNT_BALANCE = "accountBalance";
    public static final String TABLE_GOAL = "goal";
    public static final String TABLE_CURRENCY = "currency";
    public static final String TABLE_CATEGORY_COST = "categoryCost";

    public static final String _ID = "_id";

    public static final String ACCOUNT_NAME = "name";
    public static final String ACCOUNT_CURRENCY_ID = "currencyId";

    public static final String CATEGORY_NAME = "name";
    public static final String CATEGORY_TYPE = "type";
    public static final String CATEGORY_BUDJET = "budjet";

    public static final String OPERATION_DATE = "date";
    public static final String OPERATION_TYPE = "type";
    public static final String OPERATION_ACCOUNT_ID = "accountId";
    public static final String OPERATION_CATEGORY_ID = "categoryId";
    public static final String OPERATION_RECIPIENT_ACCOUNT_ID = "recipientAccountId";
    public static final String OPERATION_SUM = "sum";

    public static final String ACCOUNT_BALANCE_DATE = "date";
    public static final String ACCOUNT_BALANCE_OPERATION_ID = "operationId";
    public static final String ACCOUNT_BALANCE_ACCOUNT_ID = "accountId";
    public static final String ACCOUNT_BALANCE_SUM = "sum";

    public static final String GOAL_NAME = "name";
    public static final String GOAL_SUM = "sum";
    public static final String GOAL_DONE = "done";

    public static final String CURRENCY_NAME = "name";
    public static final String CURRENCY_RATE = "rate";

    public static final String CATEGORY_COST_DATE = "date";
    public static final String CATEGORY_COST_OPERATION_ID = "operationId";
    public static final String CATEGORY_COST_ACCOUNT_ID = "accountId";
    public static final String CATEGORY_COST_CATEGORY_ID = "categoryId";
    public static final String CATEGORY_COST_SUM = "sum";

    private Context context;

    public CashFlowDbHelper(Context context) {
        super(context, DB_CASHFLOW, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_CURRENCY
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CURRENCY_NAME + " TEXT,"
                + CURRENCY_RATE + " REAL);");

        db.execSQL("CREATE TABLE " + TABLE_ACCOUNT
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ACCOUNT_NAME + " TEXT,"
                + ACCOUNT_CURRENCY_ID + " INTEGER);");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORY
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORY_NAME + " TEXT,"
                + CATEGORY_TYPE + " TEXT,"
                + CATEGORY_BUDJET + " INTEGER);");

        db.execSQL("CREATE TABLE " + TABLE_OPERATION
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + OPERATION_DATE + " LONG,"
                + OPERATION_TYPE + " TEXT,"
                + OPERATION_ACCOUNT_ID + " INTEGER,"
                + OPERATION_CATEGORY_ID + " INTEGER,"
                + OPERATION_RECIPIENT_ACCOUNT_ID + " INTEGER,"
                + OPERATION_SUM + " INTEGER);");

        db.execSQL("CREATE TABLE " + TABLE_ACCOUNT_BALANCE
                + " ("
                + ACCOUNT_BALANCE_DATE + " LONG,"
                + ACCOUNT_BALANCE_OPERATION_ID + " INTEGER,"
                + ACCOUNT_BALANCE_ACCOUNT_ID + " INTEGER,"
                + ACCOUNT_BALANCE_SUM + " INTEGER);");

        db.execSQL("CREATE TABLE " + TABLE_GOAL
                + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GOAL_NAME + " TEXT,"
                + GOAL_SUM + " INTEGER,"
                + GOAL_DONE + " BOOLEAN);");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORY_COST
                + " ("
                + CATEGORY_COST_DATE + " LONG,"
                + CATEGORY_COST_OPERATION_ID + " INTEGER,"
                + CATEGORY_COST_ACCOUNT_ID + " INTEGER,"
                + CATEGORY_COST_CATEGORY_ID + " INTEGER,"
                + CATEGORY_COST_SUM + " INTEGER);");
        startLoad(db);
    }

    private static void startLoad(SQLiteDatabase db){
        ContentValues values = new ContentValues();

        values.put(CURRENCY_NAME, "RUB");
        db.insert(TABLE_CURRENCY, null, values);

        values.put(CURRENCY_NAME, "USD");
        db.insert(TABLE_CURRENCY, null, values);

        values.put(CURRENCY_NAME, "EUR");
        db.insert(TABLE_CURRENCY, null, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion<=3){
            db.execSQL("ALTER TABLE "+TABLE_CATEGORY+" ADD COLUMN "+CATEGORY_BUDJET+" INTEGER");
        }
    }

    private void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.epipasha.cashflow"
                        + "//databases//" + DB_CASHFLOW;;
                String backupDBPath ="//data//" + "com.epipasha.cashflow"
                        + "//databases//" + BACKUP;; // From SD directory.
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context.getApplicationContext(), "Import Successful!",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(context.getApplicationContext(), "Import Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    private void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.epipasha.cashflow"
                        + "//databases//" + DB_CASHFLOW;
                String backupDBPath = "//data//" + "com.epipasha.cashflow"
                        + "//databases//" + BACKUP;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context.getApplicationContext(), "Backup Successful!",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(context.getApplicationContext(), "Backup Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }
}
