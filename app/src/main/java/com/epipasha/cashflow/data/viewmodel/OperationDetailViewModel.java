package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.entites.OperationWithData;
import com.epipasha.cashflow.objects.OperationType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OperationDetailViewModel extends AndroidViewModel {

    private final static int DEFAULT_ID = -1;
    private int mOperationId = DEFAULT_ID;

    private AppDatabase mDb;
    private LiveData<OperationWithData> mOperation;
    private LiveData<List<Account>> mAccounts;
    private LiveData<List<Category>> mCategoriesIn;
    private LiveData<List<Category>> mCategoriesOut;
    private LiveData<List<Account>> mRecAccounts;

    private MutableLiveData<Date> mOperationDate;
    private MutableLiveData<OperationType> mOperationType;
    private MutableLiveData<Integer> mOperationAccountId;
    private MutableLiveData<Integer> mOperationCategoryId;
    private MutableLiveData<Integer> mOperationRecAccountId;
    private MutableLiveData<Integer> mOperationSum;
    private MutableLiveData<Status> mStatus;

    public OperationDetailViewModel(@NonNull Application application, int operationId) {
        super(application);

        mOperationDate = new MutableLiveData<>();
        mOperationDate.postValue(Calendar.getInstance().getTime());

        mOperationType = new MutableLiveData<>();
        mOperationType.postValue(OperationType.IN);

        mOperationAccountId = new MutableLiveData<>();
        mOperationCategoryId = new MutableLiveData<>();
        mOperationRecAccountId = new MutableLiveData<>();
        mOperationSum = new MutableLiveData<>();
        mOperationSum.postValue(0);

        mOperationId = operationId;

        mStatus = new MutableLiveData<>();

        mDb = AppDatabase.getInstance(application);

        mAccounts = mDb.accountDao().loadAllAccounts();
        mCategoriesIn = mDb.categoryDao().loadAllCategoriesByType(OperationType.IN);
        mCategoriesOut = mDb.categoryDao().loadAllCategoriesByType(OperationType.OUT);
        mRecAccounts = Transformations.switchMap(mOperationAccountId, new Function<Integer, LiveData<List<Account>>>() {
            @Override
            public LiveData<List<Account>> apply(Integer accountId) {
                if (accountId == null) return mDb.accountDao().loadAllAccounts();
                return mDb.accountDao().loadAllAccountsExceptId(accountId);
            }
        });

        if(mOperationId == DEFAULT_ID){
            mOperation = new MutableLiveData<>();
        }else{
            mOperation = mDb.operationDao().loadOperationWithDataById(mOperationId);
        }
    }

    public MutableLiveData<Status> getStatus() {
        return mStatus;
    }

    public void setOperationDate(Date operationDate) {
        mOperationDate.postValue(operationDate);
    }

    public void setOperationType(OperationType operationType) {
        mOperationType.postValue(operationType);
    }

    public void setOperationAccount(Account operationAccount) {
        mOperationAccountId.postValue(operationAccount.getId());
    }

    public void setOperationCategory(Category operationCategory) {
        if(operationCategory == null)
            mOperationCategoryId.postValue(null);
        else
            mOperationCategoryId.postValue(operationCategory.getId());
    }

    public void setOperationRecAccount(Account operationRecAccount) {
        if(operationRecAccount == null)
            mOperationRecAccountId.postValue(null);
        else
            mOperationRecAccountId.postValue(operationRecAccount.getId());
    }

    public void setOperationSum(Integer operationSum) {
        mOperationSum.postValue(operationSum);
    }

    public MutableLiveData<Integer> getOperationAccountId() {
        return mOperationAccountId;
    }

    public MutableLiveData<Integer> getOperationCategoryId() {
        return mOperationCategoryId;
    }

    public MutableLiveData<Integer> getOperationRecAccountId() {
        return mOperationRecAccountId;
    }

    public MutableLiveData<Date> getOperationDate() {
        return mOperationDate;
    }

    public MutableLiveData<OperationType> getOperationType() {
        return mOperationType;
    }

    public MutableLiveData<Integer> getOperationSum() {
        return mOperationSum;
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

    public void saveObject(){

        Integer sum = mOperationSum.getValue();

        if(sum==null||sum==0){
            mStatus.postValue(Status.EMPTY_SUM);
            return;
        }

        Integer accountId = mOperationAccountId.getValue();
        if(accountId == null){
            mStatus.postValue(Status.EMPTY_ACCOUNT);
            return;
        }

        Integer categoryId = null;
        Integer recAccountId = null;

        Date date = mOperationDate.getValue();
        OperationType type = mOperationType.getValue();
        if (type == null){
            return;
        }

        switch (type){
            case IN: case OUT:{
                categoryId = mOperationCategoryId.getValue();
                if(categoryId == null){
                    mStatus.postValue(Status.EMPTY_ANALYTIC);
                    return;
                }
                break;
            }
            case TRANSFER:{
                recAccountId = mOperationRecAccountId.getValue();
                if(recAccountId == null){
                    mStatus.postValue(Status.EMPTY_ANALYTIC);
                    return;
                }
                break;
            }
        }

        final Operation operation = new Operation(date, type, accountId, categoryId, recAccountId, sum);

        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mOperationId == DEFAULT_ID){
                    mDb.operationDao().insertOperationWithAnalytic(operation);
                }else{
                    operation.setId(mOperationId);
                    mDb.operationDao().updateOperationWithAnalytic(operation);
                }
                mStatus.postValue(Status.OPERATION_SAVED);
            }
        });
    }

    public enum Status{
        EMPTY_SUM,
        EMPTY_ACCOUNT,
        EMPTY_ANALYTIC,
        OPERATION_SAVED
    }
}
