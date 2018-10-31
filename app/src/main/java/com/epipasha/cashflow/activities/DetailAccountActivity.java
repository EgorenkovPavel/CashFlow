package com.epipasha.cashflow.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.viewmodel.AccountDetailViewModel;
import com.epipasha.cashflow.viewmodel.ViewModelFactory;

public class DetailAccountActivity extends DetailActivity {

    public static final String EXTRA_ACCOUNT_ID = "extraAccountId";

    private static final int DEFAULT_ACCOUNT_ID = -1;

    private AccountDetailViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAccountBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_account);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication()))
                .get(AccountDetailViewModel.class);

        binding.setViewmodel(model);

        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_ACCOUNT_ID)){
            int accountId = i.getIntExtra(EXTRA_ACCOUNT_ID, DEFAULT_ACCOUNT_ID);
            model.start(accountId);
        }
    }

    @Override
    public void saveObject() {
        model.saveAccount();
        finish();
    }
}
