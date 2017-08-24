package com.epipasha.cashflow.fragments.account;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.R;
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

                id = c.getInt(c.getColumnIndex(CashFlowDbHelper._ID));

                String name = c.getString(c.getColumnIndex(CashFlowDbHelper.ACCOUNT_NAME));
                int balance = c.getInt(c.getColumnIndex(CashFlowDbHelper.ACCOUNT_BALANCE_SUM));

                mAccountName.setText(name);
                mAccountSum.setText(String.format("%,d", balance));
            }
        }


//        public class CustomViewHolder extends RecyclerViewHolder
//                implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
//
//            private TextView mAccountName;
//            private TextView mAccountSum;
//
//            private int id;
//
//            public CustomViewHolder(View v) {
//                super(v);
//
//                mAccountName = (TextView) v.findViewById(R.id.account_list_item_name);
//                mAccountSum = (TextView) v.findViewById(R.id.account_list_item_sum);
//
//                v.setOnClickListener(this);
//                v.setOnLongClickListener(this);
//            }
//
//            @Override
//            public void onClick(View v) {
//
//                Account account = CashFlowDbManager.getInstance(getActivity()).getAccount(id);
//
//                Intent i = new Intent(getActivity(), ListDetailActivity.class);
//                i.putExtra("Instance", account);
//                i.putExtra("Position", getAdapterPosition());
//                frag.startActivityForResult(i, MainActivity.RESULT_CANCELED);
//            }
//
//            @Override
//            public boolean onLongClick(View v) {
//                PopupMenu popup = new PopupMenu(v.getContext(), v);
//                popup.inflate(R.menu.list_menu);
//                popup.setOnMenuItemClickListener(this);
//                popup.show();
//                return false;
//            }
//
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if(item.getItemId()==R.id.list_menu_delete){
//
//                    Account account = CashFlowDbManager.getInstance(getActivity()).getAccount(id);
//                    CashFlowDbManager.getInstance(frag.getActivity()).deleteAccount(account);
//                    notifyDataSetChanged();
//                }
//                return false;
//            }
//
//
//            public void setData(Cursor c) {
//
//                id = c.getInt(c.getColumnIndex(CashFlowDbHelper._ID));
//
//                String name = c.getString(c.getColumnIndex(CashFlowDbHelper.ACCOUNT_NAME));
//                int balance = c.getInt(c.getColumnIndex(CashFlowDbHelper.ACCOUNT_BALANCE_SUM));
//
//                mAccountName.setText(name);
//                mAccountSum.setText(String.format("%,d", balance));
//            }
//        }

    }
}
