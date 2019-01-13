package com.epipasha.cashflow.operations;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.epipasha.cashflow.Prefs;
import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.Account;
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

    private MutableLiveData<Integer> selectedAccount = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedInCategory = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedOutCategory = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedRepAccount = new MutableLiveData<>();

    private MutableLiveData<OperationType> mOperationType = new MutableLiveData<>();

    private MutableLiveData<Status> mStatus;

    private Operation operation;

    private ObservableField<Operation> mOperation = new ObservableField<>(new Operation(new Date(), OperationType.IN, 0,0,0,0));

    public OperationMasterViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

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

    public LiveData<OperationType> getOperationType() {
        return mOperationType;
    }

    public void selectAccount(AccountWithBalance account){
        selectedAccount.setValue(account.getId());

        Integer repAccountId = selectedRepAccount.getValue();
        if(repAccountId != null && repAccountId == account.getId())
            selectedRepAccount.setValue(null);
    }

    public void selectInCategory(Category category){
        selectedInCategory.setValue(category.getId());
    }

    public void selectOutCategory(Category category){
        selectedOutCategory.setValue(category.getId());
    }

    public void selectRepAccount(AccountWithBalance account){
        Integer accountId = selectedAccount.getValue();
        if(accountId == null || accountId != account.getId())
            selectedRepAccount.setValue(account.getId());
    }

    public void saveOperation(){

        Integer accountId = selectedAccount.getValue();
        Integer categoryInId = selectedInCategory.getValue();
        Integer categoryOutId = selectedOutCategory.getValue();
        Integer repAccountId = selectedRepAccount.getValue();

        OperationType type = mOperation.get().getType();
        Integer categoryId = null;

        switch (type){
            case IN:{
                categoryId = categoryInId;
                repAccountId = null;
                break;
            }case OUT:{
                categoryId = categoryOutId;
                repAccountId = null;
                break;
            }case TRANSFER:{
                categoryId = null;
                break;
            }
        }

        int sum = mOperation.get().getSum();

        if(accountId == null){
            mStatus.postValue(Status.EMPTY_ACCOUNT);
            return;
        }else if(categoryId == null && (type == OperationType.IN || type == OperationType.OUT)){
            mStatus.postValue(Status.EMPTY_ANALYTIC);
            return;
        }else if(repAccountId == null && type == OperationType.TRANSFER){
            mStatus.postValue(Status.EMPTY_ANALYTIC);
            return;
        }else if(sum == 0){
            mStatus.postValue(Status.EMPTY_SUM);
            return;
        }

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

    public MutableLiveData<Integer> getSelectedAccount() {
        return selectedAccount;
    }

    public MutableLiveData<Integer> getSelectedInCategory() {
        return selectedInCategory;
    }

    public MutableLiveData<Integer> getSelectedOutCategory() {
        return selectedOutCategory;
    }

    public MutableLiveData<Integer> getSelectedRepAccount() {
        return selectedRepAccount;
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