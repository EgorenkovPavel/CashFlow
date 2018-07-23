package com.epipasha.cashflow.data;

import static com.epipasha.cashflow.data.CashFlowContract.*;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

public class Backuper {

    public static String backupDb(Context context) throws JSONException {

        JSONObject backup = new JSONObject();

        CashFlowDbHelper dbHelper = new CashFlowDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        backup.put(AccountEntry.TABLE_NAME, backupTable(db, AccountEntry.TABLE_NAME));
        backup.put(CategoryEntry.TABLE_NAME, backupTable(db, CategoryEntry.TABLE_NAME));
        backup.put(OperationEntry.TABLE_NAME, backupTable(db, OperationEntry.TABLE_NAME));

        return backup.toString();
    }

    private static JSONArray backupTable(SQLiteDatabase db, String tableName){
        Cursor cursor = db.query(tableName,
                null,
                null,
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

        CashFlowDbHelper dbHelper = new CashFlowDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

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


     public static String backupRoomDb(Context context){

        AppDatabase mDb = AppDatabase.getInstance(context.getApplicationContext());

        List<Account> accounts = mDb.backupDao().loadAllAccounts();
        List<Category> categories = mDb.backupDao().loadAllCategories();
        List<Operation> operations = mDb.backupDao().loadAllOperations();

        BackupStructure backup = new BackupStructure(accounts, categories, operations);

        return toJson(backup);
    }

    public static void restoreRoomDb(Context context, String data){

        AppDatabase mDb = AppDatabase.getInstance(context.getApplicationContext());

        BackupStructure backup = fromJson(data);

        List<Account> accounts = backup.getAccounts();
        List<Category> categories = backup.getCategories();
        List<Operation> operations = backup.getOperations();

        mDb.beginTransaction();

        mDb.accountDao().deleteAll();
        mDb.categoryDao().deleteAll();
        mDb.operationDao().deleteAll();

        mDb.accountDao().insertAccounts(accounts);
        mDb.categoryDao().insertCategories(categories);
        for (Operation operation:operations) {
            mDb.operationDao().insertOperationWihtAnalytic(operation);
        }

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }

    private static String toJson(final BackupStructure obj){
        Gson gson = new Gson();
        Type type = new TypeToken<BackupStructure>(){}.getType();
        return gson.toJson(obj, type);
    }

    private static BackupStructure fromJson(String text){
        Gson gson = new Gson();
        Type type = new TypeToken<BackupStructure>() {}.getType();

        return gson.fromJson(text, type);
    }

    private static class BackupStructure{
        private List<Account> mAccounts;
        private List<Category> mCategories;
        private List<Operation> mOperations;

        public BackupStructure(List<Account> accounts, List<Category> categories, List<Operation> operations) {
            mAccounts = accounts;
            mCategories = categories;
            mOperations = operations;
        }

        public List<Account> getAccounts() {
            return mAccounts;
        }

        public List<Category> getCategories() {
            return mCategories;
        }

        public List<Operation> getOperations() {
            return mOperations;
        }
    }
}
