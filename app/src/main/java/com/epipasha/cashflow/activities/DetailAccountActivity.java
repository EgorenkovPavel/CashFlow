package com.epipasha.cashflow.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.Account;

public class DetailAccountActivity extends DetailActivity {

    public static final String EXTRA_ACCOUNT_ID = "extraAccountId";

    private static final int DEFAULT_ACCOUNT_ID = -1;

    private int mAccountId = DEFAULT_ACCOUNT_ID;

    private AppDatabase mDb;

    private EditText etTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = (EditText)findViewById(R.id.account_detail_name);

        mDb = AppDatabase.getInstance(getApplicationContext());

        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_ACCOUNT_ID)){
            setTitle(getString(R.string.account));
            mAccountId = i.getIntExtra(EXTRA_ACCOUNT_ID, DEFAULT_ACCOUNT_ID);
            AppExecutors.getInstance().discIO().execute(new Runnable() {
                @Override
                public void run() {
                    final LiveData<Account> account = mDb.accountDao().loadAccountById(mAccountId);
                    account.observe(DetailAccountActivity.this, new Observer<Account>() {
                        @Override
                        public void onChanged(@Nullable Account account) {
                            populateUI(account);
                        }
                    });
                }
            });
        }else
            setTitle(getString(R.string.new_account));
    }

    @Override
    public void saveObject() {
        String title = etTitle.getText().toString();

        final Account account = new Account(title);
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mAccountId == DEFAULT_ACCOUNT_ID){
                    mDb.accountDao().insertAccount(account);
                }else{
                    account.setId(mAccountId);
                    mDb.accountDao().updateAccount(account);
                }
                finish();
            }
        });
    }

    private void populateUI(Account account) {
        if (account == null) {
            return;
        }

        etTitle.setText(account.getTitle());
    }

}
