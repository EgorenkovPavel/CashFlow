package com.epipasha.cashflow.fragments.operation;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.MainActivity;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.fragments.ListDetailActivity;
import com.epipasha.cashflow.objects.Operation;
import com.epipasha.cashflow.objects.OperationType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OperationListAdapter extends RecyclerView.Adapter<OperationListAdapter.ViewHolder>
{
    private final Fragment mFragment;
    private final ArrayList<Operation> operations;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
        // each data item is just a string in this case
        public final TextView mDate;
        public final TextView mAccount;
        public final TextView mCategory;
        public final TextView mSum;
        public final ImageView mType;

        public ViewHolder(View v) {
            super(v);
            mDate = (TextView) v.findViewById(R.id.operation_list_item_date);
            mAccount = (TextView)v.findViewById(R.id.operation_list_item_account);
            mCategory = (TextView)v.findViewById(R.id.operation_list_item_category);
            mSum = (TextView)v.findViewById(R.id.operation_list_item_sum);
            mType = (ImageView)v.findViewById(R.id.operation_list_item_type);

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.putExtra("Instance", operations.get(getAdapterPosition()));
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
                CashFlowDbManager.getInstance(mFragment.getActivity()).deleteOperation(operations.get(getAdapterPosition()));

                operations.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(), operations.size());
            }
            return false;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OperationListAdapter(Fragment fragment, ArrayList<Operation> operations) {
        this.mFragment = fragment;
        this.operations = operations;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OperationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_operation, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Operation operation = operations.get(position);

        OperationType type = operation.getType();

        String accountName = operation.getAccount()==null ? "" : operation.getAccount().getName();
        String categoryName = operation.getCategory() == null ? "" : operation.getCategory().getName();
        String recipientAccountName = operation.getRecipientAccount()==null ? "" : operation.getRecipientAccount().getName();

        switch (type){
            case IN:
                holder.mCategory.setText(categoryName);
                holder.mType.setImageResource(R.mipmap.operation_type_in);
                break;
            case OUT:
                holder.mCategory.setText(categoryName);
                holder.mType.setImageResource(R.mipmap.operation_type_out);
                break;
            case TRANSFER:
                holder.mCategory.setText(recipientAccountName);
                holder.mType.setImageResource(R.mipmap.operation_type_transfer);
                break;
        }

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        holder.mDate.setText(format.format(operation.getDate()));
        holder.mAccount.setText(accountName);
        holder.mSum.setText(String.format(Locale.getDefault(),"%,d",operation.getSum()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return operations.size();
    }

}
