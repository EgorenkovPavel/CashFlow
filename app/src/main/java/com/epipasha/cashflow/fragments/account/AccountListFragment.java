package com.epipasha.cashflow.fragments.account;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.db.CashFlowContract;
import com.epipasha.cashflow.db.CashFlowContract.AccountBalanceEntry;
import com.epipasha.cashflow.db.CashFlowContract.AccountEntry;
import com.epipasha.cashflow.db.CashFlowDbHelper;
import com.epipasha.cashflow.fragments.listAdapter.RecyclerListAdapter;
import com.epipasha.cashflow.fragments.listAdapter.RecyclerListFragment;
import com.epipasha.cashflow.fragments.listAdapter.RecyclerViewHolder;

public class AccountListFragment extends RecyclerListFragment {

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new CustomAdapter(this);
    }

    @Override
    public Cursor Cursor(SQLiteDatabase db) {

        String sqlQuery = "SELECT "
                + "PL._id as _id, "
                + "PL.name as name, "
                + "PL.currencyId as currencyId, "
                + "PS.sum as sum "
                + "FROM account as PL "
                + "LEFT OUTER JOIN "
                + "(SELECT "
                + "balance.accountId as accountId, "
                + "SUM(balance.sum) as sum "
                + "FROM accountBalance as balance "
                + "GROUP BY balance.accountId) as PS "
                + "ON PL._id = PS.accountId "
                + "ORDER BY PL.Name";
        return db.rawQuery(sqlQuery, null);
    }


    public class CustomAdapter extends RecyclerListAdapter {

        public CustomAdapter(RecyclerListFragment frag) {
            super(frag);
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.list_item_account, parent, false);

            CustomViewHolder vh = new CustomViewHolder(v);
            return vh;
        }

        public class CustomViewHolder extends RecyclerViewHolder{
            private TextView mAccountName;
            private TextView mAccountSum;
            private int id;

            public CustomViewHolder(View v) {
                super(v);

                mAccountName = (TextView) v.findViewById(R.id.account_list_item_name);
                mAccountSum = (TextView) v.findViewById(R.id.account_list_item_sum);
            }

            public void setData(Cursor c) {

                id = c.getInt(c.getColumnIndex(AccountEntry._ID));

                String name = c.getString(c.getColumnIndex(AccountEntry.COLUMN_TITLE));
                int balance = c.getInt(c.getColumnIndex(AccountBalanceEntry.COLUMN_SUM));

                mAccountName.setText(name);
                mAccountSum.setText(String.format("%,d", balance));
            }
        }
    }
}
