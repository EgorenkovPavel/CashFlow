package com.epipasha.cashflow.fragments.category;

import static android.app.Activity.RESULT_CANCELED;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.fragments.ListDetailActivity;
import com.epipasha.cashflow.fragments.ListFragment;
import com.epipasha.cashflow.objects.Category;

import java.util.ArrayList;

public class CategoryFragment extends ListFragment<Category> {

    @Override
    public void addInstance() {
        Intent i = new Intent();
        i.putExtra("Instance", new Category());
        i.putExtra("Position", -1);
        i.setClass(getActivity(), ListDetailActivity.class);
        startActivityForResult(i, RESULT_CANCELED);
    }

    @Override
    protected RecyclerView.Adapter getAdapter(ListFragment fragment, ArrayList<Category> list) {
        return new CategoryListAdapter(fragment, list);
    }

    @Override
    protected ArrayList<Category> getList() {
        return CashFlowDbManager.getInstance(getActivity()).getCategories();
    }

}
