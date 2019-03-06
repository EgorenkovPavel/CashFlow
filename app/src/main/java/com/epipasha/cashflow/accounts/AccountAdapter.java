package com.epipasha.cashflow.accounts;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.Utils;
import com.epipasha.cashflow.data.entites.AccountWithBalance;

import java.util.List;
import java.util.Locale;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountHolder> {

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
    public AccountAdapter.AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account, parent, false);
        return new AccountAdapter.AccountHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountHolder holder, int position) {
        AccountWithBalance mAccount = mAccounts.get(position);

        holder.itemView.setTag(mAccount.getId());
        holder.accountTitleView.setText(mAccount.getTitle());
        holder.accountSumView.setText(Utils.formatNumber(mAccount.getSum()));
    }

    public void setAccounts(List<AccountWithBalance> accounts){
        this.mAccounts = accounts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mAccounts == null)
            return 0;
        else
            return mAccounts.size();
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
            int elementId = mAccounts.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
