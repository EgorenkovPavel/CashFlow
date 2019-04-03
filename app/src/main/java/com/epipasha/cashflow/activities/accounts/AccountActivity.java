package com.epipasha.cashflow.activities.accounts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.epipasha.cashflow.R;


import com.epipasha.cashflow.Utils;
import com.epipasha.cashflow.activities.ActivityAccountBinding;
import com.epipasha.cashflow.activities.DetailActivity;
import com.epipasha.cashflow.data.ViewModelFactory;

public class AccountActivity extends DetailActivity {

    private static final String EXTRA_ACCOUNT_ID = "extraAccountId";

    private AccountViewModel model;

    public static void start(FragmentActivity parentActivity, int id){
        Intent intent = new Intent(parentActivity, AccountActivity.class);
        intent.putExtra(AccountActivity.EXTRA_ACCOUNT_ID, id);
        parentActivity.startActivity(intent);
    }

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
            int accountId = i.getIntExtra(EXTRA_ACCOUNT_ID, Utils.EMPTY_ID);
            model.start(accountId);
        }else{
            model.start();
        }
    }

    @Override
    public void saveObject() {
        model.saveAccount();
        finish();
    }
}
