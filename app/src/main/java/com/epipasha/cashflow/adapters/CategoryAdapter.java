package com.epipasha.cashflow.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.activities.DetailCategoryActivity;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Locale;

public class CategoryAdapter extends HeaderAdapter<CategoryAdapter.HeaderHolder, CategoryAdapter.CategoryHolder>{

    public CategoryAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected void onBindItemViewHolder(CategoryHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry._ID);
        int titleIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TITLE);
        int typeIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TYPE);
        int budgetIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_BUDGET);
        int factIndex = mCursor.getColumnIndex(CashFlowContract.CategoryCostEntry.COLUMN_SUM);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        OperationType type = OperationType.toEnum(mCursor.getInt(typeIndex));
        int budget = mCursor.getInt(budgetIndex);
        int fact = mCursor.getInt(factIndex);

        int delta = 0;
        if (type.equals(OperationType.IN)) {
            delta = fact - budget;
        } else if (type.equals(OperationType.OUT)) {
            delta = budget - fact;
        }

        //Set values
        holder.itemView.setTag(id);
        holder.categoryTitleView.setText(title);
        holder.pbBudget.setMax(budget);
        holder.pbBudget.setProgress(fact);
        holder.tvProgressLabel.setText(String.format(Locale.getDefault(), "%,d", fact) + " / " + String.format(Locale.getDefault(), "%,d", budget));
        holder.categoryDeltaView.setText(String.format(Locale.getDefault(), "%,d", delta));

    }

    @Override
    protected void onBindHeaderViewHolder(HeaderHolder holder) {

        int idIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry._ID);
        int titleIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TITLE);
        int typeIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TYPE);
        int budgetIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_BUDGET);
        int factIndex = mCursor.getColumnIndex(CashFlowContract.CategoryCostEntry.COLUMN_SUM);

        int inBudget = 0;
        int inFact = 0;
        int inDelta = 0;
        int outBudget = 0;
        int outFact = 0;
        int outDelta = 0;

        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()){
            OperationType type = OperationType.toEnum(mCursor.getInt(typeIndex));
            switch (type){
                case IN:{
                    inBudget += mCursor.getInt(budgetIndex);
                    inFact += mCursor.getInt(factIndex);
                    break;
                }
                case OUT:{
                    outBudget += mCursor.getInt(budgetIndex);
                    outFact += mCursor.getInt(factIndex);
                    break;
                }
            }
            mCursor.moveToNext();
        }

        inDelta = inFact - inBudget;
        outDelta = outBudget - outFact;

        ((CategoryAdapter.HeaderHolder)holder).tvInBudget.setText(String.format(Locale.getDefault(), "%,d", inBudget));
        ((CategoryAdapter.HeaderHolder)holder).tvInFact.setText(String.format(Locale.getDefault(), "%,d", inFact));
        ((CategoryAdapter.HeaderHolder)holder).tvInDelta.setText(String.format(Locale.getDefault(), "%,d", inDelta));

        ((CategoryAdapter.HeaderHolder)holder).tvOutBudget.setText(String.format(Locale.getDefault(), "%,d", outBudget));
        ((CategoryAdapter.HeaderHolder)holder).tvOutFact.setText(String.format(Locale.getDefault(), "%,d", outFact));
        ((CategoryAdapter.HeaderHolder)holder).tvOutDelta.setText(String.format(Locale.getDefault(), "%,d", outDelta));

        ((CategoryAdapter.HeaderHolder)holder).tvCashflow.setText(String.format(Locale.getDefault(), "%,d", inFact - outFact));

    }

    @Override
    protected CategoryHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_category, parent, false);

        return new CategoryAdapter.CategoryHolder(view);
    }

    @Override
    protected HeaderHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_category_header, parent, false);

        return new CategoryAdapter.HeaderHolder(view);
    }

    class HeaderHolder extends RecyclerView.ViewHolder{

        TextView tvInBudget, tvInFact, tvInDelta;
        TextView tvOutBudget, tvOutFact, tvOutDelta;
        TextView tvCashflow;

        public HeaderHolder(View itemView) {
            super(itemView);

            tvInBudget = (TextView) itemView.findViewById(R.id.tvInBudget);
            tvInFact = (TextView) itemView.findViewById(R.id.tvInFact);
            tvInDelta = (TextView) itemView.findViewById(R.id.tvInDelta);

            tvOutBudget = (TextView) itemView.findViewById(R.id.tvOutBudget);
            tvOutFact = (TextView) itemView.findViewById(R.id.tvOutFact);
            tvOutDelta = (TextView) itemView.findViewById(R.id.tvOutDelta);

            tvCashflow = (TextView) itemView.findViewById(R.id.tvCashflow);
        }

    }

    class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView categoryTitleView;
        TextView categoryDeltaView;
        ProgressBar pbBudget;
        TextView tvProgressLabel;


        public CategoryHolder(View itemView) {
            super(itemView);
            categoryTitleView = (TextView) itemView.findViewById(R.id.tv_category);
            categoryDeltaView = (TextView) itemView.findViewById(R.id.tvInDelta);
            pbBudget = itemView.findViewById(R.id.pb_budget);
            tvProgressLabel = itemView.findViewById(R.id.tv_progress_lbl);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition()-1;
            mCursor.moveToPosition(adapterPosition);

            int idIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry._ID);
            int id = mCursor.getInt(idIndex);

            Intent i = new Intent(mContext, DetailCategoryActivity.class);

            Uri uri = CashFlowContract.CategoryEntry.buildCategoryUriWithId(id);
            i.setData(uri);
            mContext.startActivity(i);
        }
    }
}
