package com.epipasha.cashflow.categories;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.activities.AnalyticActivity;
import com.epipasha.cashflow.data.entites.CategoryWithCashflow;
import com.epipasha.cashflow.data.ViewModelFactory;

import java.util.List;

public class CategoriesFragment extends Fragment implements CategoryAdapter.HeaderClickListener, CategoryAdapter.ItemClickListener {

    private RecyclerView rvList;
    private CategoryAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        rvList = v.findViewById(R.id.rvList);

        initRecycledView();

        retrieveItems();

        return v;
    }

    private void initRecycledView(){

        rvList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvList.getContext(),
                layoutManager.getOrientation());
        rvList.addItemDecoration(mDividerItemDecoration);

        mAdapter = new CategoryAdapter(this, this);
        rvList.setAdapter(mAdapter);

    }

    private void retrieveItems() {

        CategoriesViewModel viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getActivity().getApplication())).get(CategoriesViewModel.class);
        viewModel.getCategories().observe(this, new Observer<List<CategoryWithCashflow>>() {
            @Override
            public void onChanged(@Nullable List<CategoryWithCashflow> categories) {
                mAdapter.setCategories(categories);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_CATEGORY_ID, itemId);
        startActivity(intent);
    }

    @Override
    public void onHeaderClickListener() {
        Intent i = new Intent(getActivity(), AnalyticActivity.class);
        startActivity(i);
    }
}
