package com.epipasha.cashflow.fragments.listFragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

import com.epipasha.cashflow.db.CashFlowDbHelper;

public class DbListLoader extends AsyncTaskLoader<Cursor> {

    private Context context;
    private String query;


    public DbListLoader(Context context, String query) {
        super(context);
        this.context = context;
        this.query = query;
    }

    @Override
    public Cursor loadInBackground() {

        CashFlowDbHelper dbHelper = new CashFlowDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }
}
