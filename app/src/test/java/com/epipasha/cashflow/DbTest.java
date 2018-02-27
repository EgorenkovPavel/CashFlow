package com.epipasha.cashflow;

import static com.epipasha.cashflow.data.CashFlowContract.*;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import static org.junit.Assert.*;

import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.data.CashFlowContract.AccountEntry;
import com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
import com.epipasha.cashflow.data.CashFlowDbHelper;
import com.epipasha.cashflow.objects.OperationType;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;


@RunWith(RobolectricTestRunner.class)
public class DbTest {

    private CashFlowDbHelper dbHelper;
    private SQLiteDatabase mDatabase;

    @Before
    public void setUp() throws Exception {
        dbHelper = new CashFlowDbHelper(RuntimeEnvironment.application);
        mDatabase = dbHelper.getWritableDatabase();
        dbHelper.dropTables(mDatabase);
        dbHelper.onCreate(mDatabase);
    }

    @After
    public void tearDown() {
        dbHelper.dropTables(mDatabase);
    }

    @Test
    public void testAccountProviderInsertAndQuery() throws Exception {

        ContentResolver resolver = RuntimeEnvironment.application.getContentResolver();

        //Accounts
        ContentValues values = new ContentValues();
        values.put(AccountEntry.COLUMN_TITLE, "test");

        int accountId = testProviderInsertAndQuery(values, AccountEntry.CONTENT_URI, AccountEntry.TABLE_NAME);

        //Categories
        ContentValues valuesIn = new ContentValues();
        valuesIn.put(CategoryEntry.COLUMN_TITLE, "test");
        valuesIn.put(CategoryEntry.COLUMN_TYPE, OperationType.IN.toDbValue());
        valuesIn.put(CategoryEntry.COLUMN_BUDGET, 1000);

        int categoryInId = testProviderInsertAndQuery(valuesIn, CategoryEntry.CONTENT_URI, CategoryEntry.TABLE_NAME);

        ContentValues valuesOut = new ContentValues();
        valuesOut.put(CategoryEntry.COLUMN_TITLE, "privet");
        valuesOut.put(CategoryEntry.COLUMN_TYPE, OperationType.OUT.toDbValue());
        valuesOut.put(CategoryEntry.COLUMN_BUDGET, 7000);

        testProviderInsertAndQuery(valuesOut, CategoryEntry.CONTENT_URI, CategoryEntry.TABLE_NAME);

        ContentValues valuesTran = new ContentValues();
        valuesTran.put(CategoryEntry.COLUMN_TITLE, "hoi");
        valuesTran.put(CategoryEntry.COLUMN_TYPE, OperationType.TRANSFER.toDbValue());
        valuesTran.put(CategoryEntry.COLUMN_BUDGET, 120);

        Uri answer = resolver.insert(CategoryEntry.CONTENT_URI, valuesTran);
        assertNull(answer);

        //Operations
        long time = new Date().getTime();
        int operationSum = 1000;

        ContentValues valuesOp = new ContentValues();
        valuesOp.put(OperationEntry.COLUMN_DATE, time);
        valuesOp.put(OperationEntry.COLUMN_TYPE, OperationType.IN.toDbValue());
        valuesOp.put(OperationEntry.COLUMN_ACCOUNT_ID, accountId);
        valuesOp.put(OperationEntry.COLUMN_CATEGORY_ID, categoryInId);
        valuesOp.put(OperationEntry.COLUMN_SUM, operationSum);

        int operationId = testProviderInsertAndQuery(valuesOp, OperationEntry.CONTENT_URI, OperationEntry.TABLE_NAME);

        ContentValues valuesBal = new ContentValues();
        valuesBal.put(AccountBalanceEntry.COLUMN_DATE, time);
        valuesBal.put(AccountBalanceEntry.COLUMN_OPERATION_ID, operationId);
        valuesBal.put(AccountBalanceEntry.COLUMN_ACCOUNT_ID, accountId);
        valuesBal.put(AccountBalanceEntry.COLUMN_SUM, operationSum);

        readCompareData(valuesBal, AccountBalanceEntry.TABLE_NAME,
                AccountBalanceEntry.COLUMN_OPERATION_ID + " = " + operationId);

        ContentValues valuesCost = new ContentValues();
        valuesCost.put(CategoryCostEntry.COLUMN_DATE, time);
        valuesCost.put(CategoryCostEntry.COLUMN_OPERATION_ID, operationId);
        valuesCost.put(CategoryCostEntry.COLUMN_ACCOUNT_ID, accountId);
        valuesCost.put(CategoryCostEntry.COLUMN_CATEGORY_ID, categoryInId);
        valuesCost.put(CategoryCostEntry.COLUMN_SUM, operationSum);

        readCompareData(valuesCost, CategoryCostEntry.TABLE_NAME,
                CategoryCostEntry.COLUMN_OPERATION_ID + " = " + operationId);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date start = new Date(cal.getTimeInMillis());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date end = new Date(cal.getTimeInMillis());

        Cursor cursor = mDatabase.query(CategoryCostEntry.TABLE_NAME,
                new String[]{"SUM(" + CategoryCostEntry.COLUMN_SUM + ") AS " + CategoryCostEntry.COLUMN_SUM},
                CategoryCostEntry.COLUMN_CATEGORY_ID + " = " + categoryInId + " " +
                        " AND " + CategoryCostEntry.COLUMN_DATE + " between "+start.getTime()+" AND " + end.getTime(),
                null,
                null,
                null,
                null);

        assertNotNull(cursor);
        assertTrue(cursor.moveToFirst());

        assertEquals(cursor.getInt(cursor.getColumnIndex(CategoryCostEntry.COLUMN_SUM)), operationSum);
    }

    private int testProviderInsertAndQuery(ContentValues values, Uri uri, String tableName){

        ContentResolver resolver = RuntimeEnvironment.application.getContentResolver();

        Uri answer = resolver.insert(uri, values);

        assertNotNull(answer);

        int id = Integer.valueOf(answer.getLastPathSegment());
        assertNotEquals(id, "-1");

        readCompareData(values, tableName, BaseColumns._ID + " = " + id);

        return id;
    }

    private void readCompareData(ContentValues values, String tableName, String selection){
        Cursor cursor = mDatabase.query(tableName,
                null,
                selection,
                null,
                null,
                null,
                null);

        assertNotNull(cursor);
        assertTrue(cursor.moveToFirst());

        for (String key: values.keySet()) {
            Object value = values.get(key);
            if (value instanceof String){
                String stringValueFromDatabase = cursor.getString(cursor.getColumnIndex(key));
                assertEquals(stringValueFromDatabase, value);
            }else if(value instanceof Integer){
                int intValueFromDatabase = cursor.getInt(cursor.getColumnIndex(key));
                assertEquals(intValueFromDatabase, value);
            }else if(value instanceof Long){
                long longValueFromDatabase = cursor.getLong(cursor.getColumnIndex(key));
                assertEquals(longValueFromDatabase, value);
            }else{
                fail();
            }
        }
    }

}
