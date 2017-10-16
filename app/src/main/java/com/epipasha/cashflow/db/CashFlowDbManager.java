package com.epipasha.cashflow.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epipasha.cashflow.db.CashFlowContract.AccountBalanceEntry;
import com.epipasha.cashflow.db.CashFlowContract.AccountEntry;
import com.epipasha.cashflow.db.CashFlowContract.CategoryCostEntry;
import com.epipasha.cashflow.db.CashFlowContract.CategoryEntry;
import com.epipasha.cashflow.db.CashFlowContract.OperationEntry;
import com.epipasha.cashflow.objects.Account;
import com.epipasha.cashflow.objects.Category;
import com.epipasha.cashflow.objects.Operation;
import com.epipasha.cashflow.objects.OperationType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CashFlowDbManager {

    private static CashFlowDbManager manager;

    private SQLiteDatabase db;
    private final CashFlowDbHelper dbHelper;

    public static CashFlowDbManager getInstance(Context context){
        if (manager == null){
            manager = new CashFlowDbManager(context);
        }
        return manager;
    }

    private CashFlowDbManager(Context context){
        dbHelper = new CashFlowDbHelper(context);
    }

    private void openToWrite(){
        db = dbHelper.getWritableDatabase();
    }

    private void close(){
        db.close();
    }



    public ArrayList<Account> getAccounts(){

        openToWrite();
        String sqlQuery =
                "SELECT " +
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
        Cursor cursor = db.rawQuery(sqlQuery, null);

        ArrayList<Account> list = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                Account account = new Account();
                account.setID(cursor.getInt(0));
                account.setName(cursor.getString(1));
                account.setBalance(cursor.getInt(2));
                list.add(account);
            }
            while (cursor.moveToNext());
        }
        assert cursor != null;
        cursor.close();
        close();

        return list;
    }

    public int addAccount(Account account){
        openToWrite();
        ContentValues values = new ContentValues(2);

        values.put(AccountEntry.COLUMN_TITLE, account.getName());

        int res = (int)db.insertOrThrow(AccountEntry.TABLE_NAME, null, values);
        close();
        return res;
    }

    public void updateAccount(int id, ContentValues values){
        openToWrite();

        String where = String.format("%s=%d", AccountEntry._ID, id);

        db.update(AccountEntry.TABLE_NAME, values, where, null);
        close();
    }

    public void updateAccount(Account account){
        openToWrite();
        ContentValues values = new ContentValues(2);

        values.put(AccountEntry.COLUMN_TITLE, account.getName());

        @SuppressLint("DefaultLocale")
        String where = String.format("%s=%d", AccountEntry._ID, account.getID());

        db.update(AccountEntry.TABLE_NAME, values, where, null);
        close();
    }

    public void deleteAccount(Account account){
        openToWrite();

        @SuppressLint("DefaultLocale")
        String where = String.format("%s=%d", AccountEntry._ID, account.getID());

        db.delete(AccountEntry.TABLE_NAME, where, null);
        close();
    }


    public ArrayList<Category> getCategories(){
        openToWrite();
        Cursor cursor = db.query(CategoryEntry.TABLE_NAME, null, null, null, null, null, CategoryEntry.COLUMN_TITLE);
        ArrayList<Category> list = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                Category category = new Category();
                category.setID(cursor.getInt(0));
                category.setName(cursor.getString(1));
                category.setType(OperationType.toEnum(cursor.getString(2)));
                category.setBudget(cursor.getInt(3));
                list.add(category);
            }
            while (cursor.moveToNext());
        }
        assert cursor != null;
        cursor.close();
        close();
        return list;
    }

    public ArrayList<Category> getCategories(OperationType type){
        openToWrite();
        String where = String.format("%s=\"%s\"",CategoryEntry.COLUMN_TYPE, type.toString());
        Cursor cursor = db.query(CategoryEntry.TABLE_NAME, null, where, null, null, null, CategoryEntry.COLUMN_TITLE);
        ArrayList<Category> list = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                Category category = new Category();
                category.setID(cursor.getInt(0));
                category.setName(cursor.getString(1));
                category.setType(OperationType.toEnum(cursor.getString(2)));
                category.setBudget(cursor.getInt(3));
                list.add(category);
            }
            while (cursor.moveToNext());
        }
        assert cursor != null;
        cursor.close();
        close();
        return list;
    }

    public int getTotalSum(OperationType type, Date start, Date end){
        openToWrite();
        String sqlQuery =
                "SELECT " +
                "SUM(" + CategoryCostEntry.COLUMN_SUM + ") " +
                "FROM " + CategoryCostEntry.TABLE_NAME + " " +
                "INNER JOIN " +
                CategoryEntry.TABLE_NAME + " " +
                "ON " + CategoryCostEntry.TABLE_NAME + "." + CategoryCostEntry.COLUMN_CATEGORY_ID +
                    " = " + CategoryEntry.TABLE_NAME + "." + CategoryEntry._ID + " " +
                "WHERE " + CategoryCostEntry.TABLE_NAME + "." + CategoryCostEntry.COLUMN_DATE +
                        " >= " + start.getTime() + " " +
                "AND " + CategoryCostEntry.TABLE_NAME + "." + CategoryCostEntry.COLUMN_DATE +
                        " <= " + end.getTime() + " " +
                "AND " + CategoryEntry.TABLE_NAME + "." + CategoryEntry.COLUMN_TYPE + " = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{type.toString()});

        int result = 0;
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        assert cursor != null;
        cursor.close();
        close();

        return result;

    }

    public int getTotalBudget(OperationType type) {
        openToWrite();

        String[] columns = new String[] {"SUM("+CategoryEntry.COLUMN_BUDGET +")"};
        String where = CategoryEntry.COLUMN_TYPE + " = ?";
        String[] whereArgs = new String[]{type.toString()};

        Cursor cursor = db.query(CategoryEntry.TABLE_NAME, columns, where, whereArgs, null, null, null);

        int result = 0;

        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        assert cursor != null;
        cursor.close();
        close();
        return result;

    }

    public int getCategorySum(Category category, Date start, Date end){
        openToWrite();
        String sqlQuery = "SELECT " +
                "SUM(" + CategoryCostEntry.TABLE_NAME  + "." + CategoryCostEntry.COLUMN_SUM + ")" +
                "FROM " + CategoryCostEntry.TABLE_NAME + " " +
                "INNER JOIN " +
                CategoryEntry.TABLE_NAME + " " +
                "ON " + CategoryCostEntry.TABLE_NAME + "." + CategoryCostEntry.COLUMN_CATEGORY_ID +
                " = " + CategoryEntry.TABLE_NAME + "." + CategoryEntry._ID + " " +
                "WHERE " + CategoryCostEntry.TABLE_NAME + "." + CategoryCostEntry.COLUMN_DATE +
                " >= " + start.getTime() + " " +
                "AND " + CategoryCostEntry.TABLE_NAME + "." + CategoryCostEntry.COLUMN_DATE +
                "<= " + end.getTime() + " " +
                "AND " + CategoryEntry.TABLE_NAME + "." + CategoryEntry._ID + " = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{String.valueOf(category.getID())});

        int result = 0;
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        assert cursor != null;
        cursor.close();
        close();

        return result;

    }

    public int getCategoryBudget(Category category) {
        openToWrite();

        String[] columns = new String[] {"SUM("+CategoryEntry.COLUMN_BUDGET +")"};
        String where = CategoryEntry._ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(category.getID())};

        Cursor cursor = db.query(CategoryEntry.TABLE_NAME, columns, where, whereArgs, null, null, null);

        int result = 0;

        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        assert cursor != null;
        cursor.close();
        close();
        return result;

    }


    private ContentValues valuesCategory(Category category){
        ContentValues values = new ContentValues(3);

        values.put(CategoryEntry.COLUMN_TITLE, category.getName());
        values.put(CategoryEntry.COLUMN_TYPE, category.getType().toString());
        values.put(CategoryEntry.COLUMN_BUDGET, category.getBudget());

        return values;
    }

    public int addCategory(Category category){
        openToWrite();
        int res = (int)db.insertOrThrow(CategoryEntry.TABLE_NAME, null, valuesCategory(category));
        close();

        return res;
    }

    public void updateCategory(Category category){
        openToWrite();

        @SuppressLint("DefaultLocale")
        String where = String.format("%s=%d",CategoryEntry._ID, category.getID());

        db.update(CategoryEntry.TABLE_NAME, valuesCategory(category), where, null);
        close();

    }

    public void deleteCategory(Category category){
        openToWrite();

        @SuppressLint("DefaultLocale")
        String where = String.format("%s=%d",CategoryEntry._ID, category.getID());

        db.delete(CategoryEntry.TABLE_NAME, where, null);
        close();

    }


    public ArrayList<Operation> getOperations(){

        ArrayList<Account> accounts = getAccounts();
        HashMap<Integer, Account> mapAccount = new HashMap<>();
        for (Account account:accounts) {
            mapAccount.put(account.getID(), account);
        }

        ArrayList<Category> categories = getCategories();
        HashMap<Integer, Category> mapCategory = new HashMap<>();
        for (Category category:categories) {
            mapCategory.put(category.getID(), category);
        }

        openToWrite();
        Cursor cursor = db.query(OperationEntry.TABLE_NAME, null, null, null, null, null, OperationEntry.COLUMN_DATE + " DESC");
        ArrayList<Operation> list = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                Operation operation = new Operation();
                operation.setID(cursor.getInt(0));
                operation.setDate(new Date(cursor.getLong(1)));
                operation.setType(OperationType.toEnum(cursor.getString(2)));
                operation.setAccount(mapAccount.get(cursor.getInt(3)));
                operation.setCategory(mapCategory.get(cursor.getInt(4)));
                operation.setRecipientAccount(mapAccount.get(cursor.getInt(5)));
                operation.setSum(cursor.getInt(6));

                list.add(operation);
            }
            while (cursor.moveToNext());
        }

        assert cursor != null;
        cursor.close();
        close();
        return list;
    }

    public int addOperation(Operation operation){
        openToWrite();

        int res = -1;

        db.beginTransaction();
        try {
            ContentValues values = OperationValues(operation);
            res = (int)db.insertOrThrow(OperationEntry.TABLE_NAME, null, values);
            operation.setID(res);

            addBalance(operation);
            addCategoryCost(operation);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        close();
        return res;
    }

    private ContentValues OperationValues(Operation operation){
        ContentValues values = new ContentValues(6);

        int accountId = operation.getAccount()==null ? -1 : operation.getAccount().getID();
        int categoryId = operation.getCategory()==null ? -1 : operation.getCategory().getID();
        int recipientAccountId = operation.getRecipientAccount()==null ? -1 : operation.getRecipientAccount().getID();

        values.put(OperationEntry.COLUMN_DATE, operation.getDate().getTime());
        values.put(OperationEntry.COLUMN_TYPE, operation.getType().toString());
        values.put(OperationEntry.COLUMN_ACCOUNT_ID, accountId);
        values.put(OperationEntry.COLUMN_CATEGORY_ID, categoryId);
        values.put(OperationEntry.COLUMN_RECIPIENT_ACCOUNT_ID, recipientAccountId);
        values.put(OperationEntry.COLUMN_SUM, operation.getSum());

        return values;
    }

    public void updateOperation(Operation operation){
        openToWrite();

        db.beginTransaction();
        try {
            ContentValues values = OperationValues(operation);

            @SuppressLint("DefaultLocale")
            String where = String.format("%s=%d", OperationEntry._ID, operation.getID());

            db.update(OperationEntry.TABLE_NAME, values, where, null);

            deleteBalance(operation);
            addBalance(operation);

            deleteCategoryCost(operation);
            addCategoryCost(operation);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        close();
    }

    public void deleteOperation(Operation operation){
        openToWrite();

        db.beginTransaction();
        try {

            String where = String.format(Locale.getDefault(),"%s=%d",OperationEntry._ID, operation.getID());

            db.delete(OperationEntry.TABLE_NAME, where, null);

            deleteBalance(operation);
            deleteCategoryCost(operation);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        close();
    }

    private void addBalance(Operation operation){
        int accountId = operation.getAccount()==null ? -1 : operation.getAccount().getID();
        int recipientAccountId = operation.getRecipientAccount()==null ? -1 : operation.getRecipientAccount().getID();

        switch (operation.getType()){
            case IN:
                ContentValues balanceValuesIn = new ContentValues(4);
                balanceValuesIn.put(AccountBalanceEntry.COLUMN_DATE, operation.getDate().getTime());
                balanceValuesIn.put(AccountBalanceEntry.COLUMN_OPERATION_ID, operation.getID());
                balanceValuesIn.put(AccountBalanceEntry.COLUMN_ACCOUNT_ID, accountId);
                balanceValuesIn.put(AccountBalanceEntry.COLUMN_SUM, operation.getSum());

                db.insertOrThrow(AccountBalanceEntry.TABLE_NAME, null, balanceValuesIn);
                break;
            case OUT:
                ContentValues balanceValuesOut = new ContentValues(4);
                balanceValuesOut.put(AccountBalanceEntry.COLUMN_DATE, operation.getDate().getTime());
                balanceValuesOut.put(AccountBalanceEntry.COLUMN_OPERATION_ID, operation.getID());
                balanceValuesOut.put(AccountBalanceEntry.COLUMN_ACCOUNT_ID, accountId);
                balanceValuesOut.put(AccountBalanceEntry.COLUMN_SUM, -1*operation.getSum());

                db.insertOrThrow(AccountBalanceEntry.TABLE_NAME, null, balanceValuesOut);
                break;
            case TRANSFER:
                ContentValues balanceValuesT1 = new ContentValues(4);
                balanceValuesT1.put(AccountBalanceEntry.COLUMN_DATE, operation.getDate().getTime());
                balanceValuesT1.put(AccountBalanceEntry.COLUMN_OPERATION_ID, operation.getID());
                balanceValuesT1.put(AccountBalanceEntry.COLUMN_ACCOUNT_ID, accountId);
                balanceValuesT1.put(AccountBalanceEntry.COLUMN_SUM, -1*operation.getSum());

                db.insertOrThrow(AccountBalanceEntry.TABLE_NAME, null, balanceValuesT1);

                ContentValues balanceValuesT2 = new ContentValues(4);
                balanceValuesT2.put(AccountBalanceEntry.COLUMN_DATE, operation.getDate().getTime());
                balanceValuesT2.put(AccountBalanceEntry.COLUMN_OPERATION_ID, operation.getID());
                balanceValuesT2.put(AccountBalanceEntry.COLUMN_ACCOUNT_ID, recipientAccountId);
                balanceValuesT2.put(AccountBalanceEntry.COLUMN_SUM, operation.getSum());

                db.insertOrThrow(AccountBalanceEntry.TABLE_NAME, null, balanceValuesT2);
                break;
        }

    }

    private void deleteBalance(Operation operation){

        @SuppressLint("DefaultLocale")
        String where = String.format("%s=%d",AccountBalanceEntry.COLUMN_OPERATION_ID, operation.getID());

        db.delete(AccountBalanceEntry.TABLE_NAME, where, null);
    }


    private void addCategoryCost(Operation operation){
        if ((operation.getType().equals(OperationType.IN))||(operation.getType().equals(OperationType.OUT))){
            ContentValues values = new ContentValues(5);
            values.put(CategoryCostEntry.COLUMN_DATE, operation.getDate().getTime());
            values.put(CategoryCostEntry.COLUMN_OPERATION_ID, operation.getID());
            values.put(CategoryCostEntry.COLUMN_ACCOUNT_ID, operation.getAccount().getID());
            values.put(CategoryCostEntry.COLUMN_CATEGORY_ID, operation.getCategory().getID());
            values.put(CategoryCostEntry.COLUMN_SUM, operation.getSum());

            db.insertOrThrow(CategoryCostEntry.TABLE_NAME, null, values);
        }
    }

    private void deleteCategoryCost(Operation operation){

        @SuppressLint("DefaultLocale")
        String where = String.format("%s=%d",CategoryCostEntry.COLUMN_OPERATION_ID, operation.getID());

        db.delete(CategoryCostEntry.TABLE_NAME, where, null);
    }
}
