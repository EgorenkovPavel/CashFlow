package com.epipasha.cashflow.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epipasha.cashflow.objects.Account;
import com.epipasha.cashflow.objects.Category;
import com.epipasha.cashflow.objects.Currency;
import com.epipasha.cashflow.objects.DBObject;
import com.epipasha.cashflow.objects.Goal;
import com.epipasha.cashflow.objects.Operation;
import com.epipasha.cashflow.objects.OperationType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pavel on 09.10.2016.
 */
public class CashFlowDbManager {

    private static CashFlowDbManager manager;

    private SQLiteDatabase db;
    private final Context context;
    private CashFlowDbHelper dbHelper;
    private String[] columns = new String[]{CashFlowDbHelper.ACCOUNT_NAME};

    public static CashFlowDbManager getInstance(Context context){
        if (manager == null){
            manager = new CashFlowDbManager(context);
        }
        return manager;
    }

    public CashFlowDbManager(Context context){
        this.context = context;
        dbHelper = new CashFlowDbHelper(context);
    }

    public void openToWrite(){
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        db.close();
    }



    public int add(DBObject object){
        openToWrite();

        ContentValues values = object.getValues();
        String tableName = object.getTableName();

        int res = (int)db.insertOrThrow(tableName, null, values);
        close();

        return res;
    }

    public int update(DBObject object){
        openToWrite();

        ContentValues values = object.getValues();
        String tableName = object.getTableName();
        String where = String.format("%s=%d",CashFlowDbHelper._ID, object.getID());

        int res = db.update(tableName, values, where, null);
        close();
        return res;
    }

    public int delete(DBObject object){
        openToWrite();

        String tableName = object.getTableName();
        String where = String.format("%s=%d",CashFlowDbHelper._ID, object.getID());

        int res = db.delete(tableName, where, null);
        close();
        return res;
    }




