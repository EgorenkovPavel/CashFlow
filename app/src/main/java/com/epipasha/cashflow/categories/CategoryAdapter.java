package com.epipasha.cashflow.categories;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.entites.CategoryWithCashflow;
import com.epipasha.cashflow.objects.OperationType;

import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    protected static final int HEADER_ITEM = 234;
    protected static final int LIST_ITEM = 897;

    private List<CategoryWithCashflow> mCategories;
    private ItemClickListener mItemClickListener;
    private HeaderClickListener mHeaderClickListener;

    public CategoryAdapter(HeaderClickListener headerClickListener, ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
        mHeaderClickListener = headerClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {

            case HEADER_ITEM: {
                return onCreateHeaderViewHolder(parent);
            }

            case LIST_ITEM: {
                return onCreateItemViewHolder(parent);
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            onBindHeaderViewHolder((HeaderHolder) holder);
        } else {
            onBindItemViewHolder((CategoryHolder) holder, position - 1);
        }
    }

    @Override
    public int getItemCount() {
        if (mCategories == null || mCategories.size() == 0)
            return 0;
        else
            return mCategories.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return HEADER_ITEM;
        } else {
            return LIST_ITEM;
        }
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public interface HeaderClickListener {
        void onHeaderClickListener();
    }

    public void setCategories(List<CategoryWithCashflow> categories){
        this.mCategories = categories;
        notifyDataSetChanged();
    }

    private void onBindItemViewHolder(CategoryHolder holder, int position) {

        CategoryWithCashflow category = mCategories.get(position);

        // Determine the values of the wanted data
        final int id = category.getId();
        String title = category.getTitle();
        OperationType type = category.getType();
        int budget = category.getBudget();
        int fact = category.getCashflow();

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
        holder.categoryFactView.setText(String.format(Locale.getDefault(), "%,d", fact));
        holder.categoryBudgetView.setText(String.format(Locale.getDefault(), "%,d", budget));
    }

    private void onBindHeaderViewHolder(HeaderHolder holder) {

        int inBudget = 0;
        int inFact = 0;
        int outBudget = 0;
        int outFact = 0;

        for (CategoryWithCashflow category: mCategories) {

            OperationType type = category.getType();
            switch (type){
                case IN:{
                    inBudget += category.getBudget();
                    inFact += category.getCashflow();
                    break;
                }
                case OUT:{
                    outBudget += category.getBudget();
                    outFact += category.getCashflow();
                    break;
                }
            }
        }

        holder.pbIn.setMax(inBudget);
        holder.pbIn.setProgress(inFact);
        holder.lblIn.setText(String.format(Locale.getDefault(), "%,d / %,d", inFact, inBudget));

        holder.pbOut.setMax(outBudget);
        holder.pbOut.setProgress(outFact);
        holder.lblOut.setText(String.format(Locale.getDefault(), "%,d / %,d", outFact, outBudget));

        holder.tvCashflow.setText(String.format(Locale.getDefault(), "%,d", inFact - outFact));

    }

    private CategoryHolder onCreateItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);

        return new CategoryAdapter.CategoryHolder(view);
    }

    private HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_header, parent, false);

        return new CategoryAdapter.HeaderHolder(view);
    }

    class HeaderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ProgressBar pbIn, pbOut;
        TextView lblIn, lblOut;
        TextView tvCashflow;

        public HeaderHolder(View itemView) {
            super(itemView);

            pbIn = itemView.findViewById(R.id.pb_in);
            pbOut = itemView.findViewById(R.id.pb_out);

            lblIn = itemView.findViewById(R.id.tv_progress_in_lbl);
            lblOut = itemView.findViewById(R.id.tv_progress_out_lbl);

            tvCashflow = itemView.findViewById(R.id.tvCashflow);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mHeaderClickListener.onHeaderClickListener();
        }
    }

    class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView categoryTitleView;
        TextView categoryFactView;
        TextView categoryBudgetView;
        ProgressBar pbBudget;

        public CategoryHolder(View itemView) {
            super(itemView);
            categoryTitleView = itemView.findViewById(R.id.lbl_in);
            categoryFactView = itemView.findViewById(R.id.tvFact);
            categoryBudgetView = itemView.findViewById(R.id.tvBudget);
            pbBudget = itemView.findViewById(R.id.pb_budget);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mCategories.get(getAdapterPosition() - 1).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
