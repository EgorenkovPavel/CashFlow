package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.OperationMasterActivity;
import com.epipasha.cashflow.Prefs;
import com.epipasha.cashflow.Utils;
import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.AccountWithBalance;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Date;
import java.util.List;

public class OperationMasterViewModel extends AndroidViewModel {

    private AppDatabase mDb;

    private LiveData<List<AccountWithBalance>> accounts;
    private LiveData<List<Category>> categoriesIn;
    private LiveData<List<Category>> categoriesOut;
    private LiveData<List<AccountWithBalance>> recAccounts;

    private MutableLiveData<OperationType> mOperationType;
    private MutableLiveData<AccountWithBalance> mOperationAccount;
    private MutableLiveData<Category> mOperationCategory;
    private MutableLiveData<AccountWithBalance> mOperationRecAccount;
    private MutableLiveData<Integer> mOperationSum;

    private MutableLiveData<Status> mStatus;

    private Operation operation;

    public OperationMasterViewModel(@NonNull Application application) {
        super(application);

        mDb = AppDatabase.getInstance(application);

        mOperationType = new MutableLiveData<>();
        mOperationType.postValue(OperationType.IN);

        mOperationAccount = new MutableLiveData<>();
        mOperationCategory = new MutableLiveData<>();
        mOperationRecAccount = new MutableLiveData<>();

        mOperationSum = new MutableLiveData<>();
        mOperationSum.postValue(0);

        mStatus = new MutableLiveData<>();

        accounts = mDb.accountDao().loadAllAccountsWithBalance();
        categoriesIn = mDb.categoryDao().loadAllCategoriesByType(OperationType.IN);
        categoriesOut = mDb.categoryDao().loadAllCategoriesByType(OperationType.OUT);

        recAccounts = Transformations.switchMap(mOperationAccount, new Function<AccountWithBalance, LiveData<List<AccountWithBalance>>>() {
            @Override
            public LiveData<List<AccountWithBalance>> apply(AccountWithBalance account) {
                if (account == null) return mDb.accountDao().loadAllAccountsWithBalance();
                return mDb.accountDao().loadAllAccountsWithBalanceExceptId(account.getId());
            }
        });
    }

    public void onOperationTypeChanged(OperationType type){
        this.mOperationType.postValue(type);
    }

    public void onOperationAccountChanged(AccountWithBalance account){
        mOperationAccount.postValue(account);
    }

    public void onOperationCategoryChanged(Category category){
        mOperationCategory.postValue(category);
    }

    public void onOperationAccountRecChanged(AccountWithBalance account){
        mOperationRecAccount.postValue(account);
    }

    public void onDigitPressed(int digit){
        Integer val = mOperationSum.getValue();
        val = val == null ? 0 : val;
        val = val * 10 + digit;
        mOperationSum.postValue(val);
    }

    public void onDeleteDigit(){
        Integer val = mOperationSum.getValue();
        val = val == null ? 0 : val;
        val = val/10;
        mOperationSum.postValue(val);
    }

    public LiveData<List<AccountWithBalance>> getAccounts() {
        return accounts;
    }

    public LiveData<List<Category>> getCategoriesIn() {
        return categoriesIn;
    }

    public LiveData<List<Category>> getCategoriesOut() {
        return categoriesOut;
    }

    public LiveData<List<AccountWithBalance>> getRecAccounts() {
        return recAccounts;
    }

    public LiveData<Integer> getOperationSum() {
        return mOperationSum;
    }

    public LiveData<OperationType> getOperationType() {
        return mOperationType;
    }

    public MutableLiveData<AccountWithBalance> getOperationAccount() {
        return mOperationAccount;
    }

    public MutableLiveData<Category> getOperationCategory() {
        return mOperationCategory;
    }

    public MutableLiveData<AccountWithBalance> getOperationRecAccount() {
        return mOperationRecAccount;
    }

    public void saveOperation(){

        AccountWithBalance account = mOperationAccount.getValue();
        if (account == null){
            mStatus.postValue(Status.EMPTY_ACCOUNT);
            return;
        }

        int accountId = account.getId();
        Integer categoryId = null;
        Integer repAccountId = null;

        OperationType type = mOperationType.getValue();
        if(type == null){
            mStatus.postValue(Status.EMPTY_TYPE);
            return;
        }

        switch (type){
            case IN:case OUT:{
                Category category = mOperationCategory.getValue();
                if(category == null){
                    mStatus.postValue(Status.EMPTY_ANALYTIC);
                    return;
                }
                categoryId = category.getId();
                break;
            }case TRANSFER:{
                AccountWithBalance recAccount = mOperationRecAccount.getValue();
                if(recAccount == null){
                    mStatus.postValue(Status.EMPTY_ANALYTIC);
                    return;
                }
                repAccountId = recAccount.getId();
            }
        }

        Integer sum = mOperationSum.getValue();
        if(sum == null || sum == 0){
            mStatus.postValue(Status.EMPTY_SUM);
            return;
        }

        operation = new Operation(new Date(), type, accountId, categoryId, repAccountId, sum);
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                int operationId = (int)mDb.operationDao().insertOperationWithAnalytic(operation);
                operation.setId(operationId);

                mStatus.postValue(Status.OPERATION_SAVED);
                mOperationSum.postValue(0);
            }
        });

    }

    public void deleteOperation(){
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.operationDao().deleteOperation(operation);
                mStatus.postValue(Status.OPERATION_DELETED);
            }
        });
    }

    public LiveData<Status> getStatus() {
        return mStatus;
    }

    public void loadPrefs(){

        //todo fix loading prefs

        int accountId = Prefs.OperationMasterPrefs.getAccountId(getApplication());

        List<AccountWithBalance> accountList = accounts.getValue();
        if (accountList != null)
            for (AccountWithBalance account:accountList) {
                if (account.getId() == accountId){
                    mOperationAccount.postValue(account);
                }
            }


        //Utils.setPositionById(spinAccount, accountId);

        OperationType type = Prefs.OperationMasterPrefs.getOperationType(getApplication());
        onOperationTypeChanged(type);

//        Utils.setPositionById(spinAnalytic,
//                Prefs.OperationMasterPrefs.getAnalyticId(
//                        getApplication(), type));
    }

    public void savePrefs(){
        OperationType type = mOperationType.getValue();
        if(type != null) {
            Prefs.OperationMasterPrefs.saveOperationType(getApplication(), type);

            switch (type){
                case IN: case OUT: {
                    Category category = mOperationCategory.getValue();
                    if (category != null) {
                        Prefs.OperationMasterPrefs.saveAnalyticId(getApplication(), category.getId(), type);
                    }
                    break;
                }
                case TRANSFER:{
                    AccountWithBalance repAccount = mOperationRecAccount.getValue();
                    if (repAccount != null) {
                        Prefs.OperationMasterPrefs.saveAnalyticId(getApplication(), repAccount.getId(), type);
                    }
                    break;
                }
            }
        }

        AccountWithBalance account = mOperationAccount.getValue();
        if (account != null)
            Prefs.OperationMasterPrefs.saveAccountId(getApplication(), account.getId());

    }

    public enum Status{
        EMPTY_TYPE,
        EMPTY_ANALYTIC,
        EMPTY_ACCOUNT,
        EMPTY_SUM,
        OPERATION_SAVED,
        OPERATION_DELETED
    }

}
