package com.epipasha.cashflow.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.epipasha.cashflow.data.CashFlowContract.AccountBalanceEntry;
import com.epipasha.cashflow.data.CashFlowContract.AccountEntry;
import com.epipasha.cashflow.data.CashFlowContract.CategoryCostEntry;
import com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
import com.epipasha.cashflow.data.CashFlowContract.OperationEntry;
import com.epipasha.cashflow.objects.OperationType;

public class CashFlowProvider extends ContentProvider {

    public static final int ACCOUNTS = 100;
    public static final int ACCOUNT_WITH_ID = 101;
    public static final int CATEGORIES = 200;
    public static final int CATEGORY_WITH_ID = 201;
    public static final int OPERATIONS = 300;
    public static final int OPERATION_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(CashFlowContract.AUTHORITY, CashFlowContract.PATH_ACCOUNTS, ACCOUNTS);
        uriMatcher.addURI(CashFlowContract.AUTHORITY, CashFlowContract.PATH_ACCOUNTS + "/#", ACCOUNT_WITH_ID);
        uriMatcher.addURI(CashFlowContract.AUTHORITY, CashFlowContract.PATH_CATEGORY, CATEGORIES);
        uriMatcher.addURI(CashFlowContract.AUTHORITY, CashFlowContract.PATH_CATEGORY + "/#", CATEGORY_WITH_ID);
        uriMatcher.addURI(CashFlowContract.AUTHORITY, CashFlowContract.PATH_OPERATION, OPERATIONS);
        uriMatcher.addURI(CashFlowContract.AUTHORITY, CashFlowContract.PATH_OPERATION + "/#", OPERATION_WITH_ID);

