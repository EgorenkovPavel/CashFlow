package com.epipasha.cashflow.fragments.operation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.objects.OperationType;
import com.epipasha.cashflow.fragments.Adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OperationAdapter extends Adapter<OperationAdapter.OperationHolder> {

    private final Drawable plus, minus, redo;

    public OperationAdapter(Context mContext) {
        super(mContext);
        this.plus = ContextCompat.getDrawable(mContext, R.drawable.plus);
        this.minus = ContextCompat.getDrawable(mContext, R.drawable.minus);
        this.redo = ContextCompat.getDrawable(mContext, R.drawable.redo);
    }

    @Override
    public OperationAdapter.OperationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_operation, parent, false);

        return new OperationAdapter.OperationHolder(view);
    }

    @Override
    public void onBindViewHolder(OperationAdapter.OperationHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(CashFlowContract.OperationEntry._ID);
        int dateIndex = mCursor.getColumnIndex(CashFlowContract.OperationEntry.COLUMN_DATE);
        int accountIndex = mCursor.getColumnIndex(CashFlowContract.OperationEntry.SERVICE_COLUMN_ACCOUNT_TITLE);
        int categoryIndex = mCursor.getColumnIndex(CashFlowContract.OperationEntry.SERVICE_COLUMN_CATEGORY_TITLE);
        int repAccountIndex = mCursor.getColumnIndex(CashFlowContract.OperationEntry.SERVICE_COLUMN_RECIPIENT_ACCOUNT_TITLE);
        int typeIndex = mCursor.getColumnIndex(CashFlowContract.OperationEntry.COLUMN_TYPE);
        int sumIndex = mCursor.getColumnIndex(CashFlowContract.OperationEntry.COLUMN_SUM);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        Date date = new Date(mCursor.getLong(dateIndex));
        String account = mCursor.getString(accountIndex);
        String category = mCursor.getString(categoryIndex);
        String repAccount = mCursor.getString(repAccountIndex);
        OperationType type = OperationType.toEnum(mCursor.getInt(typeIndex));
        int sum = mCursor.getInt(sumIndex);

        //Set values
        holder.itemView.setTag(id);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        holder.operationDateView.setText(format.format(date));

        holder.operationAccountView.setText(account);
        holder.operationSumView.setText(String.format(Locale.getDefault(),"%,d",sum));

        switch (type){
            case IN:{
                holder.operationCategoryView.setText(category);
                holder.operationTypeImageView.setImageDrawable(plus);
                break;
            }
            case OUT:{
                holder.operationCategoryView.setText(category);
                holder.operationTypeImageView.setImageDrawable(minus);
                break;
            }
            case TRANSFER:{
                holder.operationCategoryView.setText(repAccount);
                holder.operationTypeImageView.setImageDrawable(redo);
                break;
            }
        }
    }

    class OperationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView operationDateView;
        TextView operationAccountView;
        TextView operationCategoryView;
        TextView operationSumView;
        ImageView operationTypeImageView;

        public OperationHolder(View itemView) {
            super(itemView);
            operationDateView = (TextView) itemView.findViewById(R.id.operation_list_item_date);
            operationAccountView = (TextView) itemView.findViewById(R.id.operation_list_item_account);
            operationCategoryView = (TextView) itemView.findViewById(R.id.operation_list_item_category);
            operationSumView = (TextView) itemView.findViewById(R.id.operation_list_item_sum);
            operationTypeImageView = (ImageView) itemView.findViewById(R.id.operation_list_item_type);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            int idIndex = mCursor.getColumnIndex(CashFlowContract.OperationEntry._ID);
            int id = mCursor.getInt(idIndex);

            Intent i = new Intent(mContext, DetailOperationActivity.class);

            Uri uri = CashFlowContract.OperationEntry.buildOperationUriWithId(id);
            i.setData(uri);
            mContext.startActivity(i);
        }
    }
}

