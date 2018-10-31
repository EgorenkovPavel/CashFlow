package com.epipasha.cashflow.accounts;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.Account;

public class AccountViewModel extends AndroidViewModel {

    private final DataSource mRepository;

    private ObservableBoolean isNew = new ObservableBoolean(true);
    private ObservableField<Account> mAccount = new ObservableField<>();

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
                isNew.set(false);
            }

            @Override
            public void onDataNotAvailable() {
                mAccount.set(new Account());
            }
        });
    }

    public ObservableBoolean getIsNew() {
        return isNew;
    }

    public ObservableField<Account> getAccount() {
        return mAccount;
    }

    public void saveAccount(){
        Account account = mAccount.get();

        if (account == null){
            return;
        }

        if(isNew.get()){
            mRepository.insertAccount(account);
        }else{
            mRepository.updateAccount(account);
        }
    }

}
