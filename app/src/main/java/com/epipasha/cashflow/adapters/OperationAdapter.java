package com.epipasha.cashflow.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.entites.OperationWithData;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OperationAdapter extends RecyclerView.Adapter<OperationAdapter.OperationHolder> {

    private final Drawable plus, minus, redo;

    private List<OperationWithData> mOperations;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;

    public OperationAdapter(Context context, ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener) {
        this.plus = ContextCompat.getDrawable(context, R.drawable.ic_plus);
        this.minus = ContextCompat.getDrawable(context, R.drawable.ic_minus);
        this.redo = ContextCompat.getDrawable(context, R.drawable.ic_redo);

        mItemClickListener = itemClickListener;
        mItemLongClickListener = itemLongClickListener;
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public interface ItemLongClickListener {
        void onItemLongClickListener(int operationId, View view);
    }

    @NonNull
    @Override
    public OperationAdapter.OperationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_operation, parent, false);

        return new OperationAdapter.OperationHolder(view);
    }

    @Override
    public int getItemCount() {
        return mOperations == null ? 0 : mOperations.size();
    }

    @Override
    public void onBindViewHolder(OperationAdapter.OperationHolder holder, int position) {

        OperationWithData operation = mOperations.get(position);

        //Set values
        holder.itemView.setTag(operation.getId());

        SimpleDateFormat format = new SimpleDateFormat("MM.yyyy HH:mm", Locale.getDefault());
        holder.operationDateView.setText(format.format(operation.getDate()));

        format.applyPattern("dd");
        holder.tvDateDay.setText(format.format(operation.getDate()));

        format.applyPattern("EEEE");
        holder.tvDayOfWeek.setText(format.format(operation.getDate()));

        holder.operationAccountView.setText(operation.getAccount().getTitle());
        holder.operationSumView.setText(String.format(Locale.getDefault(),"%,d",operation.getSum()));

        switch (operation.getType()){
            case IN:{
                holder.operationCategoryView.setText(operation.getCategory().getTitle());
                holder.operationTypeImageView.setImageDrawable(plus);
                break;
            }
            case OUT:{
                holder.operationCategoryView.setText(operation.getCategory().getTitle());
                holder.operationTypeImageView.setImageDrawable(minus);
                break;
            }
            case TRANSFER:{
                holder.operationCategoryView.setText(operation.getRepAccount().getTitle());
                holder.operationTypeImageView.setImageDrawable(redo);
                break;
            }
        }
    }

    public void setOperations(List<OperationWithData> operations){
        this.mOperations = operations;
        notifyDataSetChanged();
    }

    class OperationHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView operationDateView;
        TextView operationAccountView;
        TextView operationCategoryView;
        TextView operationSumView;
        ImageView operationTypeImageView;
        TextView tvDateDay;
        TextView tvDayOfWeek;

        public OperationHolder(View itemView) {
            super(itemView);
            operationDateView = itemView.findViewById(R.id.operation_list_item_date);
            operationAccountView = itemView.findViewById(R.id.operation_list_item_account);
            operationCategoryView = itemView.findViewById(R.id.operation_list_item_category);
            operationSumView = itemView.findViewById(R.id.operation_list_item_sum);
            operationTypeImageView = itemView.findViewById(R.id.operation_list_item_type);
            tvDateDay = itemView.findViewById(R.id.tv_date_day);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            OperationWithData operation = mOperations.get(getAdapterPosition());
            mItemClickListener.onItemClickListener(operation.getId());
        }

        @Override
        public boolean onLongClick(View view) {

            OperationWithData operation = mOperations.get(getAdapterPosition());
            mItemLongClickListener.onItemLongClickListener(operation.getId(), view);
            return true;
        }

    }
}

