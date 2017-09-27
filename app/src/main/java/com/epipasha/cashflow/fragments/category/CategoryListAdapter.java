package com.epipasha.cashflow.fragments.category;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.MainActivity;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.fragments.ListDetailActivity;
import com.epipasha.cashflow.objects.Category;

import java.util.ArrayList;


public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder>
{
    private final Fragment mFragment;
    private final ArrayList<Category> categories;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
        // each data item is just a string in this case
        public final TextView mCategoryName;
        public final TextView mCategoryType;

        public ViewHolder(View v) {
            super(v);
            mCategoryName = (TextView) v.findViewById(R.id.category_list_item_name);
            mCategoryType = (TextView) v.findViewById(R.id.category_list_item_type);

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.putExtra("Instance", categories.get(getAdapterPosition()));
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
                CashFlowDbManager.getInstance(mFragment.getActivity()).deleteCategory(categories.get(getAdapterPosition()));

                categories.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(), categories.size());
            }
            return false;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoryListAdapter(Fragment fragment, ArrayList<Category> categories) {
        this.mFragment = fragment;
        this.categories = categories;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mCategoryName.setText(categories.get(position).getName());
        holder.mCategoryType.setText(categories.get(position).getType().toString());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categories.size();
    }
}
