package com.epipasha.cashflow;

import android.app.Application;

import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.AccountWithBalance;
import com.epipasha.cashflow.data.entites.CategoryWithCashflow;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainViewModel extends AndroidViewModel {

    private Repository mRepository;
    private LiveData<List<AccountWithBalance>> mAccounts;
    private LiveData<List<CategoryWithCashflow>> mCategories;

    public MainViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date end = cal.getTime();

        mCategories = mRepository.loadAllCategoriesWithCashflow(start, end);
        mAccounts = mRepository.loadAllAccountsWithBalance();
    }

    public LiveData<List<AccountWithBalance>> getAccounts() {
        return mAccounts;
    }

    public LiveData<List<CategoryWithCashflow>> getCategories() {
        return mCategories;
    }
}
