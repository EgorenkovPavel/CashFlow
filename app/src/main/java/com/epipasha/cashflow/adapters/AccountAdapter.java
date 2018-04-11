package com.epipasha.cashflow.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.activities.DetailAccountActivity;

import java.util.Locale;

public class AccountAdapter extends HeaderAdapter<AccountAdapter.HeaderHolder, AccountAdapter.AccountHolder> {

    public AccountAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected AccountHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_account, parent, false);

        return new AccountAdapter.AccountHolder(view);
    }

    @Override
    protected HeaderHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_account_header, parent, false);

        return new AccountAdapter.HeaderHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(AccountHolder holder, int position) {
        int idIndex = mCursor.getColumnIndex(CashFlowContract.AccountEntry._ID);
        int titleIndex = mCursor.getColumnIndex(CashFlowContract.AccountEntry.COLUMN_TITLE);
        int sumIndex = mCursor.getColumnIndex(CashFlowContract.AccountEntry.SERVICE_COLUMN_SUM);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        int sum = mCursor.getInt(sumIndex);

        //Set values
        holder.itemView.setTag(id);
        ((AccountAdapter.AccountHolder)holder).accountTitleView.setText(title);
        ((AccountAdapter.AccountHolder)holder).accountSumView.setText(String.format(Locale.getDefault(), "%,d", sum));

    }

    @Override
    protected void onBindHeaderViewHolder(HeaderHolder holder) {
        int sumIndex = mCursor.getColumnIndex(CashFlowContract.AccountEntry.SERVICE_COLUMN_SUM);

        mCursor.moveToFirst();
        int sum = 0;
        while (!mCursor.isAfterLast()){
            sum += mCursor.getInt(sumIndex);
            mCursor.moveToNext();
        }

        holder.accountSumView.setText(String.format(Locale.getDefault(), "%,d", sum));

    }

    class AccountHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView accountTitleView;
        TextView accountSumView;

        public AccountHolder(View itemView) {
            super(itemView);
            accountTitleView = (TextView) itemView.findViewById(R.id.account_list_item_name);
            accountSumView = (TextView) itemView.findViewById(R.id.account_list_item_sum);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition()-1;
            mCursor.moveToPosition(adapterPosition);

            int idIndex = mCursor.getColumnIndex(CashFlowContract.AccountEntry._ID);
            int id = mCursor.getInt(idIndex);

            Intent i = new Intent(mContext, DetailAccountActivity.class);

            Uri uri = CashFlowContract.AccountEntry.buildAccountUriWithId(id);
            i.setData(uri);
            mContext.startActivity(i);
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder{
        TextView accountTitleView;
        TextView accountSumView;

        public HeaderHolder(View itemView) {
            super(itemView);
            accountTitleView = (TextView) itemView.findViewById(R.id.account_list_item_name);
            accountSumView = (TextView) itemView.findViewById(R.id.account_list_item_sum);
        }

    }
}
