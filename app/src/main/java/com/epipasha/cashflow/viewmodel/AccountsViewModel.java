package com.epipasha.cashflow.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.AccountWithBalance;

import java.util.List;

public class AccountsViewModel extends AndroidViewModel {

    private Repository mRepository;
    private LiveData<List<AccountWithBalance>> accounts;

    public AccountsViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        accounts = mRepository.loadAllAccountsWithBalance();
    }

    public LiveData<List<AccountWithBalance>> getAccounts() {
        return accounts;
    }
}
