package com.epipasha.cashflow.accounts;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.complex.AccountWithBalance;

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
