package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.Account;

public class AccountDetailViewModel extends AndroidViewModel implements DataSource.GetAccountCallback {

    private final DataSource mRepository;

    private ObservableBoolean isNew = new ObservableBoolean(true);
    private ObservableField<Account> mAccount = new ObservableField<>();

    public AccountDetailViewModel(@NonNull Application application, Repository repository) {
        super(application);
        mRepository = repository;
        mAccount.set(new Account(""));
    }

    public void start(int accountId){
        mRepository.getAccountById(accountId, this);
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

    @Override
    public void onAccountLoaded(Account account) {
        mAccount.set(account);
        isNew.set(false);
    }

    @Override
    public void onDataNotAvailable() {
        //mAccount.postValue(null);
    }
}
