package com.epipasha.cashflow.activities.accounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.objects.Account;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AccountViewModel extends AndroidViewModel {

    private final Repository mRepository;
    private final MutableLiveData<Boolean> shouldClose = new MutableLiveData<>(false);

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private MutableLiveData<Integer> activityTitle = new MutableLiveData<>(R.string.new_account);
    private MediatorLiveData<Account> mCurrentAccount = new MediatorLiveData<>();

    public AccountViewModel(@NonNull Application application, Repository repository) {
        super(application);
        mRepository = repository;

        activityTitle.postValue(R.string.new_account);

        mCurrentAccount.addSource(new MutableLiveData<>(new Account()), account ->
                mCurrentAccount.setValue(account));

    }

    public void start(int accountId){

        activityTitle.postValue(R.string.account);

        mCurrentAccount.addSource(mRepository.getAccountById(accountId), account ->
                mCurrentAccount.setValue(account));

    }

    public LiveData<Account> getCurrentAccount() {
        return mCurrentAccount;
    }

    public LiveData<Integer> getActivityTitle() {
        return activityTitle;
    }

    public MutableLiveData<Boolean> getShouldClose() {
        return shouldClose;
    }

    public void saveAccount(){
        Account account = mCurrentAccount.getValue();

        if (account == null){
            return;
        }

        mDisposable.add(mRepository.insertOrUpdateRxAccount(account)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> shouldClose.setValue(true)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
    }
}
