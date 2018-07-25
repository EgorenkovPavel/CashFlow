package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.entites.OperationWithData;
import com.epipasha.cashflow.objects.OperationType;

import java.util.ArrayList;
import java.util.List;

public class OperationDetailViewModel extends AndroidViewModel {

    private final static int DEFAULT_ID = -1;
    private int mOperationId = DEFAULT_ID;
    private AppDatabase mDb;
    private LiveData<OperationWithData> mOperation;
    private LiveData<List<Account>> mAccounts;
    private LiveData<List<Category>> mCategoriesIn;
    private LiveData<List<Category>> mCategoriesOut;
    private MutableLiveData<List<Account>> mRecAccounts;

    public OperationDetailViewModel(@NonNull Application application, int operationId) {
        super(application);

        mOperationId = operationId;

        mDb = AppDatabase.getInstance(application);

        mAccounts = mDb.accountDao().loadAllAccounts();
        mCategoriesIn = mDb.categoryDao().loadAllCategoriesByType(OperationType.IN);
        mCategoriesOut = mDb.categoryDao().loadAllCategoriesByType(OperationType.OUT);
        mRecAccounts = new MutableLiveData<>();

        if(mOperationId == DEFAULT_ID){
            mOperation = new MutableLiveData<>();
        }else{
            mOperation = mDb.operationDao().loadOperationWithDataById(mOperationId);
        }
    }

    public void setSelectedAccount(Account account){

        List<Account> accounts = new ArrayList<>();
        for (Account mAccount:mAccounts.getValue()) {
            if(mAccount == account){
                continue;
            }
            accounts.add(mAccount);
        }

        mRecAccounts.postValue(accounts);
    }

    public LiveData<List<Account>> getRecAccounts(){
        return mRecAccounts;
    }

    public LiveData<OperationWithData> getOperationWithData() {
        return mOperation;
    }

    public LiveData<List<Account>> getAccounts() {
        return mAccounts;
    }

    public LiveData<List<Category>> getCategoriesIn() {
        return mCategoriesIn;
    }

    public LiveData<List<Category>> getCategoriesOut() {
        return mCategoriesOut;
    }

    public void saveObject(final Operation operation){
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mOperationId == DEFAULT_ID){
                    mDb.operationDao().insertOperationWithAnalytic(operation);
                }else{
                    operation.setId(mOperationId);
                    mDb.operationDao().updateOperationWithAnalytic(operation);
                }
            }
        });
    }
}
