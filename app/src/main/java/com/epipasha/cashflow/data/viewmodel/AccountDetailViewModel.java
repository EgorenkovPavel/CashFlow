package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.Account;

public class AccountDetailViewModel extends AndroidViewModel {

    private static final int DEFAULT_ACCOUNT_ID = -1;

    private AppDatabase mDb;
    private LiveData<Account> mAccount;
    private int mAccountId;

    public AccountDetailViewModel(@NonNull Application application, int id) {
        super(application);

        mAccountId = id;

        mDb = AppDatabase.getInstance(this.getApplication());

        if (mAccountId == DEFAULT_ACCOUNT_ID)
            mAccount = new MutableLiveData<>();
        else
            mAccount = mDb.accountDao().loadAccountById(mAccountId);

    }

    public LiveData<Account> getAccount() {
        return mAccount;
    }

    public void saveAccount(final Account account){
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mAccountId == DEFAULT_ACCOUNT_ID){
                    mDb.accountDao().insertAccount(account);
                }else{
                    account.setId(mAccountId);
                    mDb.accountDao().updateAccount(account);
                }
            }
        });
    }
}
