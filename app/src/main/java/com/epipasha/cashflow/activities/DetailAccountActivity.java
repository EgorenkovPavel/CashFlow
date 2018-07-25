package com.epipasha.cashflow.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.viewmodel.AccountDetailViewModel;
import com.epipasha.cashflow.data.viewmodel.ModelFactory;

public class DetailAccountActivity extends DetailActivity {

    public static final String EXTRA_ACCOUNT_ID = "extraAccountId";

    private static final int DEFAULT_ACCOUNT_ID = -1;

    private AccountDetailViewModel model;

    private EditText etTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //todo binding
        //DetailAccountActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_account);
//        User user = new User("Test", "User");
//        binding.setUser(user);

        setContentView(R.layout.activity_detail_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = findViewById(R.id.account_detail_name);

        int accountId = DEFAULT_ACCOUNT_ID;

        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_ACCOUNT_ID)){
            setTitle(getString(R.string.account));
            accountId = i.getIntExtra(EXTRA_ACCOUNT_ID, DEFAULT_ACCOUNT_ID);
        }else
            setTitle(getString(R.string.new_account));

        model = ViewModelProviders.of(this, new ModelFactory(getApplication(), accountId)).get(AccountDetailViewModel.class);
        model.getAccount().observe(this, new Observer<Account>() {
            @Override
            public void onChanged(@Nullable Account account) {
                populateUI(account);
            }
        });

    }

    @Override
    public void saveObject() {
        String title = etTitle.getText().toString();
        Account account = new Account(title);
        model.saveAccount(account);
        finish();
    }

    private void populateUI(Account account) {
        if (account == null) {
            return;
        }

        etTitle.setText(account.getTitle());
    }

}
