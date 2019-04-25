package com.epipasha.cashflow.activities.categories;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.Utils;
import com.epipasha.cashflow.activities.DetailActivity;
import com.epipasha.cashflow.data.ViewModelFactory;
import com.epipasha.cashflow.databinding.ActivityCategoryBinding;

public class CategoryActivity extends DetailActivity {

    private static final String EXTRA_CATEGORY_ID = "extraCategoryId";

    private CategoryViewModel model;

    public static void start(FragmentActivity parentActivity, int id){
        Intent intent = new Intent(parentActivity, CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_CATEGORY_ID, id);
        parentActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCategoryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_category);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication()))
                .get(CategoryViewModel.class);

        binding.setViewmodel(model);

        if(savedInstanceState == null) {
            Intent i = getIntent();
            if (i != null && i.hasExtra(EXTRA_CATEGORY_ID)) {
                int mCategoryId = i.getIntExtra(EXTRA_CATEGORY_ID, Utils.EMPTY_ID);
                model.start(mCategoryId);
            }
        }

        model.getShouldClose().observe(this, bool -> {if (bool) finish();});
    }

    @Override
    public void saveObject(){
        model.saveObject();
    }

 }