        return uriMatcher;
    }

    private CashFlowDbHelper mCashFlowDbHelper;

    private static final SQLiteQueryBuilder sAccountsQueryBuilder;

    static{
        sAccountsQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sAccountsQueryBuilder.setTables(
                AccountEntry.TABLE_NAME + " " +
                        "LEFT OUTER JOIN " +
                        "(SELECT " +
                        AccountBalanceEntry.TABLE_NAME + "." + AccountBalanceEntry.COLUMN_ACCOUNT_ID + ", " +
                        "SUM(" + AccountBalanceEntry.TABLE_NAME + "." + AccountBalanceEntry.COLUMN_SUM + ") as " + AccountEntry.SERVICE_COLUMN_SUM + " " +
                        "FROM " + AccountBalanceEntry.TABLE_NAME + " " +
                        "GROUP BY " + AccountBalanceEntry.TABLE_NAME + "." + AccountBalanceEntry.COLUMN_ACCOUNT_ID + ") " +
                        "as " + AccountBalanceEntry.TABLE_NAME + " " +
                        "ON " + AccountEntry.TABLE_NAME + "." + AccountEntry._ID + " = " +
                        AccountBalanceEntry.TABLE_NAME + "." + AccountBalanceEntry.COLUMN_ACCOUNT_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mCashFlowDbHelper = new CashFlowDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mCashFlowDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case ACCOUNTS:

                retCursor = sAccountsQueryBuilder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        AccountEntry.TABLE_NAME + "." + AccountEntry.COLUMN_TITLE);
                break;

            case ACCOUNT_WITH_ID:{
                Long id = Long.valueOf(uri.getLastPathSegment());

                retCursor = sAccountsQueryBuilder.query(
                        db,
                        projection,
                        AccountEntry.TABLE_NAME + "." + AccountEntry._ID + " = " + id,
                        selectionArgs,
                        null,
                        null,
                        null);

                break;

            }

            case CATEGORIES:{
                retCursor = db.query(CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        CategoryEntry.COLUMN_TITLE);
                break;
            }

            case CATEGORY_WITH_ID:{
                String id = uri.getLastPathSegment();
                retCursor = db.query(CategoryEntry.TABLE_NAME,
                        projection,
                        CategoryEntry._ID + " = ?",
                        new String[]{id},
                        null,
                        null,
                        null);
                break;
            }

            case OPERATIONS:{

                final String ACCOUNT_TABLE = "operation_account_table";
                final String CATEGORY_TABLE = "operation_category_table";
                final String RECIPIENT_OPERATION_TABLE = "operation_recipient_account_table";

                String sqlQuery2 = "SELECT " +
                        OperationEntry.TABLE_NAME + "." + OperationEntry._ID + ", " +
                        OperationEntry.TABLE_NAME + "." + OperationEntry.COLUMN_DATE + ", " +
                        OperationEntry.TABLE_NAME + "." + OperationEntry.COLUMN_TYPE + ", " +
                        OperationEntry.TABLE_NAME + "." + OperationEntry.COLUMN_SUM + ", " +
                        ACCOUNT_TABLE + "." + AccountEntry.COLUMN_TITLE + " as " + OperationEntry.SERVICE_COLUMN_ACCOUNT_TITLE + ", " +
                        CATEGORY_TABLE + "." + CategoryEntry.COLUMN_TITLE + " as " + OperationEntry.SERVICE_COLUMN_CATEGORY_TITLE + ", " +
                        RECIPIENT_OPERATION_TABLE + "." + AccountEntry.COLUMN_TITLE + " as " + OperationEntry.SERVICE_COLUMN_RECIPIENT_ACCOUNT_TITLE + " " +

                        "FROM " + OperationEntry.TABLE_NAME + " " +

                        "LEFT OUTER JOIN " +
                        AccountEntry.TABLE_NAME + " as " + ACCOUNT_TABLE + " " +
                        " ON " + OperationEntry.TABLE_NAME + "." + OperationEntry.COLUMN_ACCOUNT_ID +
                        " = " + ACCOUNT_TABLE + "." + AccountEntry._ID + " " +

                        "LEFT OUTER JOIN " +
                        CategoryEntry.TABLE_NAME + " as " + CATEGORY_TABLE + " " +
                        " ON " + OperationEntry.TABLE_NAME + "." + OperationEntry.COLUMN_CATEGORY_ID +
                        " = " + CATEGORY_TABLE + "." + CategoryEntry._ID + " " +

                        "LEFT OUTER JOIN " +
                        AccountEntry.TABLE_NAME + " as " + RECIPIENT_OPERATION_TABLE + " " +
                        " ON " + OperationEntry.TABLE_NAME + "." + OperationEntry.COLUMN_RECIPIENT_ACCOUNT_ID +
                        " = " + RECIPIENT_OPERATION_TABLE + "." + AccountEntry._ID + " " +

                        "ORDER BY " + OperationEntry.TABLE_NAME + "." + OperationEntry.COLUMN_DATE + "  DESC;";
                retCursor = db.rawQuery(sqlQuery2, null);

                break;
           }

            case OPERATION_WITH_ID:{
                String id = uri.getLastPathSegment();
                retCursor = db.query(OperationEntry.TABLE_NAME,
                        projection,
                        OperationEntry._ID + " = ?",
                        new String[]{id},
                        null,
                        null,
                        null);
                break;
            }

            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        Uri resUri = null;
        Long id;

        switch (sUriMatcher.match(uri)) {

            case ACCOUNTS: {
                id = mCashFlowDbHelper.getWritableDatabase().insert(
                        AccountEntry.TABLE_NAME,
                        null,
                        contentValues);

                if (id != -1) {
                    resUri = AccountEntry.buildAccountUriWithId(id);
                }

                break;
            }

            case CATEGORIES: {
                id = mCashFlowDbHelper.getWritableDatabase().insert(
                        CategoryEntry.TABLE_NAME,
                        null,
                        contentValues);

                if (id != -1) {
                    resUri = CategoryEntry.buildCategoryUriWithId(id);
                }
                break;
            }

            case OPERATIONS: {
                SQLiteDatabase db = mCashFlowDbHelper.getWritableDatabase();

                db.beginTransaction();
                try {
                    id = db.insert(
                            OperationEntry.TABLE_NAME,
                            null,
                            contentValues);

                    insertOperationAnalytics(db, contentValues, id);



                    if (id != -1) {
                        resUri = OperationEntry.buildOperationUriWithId(id);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (id != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return resUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsDeleted;

        switch (sUriMatcher.match(uri)){
            case OPERATION_WITH_ID:{
                String id = uri.getLastPathSegment();

                SQLiteDatabase db = mCashFlowDbHelper.getWritableDatabase();

                db.beginTransaction();
                try {
                    numRowsDeleted = db.delete(
                            OperationEntry.TABLE_NAME,
                            OperationEntry._ID + " = ?",
                            new String[]{id});

                    deleteOperationAnalytics(db, id);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsUpdated;

        switch (sUriMatcher.match(uri)) {

            case ACCOUNT_WITH_ID: {
                String id = uri.getLastPathSegment();
                numRowsUpdated = mCashFlowDbHelper.getWritableDatabase().update(
                        AccountEntry.TABLE_NAME,
                        contentValues,
                        AccountEntry._ID + " = ?",
                        new String[]{id});
                break;
            }

            case CATEGORY_WITH_ID: {
                String id = uri.getLastPathSegment();
                numRowsUpdated = mCashFlowDbHelper.getWritableDatabase().update(
                        CategoryEntry.TABLE_NAME,
                        contentValues,
                        CategoryEntry._ID + " = ?",
                        new String[]{id});
                break;
            }

            case OPERATION_WITH_ID: {
                String id = uri.getLastPathSegment();

                SQLiteDatabase db = mCashFlowDbHelper.getWritableDatabase();

                db.beginTransaction();
                try {
                    numRowsUpdated = db.update(
                        OperationEntry.TABLE_NAME,
                        contentValues,
                        OperationEntry._ID + " = ?",
                        new String[]{id});

                    deleteOperationAnalytics(db, id);
                    insertOperationAnalytics(db, contentValues, Long.valueOf(id));

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsUpdated;
    }

    private void insertOperationAnalytics(SQLiteDatabase db, ContentValues operationValues, long id){
        OperationType type = OperationType.toEnum(operationValues.getAsInteger(OperationEntry.COLUMN_TYPE));
        switch (type){
            case IN:{
                ContentValues balanceValues = new ContentValues();
                balanceValues.put(AccountBalanceEntry.COLUMN_DATE, operationValues.getAsInteger(OperationEntry.COLUMN_DATE));
                balanceValues.put(AccountBalanceEntry.COLUMN_ACCOUNT_ID, operationValues.getAsInteger(OperationEntry.COLUMN_ACCOUNT_ID));
                balanceValues.put(AccountBalanceEntry.COLUMN_OPERATION_ID, id);
                balanceValues.put(AccountBalanceEntry.COLUMN_SUM, operationValues.getAsInteger(OperationEntry.COLUMN_SUM));

                db.insert(AccountBalanceEntry.TABLE_NAME, null, balanceValues);
                break;
            }
            case OUT:{
                ContentValues balanceValues = new ContentValues();
                balanceValues.put(AccountBalanceEntry.COLUMN_DATE, operationValues.getAsInteger(OperationEntry.COLUMN_DATE));
                balanceValues.put(AccountBalanceEntry.COLUMN_ACCOUNT_ID, operationValues.getAsInteger(OperationEntry.COLUMN_ACCOUNT_ID));
                balanceValues.put(AccountBalanceEntry.COLUMN_OPERATION_ID, id);
                balanceValues.put(AccountBalanceEntry.COLUMN_SUM, -1* operationValues.getAsInteger(OperationEntry.COLUMN_SUM));

                db.insert(AccountBalanceEntry.TABLE_NAME, null, balanceValues);
                break;
            }
            case TRANSFER:{
                ContentValues balanceValuesIn = new ContentValues();
                balanceValuesIn.put(AccountBalanceEntry.COLUMN_DATE, operationValues.getAsInteger(OperationEntry.COLUMN_DATE));
                balanceValuesIn.put(AccountBalanceEntry.COLUMN_ACCOUNT_ID, operationValues.getAsInteger(OperationEntry.COLUMN_ACCOUNT_ID));
                balanceValuesIn.put(AccountBalanceEntry.COLUMN_OPERATION_ID, id);
                balanceValuesIn.put(AccountBalanceEntry.COLUMN_SUM, -1 * operationValues.getAsInteger(OperationEntry.COLUMN_SUM));

                db.insert(AccountBalanceEntry.TABLE_NAME, null, balanceValuesIn);

                ContentValues balanceValuesOut = new ContentValues();
                balanceValuesOut.put(AccountBalanceEntry.COLUMN_DATE, operationValues.getAsInteger(OperationEntry.COLUMN_DATE));
                balanceValuesOut.put(AccountBalanceEntry.COLUMN_ACCOUNT_ID, operationValues.getAsInteger(OperationEntry.COLUMN_RECIPIENT_ACCOUNT_ID));
                balanceValuesOut.put(AccountBalanceEntry.COLUMN_OPERATION_ID, id);
                balanceValuesOut.put(AccountBalanceEntry.COLUMN_SUM, operationValues.getAsInteger(OperationEntry.COLUMN_SUM));

                db.insertOrThrow(AccountBalanceEntry.TABLE_NAME, null, balanceValuesOut);
                break;
            }
        }

        if (type.equals(OperationType.IN) || type.equals(OperationType.OUT)){
            ContentValues values = new ContentValues();
            values.put(CategoryCostEntry.COLUMN_DATE, operationValues.getAsInteger(OperationEntry.COLUMN_DATE));
            values.put(CategoryCostEntry.COLUMN_OPERATION_ID, id);
            values.put(CategoryCostEntry.COLUMN_ACCOUNT_ID, operationValues.getAsInteger(OperationEntry.COLUMN_ACCOUNT_ID));
            values.put(CategoryCostEntry.COLUMN_CATEGORY_ID, operationValues.getAsInteger(OperationEntry.COLUMN_CATEGORY_ID));
            values.put(CategoryCostEntry.COLUMN_SUM, operationValues.getAsInteger(OperationEntry.COLUMN_SUM));

            db.insertOrThrow(CategoryCostEntry.TABLE_NAME, null, values);
        }

    }

    private void deleteOperationAnalytics(SQLiteDatabase db, String id){
        db.delete(AccountBalanceEntry.TABLE_NAME, AccountBalanceEntry.COLUMN_OPERATION_ID + " = " + id, null);
        db.delete(CategoryCostEntry.TABLE_NAME, CategoryCostEntry.COLUMN_OPERATION_ID + " = " + id, null);
    }
}
