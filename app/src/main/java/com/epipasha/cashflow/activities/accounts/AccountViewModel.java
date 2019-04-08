package com.epipasha.cashflow.activities.accounts;

import android.app.Application;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.annotation.NonNull;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.Account;

public class AccountViewModel extends AndroidViewModel {

    private final DataSource mRepository;

    private ObservableField<Account> mAccount = new ObservableField<>();
    private ObservableInt activityTitle = new ObservableInt(R.string.new_account);

    public AccountViewModel(@NonNull Application application, Repository repository) {
        super(application);
        mRepository = repository;
        mAccount.set(new Account());
    }

    public void start(int accountId){
        mRepository.getAccountById(accountId, new DataSource.GetAccountCallback() {
            @Override
            public void onAccountLoaded(Account account) {
                mAccount.set(account);
                activityTitle.set(R.string.account);
            }

            @Override
            public void onDataNotAvailable() {
            }
        });
    }

    public ObservableInt getActivityTitle() {
        return activityTitle;
    }

    public ObservableField<Account> getAccount() {
        return mAccount;
    }

    public void saveAccount(){
        Account account = mAccount.get();

        if (account == null){
            return;
        }

        mRepository.insertAccount(account);
    }
}
