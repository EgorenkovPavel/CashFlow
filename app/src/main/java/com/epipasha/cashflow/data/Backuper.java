package com.epipasha.cashflow.data;

import android.content.Context;
import android.util.Log;

import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.objects.OperationType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Backuper {

     public static String backupRoomDb(Context context){

        AppDatabase mDb = AppDatabase.getInstance(context.getApplicationContext());

        List<Account> accounts = mDb.backupDao().loadAllAccounts();
        List<Category> categories = mDb.backupDao().loadAllCategories();
        List<Operation> operations = mDb.backupDao().loadAllOperations();

        BackupStructure backup = new BackupStructure(accounts, categories, operations);

        return toJson(backup);
    }

    public static void restoreOldRoomDb(Context context, String data){

        AppDatabase mDb = AppDatabase.getInstance(context.getApplicationContext());

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String,String>[]>>() {}.getType();

        Map<String, Map<String,String>[]> backup = gson.fromJson(data, type);

        List<Account> mAccounts = new ArrayList<>();
        List<Category> mCategories = new ArrayList<>();
        List<Operation> mOperations = new ArrayList<>();

        for(Map<String, String> accountData: backup.get("account")){
            Account account = new Account();
            account.setId(Integer.valueOf(accountData.get("_id")));
            account.setTitle(accountData.get("account_title"));
            mAccounts.add(account);
        }

        for(Map<String, String> categoryData: backup.get("category")){

            Category category = new Category(
                    Integer.valueOf(categoryData.get("_id")),
                    categoryData.get("category_title"),
                    OperationType.toEnum(Integer.valueOf(categoryData.get("category_type"))),
                    categoryData.get("category_budget").isEmpty() ? 0 : Integer.valueOf(categoryData.get("category_budget")));

            mCategories.add(category);
        }

        for(Map<String, String> operationData: backup.get("operation")){

            String category = operationData.get("operation_category_id");
            Integer categoryId = null;
            if(!category.isEmpty() && !category.equals("-1"))
                categoryId = Integer.valueOf(category);

            String repAccount = operationData.get("operation_recipient_account_id");
            Integer repAccountId = null;
            if(!repAccount.isEmpty() && !repAccount.equals("-1"))
                repAccountId = Integer.valueOf(repAccount);

            Operation operation = new Operation(
                    Integer.valueOf(operationData.get("_id")),
                    new Date(Long.valueOf(operationData.get("operation_date"))),
                    OperationType.toEnum(Integer.valueOf(operationData.get("operation_type"))),
                    Integer.valueOf(operationData.get("operation_account_id")),
                    categoryId,
                    repAccountId,
                    Integer.valueOf(operationData.get("operation_sum")));
            mOperations.add(operation);
        }

        mDb.beginTransaction();

        mDb.accountDao().deleteAll();
        mDb.categoryDao().deleteAll();
        mDb.operationDao().deleteAll();

        mDb.accountDao().insertAccounts(mAccounts);
        mDb.categoryDao().insertCategories(mCategories);
        for (Operation operation:mOperations) {
            mDb.operationDao().insertOperationWithAnalytic(operation);
        }

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
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
            mDb.operationDao().insertOperationWithAnalytic(operation);
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
