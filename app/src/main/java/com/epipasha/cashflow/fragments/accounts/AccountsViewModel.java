package com.epipasha.cashflow.fragments.accounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.objects.Account;

import java.util.List;

public class AccountsViewModel extends AndroidViewModel {

    private Repository mRepository;
    private LiveData<List<Account>> accounts;

    public AccountsViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        accounts = mRepository.getAllAccountsLive();
    }

    public LiveData<List<Account>> getAccounts() {
        return accounts;
    }
}
