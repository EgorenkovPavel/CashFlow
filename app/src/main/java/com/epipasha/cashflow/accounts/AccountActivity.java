package com.epipasha.cashflow.accounts;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.epipasha.cashflow.R;


import com.epipasha.cashflow.activities.ActivityAccountBinding;
import com.epipasha.cashflow.activities.DetailActivity;
import com.epipasha.cashflow.data.ViewModelFactory;

public class AccountActivity extends DetailActivity {

    public static final String EXTRA_ACCOUNT_ID = "extraAccountId";

    private static final int DEFAULT_ACCOUNT_ID = -1;

    private AccountViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAccountBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_account);

        setSupportActionBar(binding.toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication()))
                .get(AccountViewModel.class);

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
