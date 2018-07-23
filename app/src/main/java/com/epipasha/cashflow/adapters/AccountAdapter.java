package com.epipasha.cashflow.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.entites.AccountWithBalance;

import java.util.List;
import java.util.Locale;

public class AccountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int HEADER_ITEM = 234;
    protected static final int LIST_ITEM = 897;

    private List<AccountWithBalance> mAccounts;
    private ItemClickListener mItemClickListener;

    public AccountAdapter(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {

            case HEADER_ITEM: {
                return onCreateHeaderViewHolder(parent, viewType);
            }

            case LIST_ITEM: {
                return onCreateItemViewHolder(parent, viewType);
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

    }

    private RecyclerView.ViewHolder onCreateHeaderViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_account_header, parent, false);

        return new AccountAdapter.HeaderHolder(view);
    }

    private RecyclerView.ViewHolder onCreateItemViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_account, parent, false);

        return new AccountAdapter.AccountHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (position == 0) {

            onBindHeaderViewHolder((HeaderHolder) holder);

        } else {
            onBindItemViewHolder((AccountHolder) holder, position - 1);
        }


    }

    private void onBindHeaderViewHolder(HeaderHolder holder){
        int sum = 0;
        for (AccountWithBalance account:mAccounts) {
            sum += account.getSum();
        }
        holder.accountSumView.setText(String.format(Locale.getDefault(), "%,d", sum));
    }

    private void onBindItemViewHolder(AccountHolder holder, int position){
        AccountWithBalance mAccount = mAccounts.get(position);

        //Set values
        holder.itemView.setTag(mAccount.getId());
        holder.accountTitleView.setText(mAccount.getTitle());
        holder.accountSumView.setText(String.format(Locale.getDefault(), "%,d", mAccount.getSum()));
    }

    public void setAccounts(List<AccountWithBalance> accounts){
        this.mAccounts = accounts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mAccounts == null || mAccounts.size() == 0)
            return 0;
        else
            return mAccounts.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return HEADER_ITEM;
        } else {
            return LIST_ITEM;
        }
    }

    class AccountHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView accountTitleView;
        TextView accountSumView;

        public AccountHolder(View itemView) {
            super(itemView);
            accountTitleView = itemView.findViewById(R.id.account_list_item_name);
            accountSumView = itemView.findViewById(R.id.account_list_item_sum);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mAccounts.get(getAdapterPosition() - 1).getId();
            mItemClickListener.onItemClickListener(elementId);
        }

    }

    class HeaderHolder extends RecyclerView.ViewHolder{
        TextView accountTitleView;
        TextView accountSumView;

        public HeaderHolder(View itemView) {
            super(itemView);
            accountTitleView = itemView.findViewById(R.id.account_list_item_name);
            accountSumView = itemView.findViewById(R.id.account_list_item_sum);
        }

    }
}
