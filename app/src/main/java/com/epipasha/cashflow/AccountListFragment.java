package com.epipasha.cashflow;

import static com.epipasha.cashflow.db.CashFlowContract.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.db.CashFlowContract;
import com.epipasha.cashflow.db.CashFlowDbHelper;

import java.util.Locale;

public class AccountListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private Adapter mAdapter;

    public AccountListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_account_list, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration div = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        div.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(div);

        mAdapter = new Adapter();
        recyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new dbLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        private Cursor cursor;

        public void setCursor(Cursor cursor){
            this.cursor = cursor;
            notifyDataSetChanged();
        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_account, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            cursor.moveToPosition(position);

            holder.id = cursor.getInt(0);
            holder.mAccountName.setText(cursor.getString(1));
            holder.mAccountSum.setText(String.format(Locale.getDefault(),"%,d",cursor.getInt(2)));
        }

        @Override
        public int getItemCount() {
            if(cursor == null){
                return 0;
            }else{
                return cursor.getCount();
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            int id;
            final TextView mAccountName;
            final TextView mAccountSum;

            public ViewHolder(View v) {
                super(v);

                mAccountName = (TextView) v.findViewById(R.id.account_list_item_name);
                mAccountSum = (TextView) v.findViewById(R.id.account_list_item_sum);
            }
        }

    }

    static class dbLoader extends AsyncTaskLoader<Cursor> {

        Context context;

        public dbLoader(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public Cursor loadInBackground() {

            CashFlowDbHelper dbHelper = new CashFlowDbHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
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
            return cursor;
        }

    }
}
