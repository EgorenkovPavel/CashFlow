package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.entites.AccountWithBalance;

import java.util.List;

public class AccountListViewModel extends AndroidViewModel {

    private LiveData<List<AccountWithBalance>> accounts;

    public AccountListViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(this.getApplication());
        accounts = db.accountDao().loadAllAccountsWithBalance();
    }

    public LiveData<List<AccountWithBalance>> getAccounts() {
        return accounts;
    }
}
