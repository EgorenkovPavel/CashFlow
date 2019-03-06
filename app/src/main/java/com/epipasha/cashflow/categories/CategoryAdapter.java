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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder>{

    private static final int TYPE_PARENT = 0;
    private static final int TYPE_ITEM = 1;

    private List<CategoryWithCashflow> mCategories;
    private ItemClickListener mItemClickListener;

    public CategoryAdapter(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category, parent, false);

            return new CategoryAdapter.CategoryHolder(view);
        }else if(viewType == TYPE_PARENT){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_parent_category, parent, false);

            return new CategoryAdapter.CategoryHolder(view);
        }else throw new IllegalArgumentException();
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryHolder holder, int position) {

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
        if(holder.pbBudget != null) {
            holder.pbBudget.setMax(budget);
            holder.pbBudget.setProgress(fact);
        }
        holder.categoryFactView.setText(String.format(Locale.getDefault(), "%,d", fact));
        holder.categoryBudgetView.setText(String.format(Locale.getDefault(), "%,d", budget));
    }

    @Override
    public int getItemCount() {
        if (mCategories == null || mCategories.size() == 0)
            return 0;
        else
            return mCategories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mCategories.get(position).getParentId() == null ? TYPE_PARENT : TYPE_ITEM;
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public void setCategories(List<CategoryWithCashflow> categories){
        //this.mCategories = categories;

        List<CategoryWithCashflow> mParentCategories = new ArrayList<>();
        Map<Integer, List<CategoryWithCashflow>> map = new HashMap<>();
        for (CategoryWithCashflow cat:categories) {
            if(cat.getParentId() == null){
                mParentCategories.add(cat);
            }else{
                List<CategoryWithCashflow> list = map.get(cat.getParentId());
                if(list == null) list = new ArrayList<>();
                list.add(cat);
                map.put(cat.getParentId(), list);
            }
        }

        this.mCategories = new ArrayList<>();
        for (CategoryWithCashflow category:mParentCategories){
            this.mCategories.add(category);
            List<CategoryWithCashflow> list = map.get(category.getId());
            if(list == null) continue;
            this.mCategories.addAll(list);
        }

        notifyDataSetChanged();
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
            int elementId = mCategories.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
