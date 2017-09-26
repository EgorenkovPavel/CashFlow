package com.epipasha.cashflow.fragments.account;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.MainActivity;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.fragments.ListDetailActivity;
import com.epipasha.cashflow.objects.Account;

import java.util.ArrayList;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder>
    {
        private Fragment mFragment;
        private ArrayList<Account> accounts;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
            // each data item is just a string in this case
            public TextView mAccountName;
            public TextView mAccountSum;
            public TextView mAccountCurrency;
            public ViewHolder(View v) {
                super(v);
                mAccountName = (TextView) v.findViewById(R.id.account_list_item_name);
                mAccountSum = (TextView) v.findViewById(R.id.account_list_item_sum);
                mAccountCurrency = (TextView)v.findViewById(R.id.account_list_item_currency);

                v.setOnClickListener(this);
                v.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("Instance", accounts.get(getAdapterPosition()));
                i.putExtra("Position", getAdapterPosition());
                i.setClass(mFragment.getActivity(), ListDetailActivity.class);
                mFragment.startActivityForResult(i, MainActivity.RESULT_CANCELED);
            }

            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.list_menu);
                popup.setOnMenuItemClickListener(this);
                popup.show();
                return false;
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.list_menu_delete){
                    CashFlowDbManager.getInstance(mFragment.getActivity()).deleteAccount(accounts.get(getAdapterPosition()));

                    accounts.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), accounts.size());
                  }
                return false;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public AccountListAdapter(Fragment fragment, ArrayList<Account> accounts) {
            this.mFragment = fragment;
            this.accounts = accounts;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public AccountListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_account, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mAccountName.setText(accounts.get(position).getName());
            holder.mAccountSum.setText(String.format("%,d",accounts.get(position).getBalance()));

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return accounts.size();
        }
    }

