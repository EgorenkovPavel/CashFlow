package com.epipasha.cashflow.activities.accounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.AccountEntity;
import com.epipasha.cashflow.data.objects.Account;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AccountViewModel extends AndroidViewModel {

    private final Repository mRepository;
    private final MutableLiveData<Boolean> shouldClose = new MutableLiveData<>(false);

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private ObservableField<Account> mAccount = new ObservableField<>();
    private ObservableInt activityTitle = new ObservableInt(R.string.new_account);

    public AccountViewModel(@NonNull Application application, Repository repository) {
        super(application);
        mRepository = repository;
        mAccount.set(new Account());
    }

    public void start(int accountId){
        mDisposable.add(mRepository.getAccountById(accountId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(account -> {mAccount.set(account);
                            activityTitle.set(R.string.account);},
                        throwable -> {}));

    }

    public ObservableInt getActivityTitle() {
        return activityTitle;
    }

    public ObservableField<Account> getAccount() {
        return mAccount;
    }

    public MutableLiveData<Boolean> getShouldClose() {
        return shouldClose;
    }

    public void saveAccount(){
        Account account = mAccount.get();

        if (account == null){
            return;
        }

        mDisposable.add(mRepository.insertOrUpdateAccount(account)
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
