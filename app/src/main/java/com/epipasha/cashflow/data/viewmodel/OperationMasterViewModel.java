package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.epipasha.cashflow.Prefs;
import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.AccountWithBalance;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.objects.OperationType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OperationMasterViewModel extends AndroidViewModel {

    private DataSource mRepository;

    private LiveData<List<AccountWithBalance>> accounts;
    private LiveData<List<Category>> categoriesIn;
    private LiveData<List<Category>> categoriesOut;
    private MutableLiveData<List<AccountWithBalance>> recAccounts = new MutableLiveData<>();

    private MutableLiveData<OperationType> mOperationType;

    private MutableLiveData<Status> mStatus;

    private Operation operation;

    private ObservableField<Operation> mOperation = new ObservableField<>(new Operation(new Date(), OperationType.IN, 0,0,0,0));

    public OperationMasterViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        mOperationType = new MutableLiveData<>();
        mOperationType.postValue(OperationType.IN);

        mStatus = new MutableLiveData<>();

        accounts = mRepository.loadAllAccountsWithBalance();
        categoriesIn = mRepository.loadAllCategoriesByType(OperationType.IN);
        categoriesOut = mRepository.loadAllCategoriesByType(OperationType.OUT);
    }

    public ObservableField<Operation> getOperation() {
        return mOperation;
    }

    public void onOperationTypeChanged(OperationType type){
        this.mOperationType.postValue(type);

        mOperation.get().setType(type);
        mOperation.notifyChange();
    }

    public void onDigitPressed(int digit){
        int sum = mOperation.get().getSum();
        sum = sum * 10 + digit;
        mOperation.get().setSum(sum);
        mOperation.notifyChange();
    }

    public void onDeleteDigit(){
        int val = mOperation.get().getSum();
        val = val/10;
        mOperation.get().setSum(val);
        mOperation.notifyChange();
    }

    @BindingAdapter("app:sum")
    public static void onSumChanged(TextView view, int sum){
        view.setText(String.format(Locale.getDefault(),"%,d",sum));
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

    public LiveData<OperationType> getOperationType() {
        return mOperationType;
    }

    public void onAccountSelected(int pos){
        AccountWithBalance account = accounts.getValue().get(pos);

        mOperation.get().setAccountId(account.getId());

        ArrayList<AccountWithBalance> ac = new ArrayList<>(accounts.getValue());
        ac.remove(pos);
        recAccounts.postValue(ac);
    }

    public void onAnalyticSelected(int pos){
        Operation operation = mOperation.get();
        switch (operation.getType()){
            case IN:{
                Category category = categoriesIn.getValue().get(pos);

                operation.setCategoryId(category.getId());
                operation.setRecipientAccountId(null);
                break;
            }
            case OUT:{
                Category category = categoriesOut.getValue().get(pos);

                operation.setCategoryId(category.getId());
                operation.setRecipientAccountId(null);
                break;
            }
            case TRANSFER:{
                AccountWithBalance account = recAccounts.getValue().get(pos);

                operation.setCategoryId(null);
                operation.setRecipientAccountId(account.getId());
                break;
            }
        }
    }

    public void saveOperation(){

        int accountId = mOperation.get().getAccountId();

        Integer categoryId = null;
        Integer repAccountId = null;

        OperationType type = mOperation.get().getType();

        switch (type){
            case IN:case OUT:{
                categoryId = mOperation.get().getCategoryId();
                break;
            }case TRANSFER:{
                repAccountId = mOperation.get().getRecipientAccountId();
                break;
            }
        }

        int sum = mOperation.get().getSum();

        operation = new Operation(new Date(), type, accountId, categoryId, repAccountId, sum);
        mRepository.insertOperation(operation, new DataSource.InsertOperationCallback() {
            @Override
            public void onOperationInsertedSuccess(int id) {
                operation.setId(id);

                mStatus.postValue(Status.OPERATION_SAVED);
                mOperation.get().setSum(0);
            }

            @Override
            public void onOperationInsertedFailed() {

            }
        });

    }

    public void deleteOperation(){
        mRepository.deleteOperation(operation, new DataSource.DeleteOperationCallback() {
            @Override
            public void onOperationDeletedSuccess(int numCol) {
                mStatus.postValue(Status.OPERATION_DELETED);
            }

            @Override
            public void onOperationDeletedFailed() {

            }
        });
    }

    public LiveData<Status> getStatus() {
        return mStatus;
    }

    public void loadPrefs(){

        //todo fix loading prefs

//        int accountId = Prefs.OperationMasterPrefs.getAccountId(getApplication());
//
//        List<AccountWithBalance> accountList = accounts.getValue();
//        if (accountList != null)
//            for (AccountWithBalance account:accountList) {
//                if (account.getId() == accountId){
//                    mOperationAccount.postValue(account);
//                }
//            }
//
//
//        //Utils.setPositionById(spinAccount, accountId);
//
//        OperationType type = Prefs.OperationMasterPrefs.getOperationType(getApplication());
//        onOperationTypeChanged(type);

//        Utils.setPositionById(spinAnalytic,
//                Prefs.OperationMasterPrefs.getAnalyticId(
//                        getApplication(), type));
    }

    public void savePrefs(){
        OperationType type = mOperation.get().getType();
        if(type != null) {
            Prefs.OperationMasterPrefs.saveOperationType(getApplication(), type);

            switch (type){
                case IN: case OUT: {
                    int categoryId = mOperation.get().getCategoryId();
                    Prefs.OperationMasterPrefs.saveAnalyticId(getApplication(), categoryId, type);
                    break;
                }
                case TRANSFER:{
                    int repAccountId = mOperation.get().getRecipientAccountId();
                    Prefs.OperationMasterPrefs.saveAnalyticId(getApplication(), repAccountId, type);
                    break;
                }
            }
        }

        int accountId = mOperation.get().getAccountId();
        Prefs.OperationMasterPrefs.saveAccountId(getApplication(), accountId);

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
