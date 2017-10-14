package com.epipasha.cashflow.fragments.listFragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

import com.epipasha.cashflow.db.CashFlowDbHelper;

class DbListLoader extends AsyncTaskLoader<Cursor> {

    private final Context context;
    private final String query;


    public DbListLoader(Context context, String query) {
        super(context);
        this.context = context;
        this.query = query;
    }

    @Override
    public Cursor loadInBackground() {

        CashFlowDbHelper dbHelper = new CashFlowDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(query, null);
    }
}
