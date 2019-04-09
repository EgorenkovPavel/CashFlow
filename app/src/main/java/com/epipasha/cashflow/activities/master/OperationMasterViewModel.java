package com.epipasha.cashflow.activities.master;

import android.app.Application;
import android.widget.TextView;

import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.complex.AccountWithBalance;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

        Operation operation = mOperation.get();
        if(operation == null) return;
        operation.setType(type);
        mOperation.notifyChange();
    }

    public void onDigitPressed(int digit){
        Operation operation = mOperation.get();
        if(operation == null) return;
        int sum = operation.getSum();
        sum = sum * 10 + digit;
        operation.setSum(sum);
        mOperation.notifyChange();
    }

    public void onDeleteDigit(){
        Operation operation = mOperation.get();
        if(operation == null) return;
        int val = operation.getSum();
        val = val/10;
        operation.setSum(val);
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