    public ArrayList<Account> getAccounts(){

        ArrayList<Currency> curs = getCurrencies();
        HashMap<Integer, Currency> mapCur = new HashMap<>();
        for (Currency cur:curs) {
            mapCur.put(cur.getId(), cur);
        }

        openToWrite();
        String sqlQuery = "SELECT "
                + "PL._id as id, "
                + "PL.name as name, "
                + "PL.currencyId as currencyId, "
                + "PS.sum as sum "
                + "FROM account as PL "
                + "LEFT OUTER JOIN "
                + "(SELECT "
                +   "balance.accountId as accountId, "
                +   "SUM(balance.sum) as sum "
                +   "FROM accountBalance as balance "
                +   "GROUP BY balance.accountId) as PS "
                + "ON PL._id = PS.accountId "
                + "ORDER BY PL.Name";
        Cursor cursor = db.rawQuery(sqlQuery, null);

        ArrayList<Account> list = new ArrayList<Account>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                Account account = new Account();
                account.setID(cursor.getInt(0));
                account.setName(cursor.getString(1));
                account.setCurrency(mapCur.get(cursor.getInt(2)));
                account.setBalance(cursor.getInt(3));
                list.add(account);
            }
            while (cursor.moveToNext());
        }

        close();

        return list;
    }

    public Account getAccount(int id){

        ArrayList<Currency> curs = getCurrencies();
        HashMap<Integer, Currency> mapCur = new HashMap<>();
        for (Currency cur:curs) {
            mapCur.put(cur.getId(), cur);
        }

        openToWrite();
        String sqlQuery = "SELECT "
                + "PL._id as id, "
                + "PL.name as name, "
                + "PL.currencyId as currencyId, "
                + "PS.sum as sum "
                + "FROM account as PL "
                + "LEFT OUTER JOIN "
                + "(SELECT "
                +   "balance.accountId as accountId, "
                +   "SUM(balance.sum) as sum "
                +   "FROM accountBalance as balance "
                +   "GROUP BY balance.accountId) as PS "
                + "ON PL._id = PS.accountId "
                + "WHERE PL._id = " + id
                + " ORDER BY PL.Name";
        Cursor cursor = db.rawQuery(sqlQuery, null);

        Account account = new Account();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                account = new Account();
                account.setID(cursor.getInt(0));
                account.setName(cursor.getString(1));
                account.setCurrency(mapCur.get(cursor.getInt(2)));
                account.setBalance(cursor.getInt(3));
            }
            while (cursor.moveToNext());
        }

        close();

        return account;
    }

    public int addAccount(Account account){
        openToWrite();
        ContentValues values = new ContentValues(2);

        values.put(CashFlowDbHelper.ACCOUNT_NAME, account.getName());
        values.put(CashFlowDbHelper.ACCOUNT_CURRENCY_ID, account.getCurrency().getId());

        int res = (int)db.insertOrThrow(CashFlowDbHelper.TABLE_ACCOUNT, null, values);
        close();
        return res;
    }

    public int updateAccount(Account account){
        openToWrite();
        ContentValues values = new ContentValues(2);

        values.put(CashFlowDbHelper.ACCOUNT_NAME, account.getName());
        values.put(CashFlowDbHelper.ACCOUNT_CURRENCY_ID, account.getCurrency().getId());

        String where = String.format("%s=%d",CashFlowDbHelper._ID, account.getID());

        int res = db.update(CashFlowDbHelper.TABLE_ACCOUNT, values, where, null);
        close();
        return res;
    }

    public int deleteAccount(Account account){
        openToWrite();

        String where = String.format("%s=%d",CashFlowDbHelper._ID, account.getID());

        int res = db.delete(CashFlowDbHelper.TABLE_ACCOUNT, where, null);
        close();
        return res;
    }


    public ArrayList<Category> getCategories(){
        openToWrite();
        Cursor cursor = db.query(CashFlowDbHelper.TABLE_CATEGORY, null, null, null, null, null, CashFlowDbHelper.CATEGORY_NAME);
        ArrayList<Category> list = new ArrayList<Category>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                Category category = new Category();
                category.setID(cursor.getInt(0));
                category.setName(cursor.getString(1));
                category.setType(OperationType.toEnum(cursor.getString(2)));
                category.setBudjet(cursor.getInt(3));
                list.add(category);
            }
            while (cursor.moveToNext());
        }
        close();
        return list;
    }

    public ArrayList<Category> getCategories(OperationType type){
        openToWrite();
        String where = String.format("%s=\"%s\"",CashFlowDbHelper.CATEGORY_TYPE, type.toString());
        Cursor cursor = db.query(CashFlowDbHelper.TABLE_CATEGORY, null, where, null, null, null, CashFlowDbHelper.CATEGORY_NAME);
        ArrayList<Category> list = new ArrayList<Category>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                Category category = new Category();
                category.setID(cursor.getInt(0));
                category.setName(cursor.getString(1));
                category.setType(OperationType.toEnum(cursor.getString(2)));
                category.setBudjet(cursor.getInt(3));
                list.add(category);
            }
            while (cursor.moveToNext());
        }
        close();
        return list;
    }

    public ArrayList<Map<String, String>> getCategoryCost(OperationType type, Date start, Date end){

        openToWrite();
        String sqlQuery = "SELECT "
                + "CATEGORY.name as name, "
                + "CATEGORY.budjet as budjet, "
                + "COST.sum as sum "
                + "FROM category as CATEGORY "
                + "LEFT OUTER JOIN "
                + "(SELECT "
                +   "cost.categoryId as categoryId, "
                +   "SUM(cost.sum) as sum "
                +   "FROM categoryCost as cost "
                +   "WHERE cost.date >= " + start.getTime() + " "
                +   "AND cost.date <= " + end.getTime() + " "
                +   "GROUP BY cost.categoryId) as COST "
                + "ON CATEGORY._id = COST.categoryId "
                + "WHERE CATEGORY.type = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{type.toString()});

        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                Map<String, String> m = new HashMap<String, String>();
                m.put("name", cursor.getString(0));
                m.put("budjet", cursor.getString(1));
                m.put("sum", String.format("%,d",cursor.getInt(2)));
                list.add(m);
            }
            while (cursor.moveToNext());
        }

        close();

        return list;

    }

    public Map<String, Integer> getCost(OperationType type, Date start, Date end){

        openToWrite();
        String sqlQuery = "SELECT "
                + "SUM(CATEGORY.budjet) as budjet, "
                + "SUM(IFNULL(COST.sum,0)) as sum "
                + "FROM category as CATEGORY "
                + "LEFT OUTER JOIN "
                + "(SELECT "
                +   "cost.categoryId as categoryId, "
                +   "SUM(cost.sum) as sum "
                +   "FROM categoryCost as cost "
                +   "WHERE cost.date >= " + start.getTime() + " "
                +   "AND cost.date <= " + end.getTime() + " "
                +   "GROUP BY cost.categoryId) as COST "
                + "ON CATEGORY._id = COST.categoryId "
                + "WHERE CATEGORY.type = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{type.toString()});

        Map<String, Integer> m = new HashMap<String, Integer>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                m.put("budjet", cursor.getInt(0));
                m.put("sum",    cursor.getInt(1));
            }
            while (cursor.moveToNext());
        }

        close();

        return m;

    }

    public int getTotalSum(OperationType type, Date start, Date end){
        openToWrite();
        String sqlQuery = "SELECT "
                +   "SUM(COST.sum) as sum "
                +   "FROM categoryCost as COST "
                +   "INNER JOIN "
                +   "category as CATEGORY "
                +   "ON COST.categoryId = CATEGORY._id "
                +   "WHERE COST.date >= " + start.getTime() + " "
                +   "AND COST.date <= " + end.getTime() + " "
                +   "AND CATEGORY.type = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{type.toString()});

        int result = 0;
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }

        close();

        return result;

    }

    public int getTotalBudjet(OperationType type, Date start, Date end) {
        openToWrite();

        String[] columns = new String[] {"SUM("+CashFlowDbHelper.CATEGORY_BUDJET+")"};
        String where = CashFlowDbHelper.CATEGORY_TYPE + " = ?";
        String[] whereArgs = new String[]{type.toString()};

        Cursor cursor = db.query(CashFlowDbHelper.TABLE_CATEGORY, columns, where, whereArgs, null, null, null);

        int result = 0;

        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        close();
        return result;

    }

    public int getCategorySum(Category category, Date start, Date end){
        openToWrite();
        String sqlQuery = "SELECT "
                +   "SUM(COST.sum) as sum "
                +   "FROM categoryCost as COST "
                +   "INNER JOIN "
                +   "category as CATEGORY "
                +   "ON COST.categoryId = CATEGORY._id "
                +   "WHERE COST.date >= " + start.getTime() + " "
                +   "AND COST.date <= " + end.getTime() + " "
                +   "AND CATEGORY._id = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{String.valueOf(category.getID())});

        int result = 0;
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }

        close();

        return result;

    }

    public int getCategoryBudjet(Category category, Date start, Date end) {
        openToWrite();

        String[] columns = new String[] {"SUM("+CashFlowDbHelper.CATEGORY_BUDJET+")"};
        String where = CashFlowDbHelper._ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(category.getID())};

        Cursor cursor = db.query(CashFlowDbHelper.TABLE_CATEGORY, columns, where, whereArgs, null, null, null);

        int result = 0;

        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        close();
        return result;

    }


    private ContentValues valuesCategory(Category category){
        ContentValues values = new ContentValues(3);

        values.put(CashFlowDbHelper.CATEGORY_NAME, category.getName());
        values.put(CashFlowDbHelper.CATEGORY_TYPE, category.getType().toString());
        values.put(CashFlowDbHelper.CATEGORY_BUDJET, category.getBudjet());

        return values;
    }

    public int addCategory(Category category){
        openToWrite();
        int res = (int)db.insertOrThrow(CashFlowDbHelper.TABLE_CATEGORY, null, valuesCategory(category));
        close();

        return res;
    }

    public int updateCategory(Category category){
        openToWrite();
        String where = String.format("%s=%d",CashFlowDbHelper._ID, category.getID());
        int res = db.update(CashFlowDbHelper.TABLE_CATEGORY, valuesCategory(category), where, null);
        close();

        return res;
    }

    public int deleteCategory(Category category){
        openToWrite();
        String where = String.format("%s=%d",CashFlowDbHelper._ID, category.getID());
        int res = db.delete(CashFlowDbHelper.TABLE_CATEGORY, where, null);
        close();

        return res;
    }


    public ArrayList<Operation> getOperations(){

        ArrayList<Account> accounts = getAccounts();
        HashMap<Integer, Account> mapAccount = new HashMap<>();
        for (Account account:accounts) {
            mapAccount.put(account.getID(), account);
        }

        ArrayList<Category> categores = getCategories();
        HashMap<Integer, Category> mapCategory = new HashMap<>();
        for (Category category:categores) {
            mapCategory.put(category.getID(), category);
        }

        openToWrite();
        Cursor cursor = db.query(CashFlowDbHelper.TABLE_OPERATION, null, null, null, null, null, CashFlowDbHelper.OPERATION_DATE + " DESC");
        ArrayList<Operation> list = new ArrayList<Operation>();
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
        close();
        return list;
    }

    public int addOperation(Operation operation){
        openToWrite();

        int res = -1;

        db.beginTransaction();
        try {
            ContentValues values = OperationValues(operation);
            res = (int)db.insertOrThrow(CashFlowDbHelper.TABLE_OPERATION, null, values);
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

        values.put(CashFlowDbHelper.OPERATION_DATE, operation.getDate().getTime());
        values.put(CashFlowDbHelper.OPERATION_TYPE, operation.getType().toString());
        values.put(CashFlowDbHelper.OPERATION_ACCOUNT_ID, accountId);
        values.put(CashFlowDbHelper.OPERATION_CATEGORY_ID, categoryId);
        values.put(CashFlowDbHelper.OPERATION_RECIPIENT_ACCOUNT_ID, recipientAccountId);
        values.put(CashFlowDbHelper.OPERATION_SUM, operation.getSum());

        return values;
    }

    public int updateOperation(Operation operation){
        openToWrite();

        int res = -1;

        db.beginTransaction();
        try {
            ContentValues values = OperationValues(operation);
            String where = String.format("%s=%d", CashFlowDbHelper._ID, operation.getID());
            res = db.update(CashFlowDbHelper.TABLE_OPERATION, values, where, null);

            deleteBalance(operation);
            addBalance(operation);

            deleteCategoryCost(operation);
            addCategoryCost(operation);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        close();
        return res;
    }

    public int deleteOperation(Operation operation){
        openToWrite();

        int res = -1;

        db.beginTransaction();
        try {
            String where = String.format("%s=%d",CashFlowDbHelper._ID, operation.getID());
            res = db.delete(CashFlowDbHelper.TABLE_OPERATION, where, null);

            deleteBalance(operation);
            deleteCategoryCost(operation);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        close();
        return res;
    }


    public ArrayList<Goal> getGoals(){
        openToWrite();
        Cursor cursor = db.query(CashFlowDbHelper.TABLE_GOAL, null, null, null, null, null, CashFlowDbHelper.GOAL_NAME);
        ArrayList<Goal> list = new ArrayList<Goal>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                Goal goal = new Goal();
                goal.setId(cursor.getInt(0));
                goal.setName(cursor.getString(1));
                goal.setSum(cursor.getInt(2));
                goal.setDone(cursor.getInt(3)>0);
                list.add(goal);
            }
            while (cursor.moveToNext());
        }
        close();
        return list;
    }

    public int addGoal(Goal goal){
        openToWrite();
        ContentValues values = new ContentValues(3);

        values.put(CashFlowDbHelper.GOAL_NAME, goal.getName());
        values.put(CashFlowDbHelper.GOAL_SUM, goal.getSum());
        values.put(CashFlowDbHelper.GOAL_DONE, goal.isDone());

        int res = (int)db.insertOrThrow(CashFlowDbHelper.TABLE_GOAL, null, values);
        close();
        return res;
    }

    public int updateGoal(Goal goal){
        openToWrite();
        ContentValues values = new ContentValues(3);

        values.put(CashFlowDbHelper.GOAL_NAME, goal.getName());
        values.put(CashFlowDbHelper.GOAL_SUM, goal.getSum());
        values.put(CashFlowDbHelper.GOAL_DONE, goal.isDone());

        String where = String.format("%s=%d",CashFlowDbHelper._ID, goal.getId());

        int res = db.update(CashFlowDbHelper.TABLE_GOAL, values, where, null);
        close();
        return res;
    }

    public int deleteGoal(Goal goal){
        openToWrite();

        String where = String.format("%s=%d",CashFlowDbHelper._ID, goal.getId());

        int res = db.delete(CashFlowDbHelper.TABLE_GOAL, where, null);
        close();
        return res;
    }


    public ArrayList<Currency> getCurrencies(){
        openToWrite();
        Cursor cursor = db.query(CashFlowDbHelper.TABLE_CURRENCY, null, null, null, null, null, null);
        ArrayList<Currency> list = new ArrayList<Currency>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                Currency currency = new Currency();
                currency.setId(cursor.getInt(0));
                currency.setName(cursor.getString(1));
                currency.setRate(cursor.getFloat(2));
                list.add(currency);
            }
            while (cursor.moveToNext());
        }
        close();
        return list;
    }

    public int updateCurrency(Currency currency){
        openToWrite();
        ContentValues values = new ContentValues(2);

        values.put(CashFlowDbHelper.CURRENCY_NAME, currency.getName());
        values.put(CashFlowDbHelper.CURRENCY_RATE, currency.getRate());

        String where = String.format("%s=%d",CashFlowDbHelper._ID, currency.getId());

        int res = db.update(CashFlowDbHelper.TABLE_CURRENCY, values, where, null);
        close();
        return res;
    }



    private void addBalance(Operation operation){
        int accountId = operation.getAccount()==null ? -1 : operation.getAccount().getID();
        int recipientAccountId = operation.getRecipientAccount()==null ? -1 : operation.getRecipientAccount().getID();

        switch (operation.getType()){
            case IN:
                ContentValues balanceValuesIn = new ContentValues(4);
                balanceValuesIn.put(CashFlowDbHelper.ACCOUNT_BALANCE_DATE, operation.getDate().getTime());
                balanceValuesIn.put(CashFlowDbHelper.ACCOUNT_BALANCE_OPERATION_ID, operation.getID());
                balanceValuesIn.put(CashFlowDbHelper.ACCOUNT_BALANCE_ACCOUNT_ID, accountId);
                balanceValuesIn.put(CashFlowDbHelper.ACCOUNT_BALANCE_SUM, operation.getSum());

                db.insertOrThrow(CashFlowDbHelper.TABLE_ACCOUNT_BALANCE, null, balanceValuesIn);
                break;
            case OUT:
                ContentValues balanceValuesOut = new ContentValues(4);
                balanceValuesOut.put(CashFlowDbHelper.ACCOUNT_BALANCE_DATE, operation.getDate().getTime());
                balanceValuesOut.put(CashFlowDbHelper.ACCOUNT_BALANCE_OPERATION_ID, operation.getID());
                balanceValuesOut.put(CashFlowDbHelper.ACCOUNT_BALANCE_ACCOUNT_ID, accountId);
                balanceValuesOut.put(CashFlowDbHelper.ACCOUNT_BALANCE_SUM, -1*operation.getSum());

                db.insertOrThrow(CashFlowDbHelper.TABLE_ACCOUNT_BALANCE, null, balanceValuesOut);
                break;
            case TRANSFER:
                ContentValues balanceValuesT1 = new ContentValues(4);
                balanceValuesT1.put(CashFlowDbHelper.ACCOUNT_BALANCE_DATE, operation.getDate().getTime());
                balanceValuesT1.put(CashFlowDbHelper.ACCOUNT_BALANCE_OPERATION_ID, operation.getID());
                balanceValuesT1.put(CashFlowDbHelper.ACCOUNT_BALANCE_ACCOUNT_ID, accountId);
                balanceValuesT1.put(CashFlowDbHelper.ACCOUNT_BALANCE_SUM, -1*operation.getSum());

                db.insertOrThrow(CashFlowDbHelper.TABLE_ACCOUNT_BALANCE, null, balanceValuesT1);

                ContentValues balanceValuesT2 = new ContentValues(4);
                balanceValuesT2.put(CashFlowDbHelper.ACCOUNT_BALANCE_DATE, operation.getDate().getTime());
                balanceValuesT2.put(CashFlowDbHelper.ACCOUNT_BALANCE_OPERATION_ID, operation.getID());
                balanceValuesT2.put(CashFlowDbHelper.ACCOUNT_BALANCE_ACCOUNT_ID, recipientAccountId);
                balanceValuesT2.put(CashFlowDbHelper.ACCOUNT_BALANCE_SUM, operation.getSum());

                db.insertOrThrow(CashFlowDbHelper.TABLE_ACCOUNT_BALANCE, null, balanceValuesT2);
                break;
        }

    }

    private void deleteBalance(Operation operation){
        String where = String.format("%s=%d",CashFlowDbHelper.ACCOUNT_BALANCE_OPERATION_ID, operation.getID());
        db.delete(CashFlowDbHelper.TABLE_ACCOUNT_BALANCE, where, null);
    }


    private void addCategoryCost(Operation operation){
        if ((operation.getType().equals(OperationType.IN))||(operation.getType().equals(OperationType.OUT))){
            ContentValues values = new ContentValues(5);
            values.put(CashFlowDbHelper.CATEGORY_COST_DATE, operation.getDate().getTime());
            values.put(CashFlowDbHelper.CATEGORY_COST_OPERATION_ID, operation.getID());
            values.put(CashFlowDbHelper.CATEGORY_COST_ACCOUNT_ID, operation.getAccount().getID());
            values.put(CashFlowDbHelper.CATEGORY_COST_CATEGORY_ID, operation.getCategory().getID());
            values.put(CashFlowDbHelper.CATEGORY_COST_SUM, operation.getSum());

            db.insertOrThrow(CashFlowDbHelper.TABLE_CATEGORY_COST, null, values);
        }
    }

    private void deleteCategoryCost(Operation operation){
        String where = String.format("%s=%d",CashFlowDbHelper.CATEGORY_COST_OPERATION_ID, operation.getID());
        db.delete(CashFlowDbHelper.TABLE_CATEGORY_COST, where, null);
    }
}
