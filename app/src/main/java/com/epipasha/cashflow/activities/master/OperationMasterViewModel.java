package com.epipasha.cashflow.activities.master;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.complex.AccountWithBalance;
import com.epipasha.cashflow.data.entites.CategoryEntity;
import com.epipasha.cashflow.data.entites.OperationEntity;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class OperationMasterViewModel extends AndroidViewModel {

    private Repository mRepository;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    private LiveData<List<AccountWithBalance>> mAccounts;
    private LiveData<List<CategoryEntity>> mAllCategories;
    private MediatorLiveData<List<CategoryEntity>> mCategories = new MediatorLiveData<>();

    private MutableLiveData<AccountWithBalance> selectedAccount = new MutableLiveData<>();
    private MutableLiveData<CategoryEntity> selectedCategory = new MutableLiveData<>();
    private MutableLiveData<AccountWithBalance> selectedRepAccount = new MutableLiveData<>();

    private MutableLiveData<Status> mStatus = new MutableLiveData<>();

    private OperationEntity operation;

    private ObservableField<OperationEntity> mOperation = new ObservableField<>(new OperationEntity(new Date(), OperationType.IN, 0,0,0,0));


    public OperationMasterViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        mAccounts = mRepository.loadAllAccountsWithBalance();
        mAllCategories = mRepository.loadCategoriesByType(mOperation.get().getType());

        reloadCategories(mOperation.get().getType());
    }

    public ObservableField<OperationEntity> getOperation() {
        return mOperation;
    }

    public void onOperationTypeChanged(OperationType type){
        OperationEntity operation = mOperation.get();
        if(operation == null) return;
        operation.setType(type);
        mOperation.notifyChange();

        reloadCategories(type);
    }

    private void reloadCategories(OperationType type){

        mCategories.removeSource(mAllCategories);

        mAllCategories = mRepository.loadCategoriesByType(type);
        mCategories.addSource(mAllCategories, categories -> mCategories.setValue(categories));
    }

    public LiveData<List<AccountWithBalance>> getAccounts() {
        return mAccounts;
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return mCategories;
    }

    public void selectAccount(AccountWithBalance account){
        selectedAccount.setValue(account);

        AccountWithBalance repAccountId = selectedRepAccount.getValue();
        if(repAccountId != null && repAccountId.getId() == account.getId())
            selectedRepAccount.setValue(null);
    }

    public void selectRepAccount(AccountWithBalance account){
        AccountWithBalance accountId = selectedAccount.getValue();
        if(accountId == null || accountId.getId() != account.getId())
            selectedRepAccount.setValue(account);
    }

    public void saveOperation(){

        Integer accountId = selectedAccount.getValue().getId();
        Integer categoryId = selectedCategory.getValue().getId();
        Integer repAccountId = selectedRepAccount.getValue().getId();

        OperationType type = mOperation.get().getType();

        switch (type){
            case IN:{
                repAccountId = null;
                break;
            }case OUT:{
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

        operation = new OperationEntity(new Date(), type, accountId, categoryId, repAccountId, sum);
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
        mDisposable.add(mRepository.deleteOperation(operation.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> {
            mStatus.postValue(Status.OPERATION_DELETED);
        }));
    }

    public void onMoreClicked(){
        mStatus.setValue(Status.CLOSE);
    }

    public LiveData<Status> getStatus() {
        return mStatus;
    }

    public MutableLiveData<AccountWithBalance> getSelectedAccount() {
        return selectedAccount;
    }

    public MutableLiveData<CategoryEntity> getSelectedCategory() {
        return selectedCategory;
    }

    public MutableLiveData<AccountWithBalance> getSelectedRepAccount() {
        return selectedRepAccount;
    }

    public void selectCategory(CategoryEntity category) {
        selectedCategory.setValue(category);
    }

    public enum Status{
        EMPTY_TYPE,
        EMPTY_ANALYTIC,
        EMPTY_ACCOUNT,
        EMPTY_SUM,
        OPERATION_SAVED,
        OPERATION_DELETED,
        CLOSE
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
    }
}
