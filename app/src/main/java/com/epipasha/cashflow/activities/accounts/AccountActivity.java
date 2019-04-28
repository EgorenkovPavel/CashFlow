package com.epipasha.cashflow.activities.accounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.Utils;
import com.epipasha.cashflow.activities.ActivityAccountBinding;
import com.epipasha.cashflow.activities.DetailActivity;
import com.epipasha.cashflow.data.ViewModelFactory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

public class AccountActivity extends DetailActivity {

    private static final String EXTRA_ACCOUNT_ID = "extraAccountId";

    private AccountViewModel model;

    public static void start(Context context, int id){
        Intent intent = new Intent(context, AccountActivity.class);
        intent.putExtra(AccountActivity.EXTRA_ACCOUNT_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAccountBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_account);

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication()))
                .get(AccountViewModel.class);

        binding.setViewmodel(model);

        if(savedInstanceState == null) {
            Intent i = getIntent();
            if (i != null && i.hasExtra(EXTRA_ACCOUNT_ID)) {
                int accountId = i.getIntExtra(EXTRA_ACCOUNT_ID, Utils.EMPTY_ID);
                model.start(accountId);
            }
        }

        model.getShouldClose().observe(this, bool -> {
            if (bool) finish();
        });
    }

    @Override
    public void saveObject(){
        model.saveAccount();
    }
}
