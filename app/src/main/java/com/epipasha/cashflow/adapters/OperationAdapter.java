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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OperationAdapter extends RecyclerView.Adapter<OperationAdapter.OperationHolder> {

    private final Drawable plus, minus, redo;

    private List<Operation> mOperations;
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
        void onItemLongClickListener(Operation operation, View view);
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

        Operation operation = mOperations.get(position);

        //Set values
        holder.itemView.setTag(operation.getId());

        SimpleDateFormat format = new SimpleDateFormat("MM.yyyy HH:mm", Locale.getDefault());
        holder.operationDateView.setText(format.format(operation.getDate()));

        format.applyPattern("dd");
        holder.tvDateDay.setText(format.format(operation.getDate()));

        format.applyPattern("EEEE");
        holder.tvDayOfWeek.setText(format.format(operation.getDate()));

//        holder.operationAccountView.setText(account);
        holder.operationSumView.setText(String.format(Locale.getDefault(),"%,d",operation.getSum()));

        switch (operation.getType()){
            case IN:{
                //holder.operationCategoryView.setText(category);
                holder.operationTypeImageView.setImageDrawable(plus);
                break;
            }
            case OUT:{
                //holder.operationCategoryView.setText(category);
                holder.operationTypeImageView.setImageDrawable(minus);
                break;
            }
            case TRANSFER:{
                //holder.operationCategoryView.setText(repAccount);
                holder.operationTypeImageView.setImageDrawable(redo);
                break;
            }
        }
    }

    public void setOperations(List<Operation> operations){
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
            Operation operation = mOperations.get(getAdapterPosition());
            mItemClickListener.onItemClickListener(operation.getId());
//
//            int adapterPosition = getAdapterPosition();
//            mCursor.moveToPosition(adapterPosition);
//
//            int idIndex = mCursor.getColumnIndex(OperationEntry._ID);
//            int id = mCursor.getInt(idIndex);
//
//            Intent i = new Intent(mContext, DetailOperationActivity.class);
//
//            Uri uri = OperationEntry.buildOperationUriWithId(id);
//            i.setData(uri);
//            mContext.startActivity(i);
        }

        @Override
        public boolean onLongClick(View view) {

            Operation operation = mOperations.get(getAdapterPosition());
            mItemLongClickListener.onItemLongClickListener(operation, view);
//
//            PopupMenu popupMenu = new PopupMenu(mContext, view);
//            popupMenu.inflate(R.menu.popup_list_item_operation);
//            popupMenu.setOnMenuItemClickListener(this);
//            popupMenu.show();
//
            return true;
        }

    }
}

