package com.epipasha.cashflow.data;

import android.content.Context;

import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Backuper {

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
