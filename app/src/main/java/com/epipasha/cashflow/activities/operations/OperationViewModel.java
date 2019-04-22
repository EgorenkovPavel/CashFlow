package com.epipasha.cashflow.activities.operations;

import android.app.Application;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.AccountEntity;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class OperationViewModel extends AndroidViewModel{

    private MutableLiveData<Status> mStatus = new MutableLiveData<>();

    private DataSource mRepository;
    private ObservableField<Operation> mOperation = new ObservableField<>(
            new Operation(Calendar.getInstance().getTime(), OperationType.IN, 0, 0, 0, 0));
    private ObservableInt activityTitle = new ObservableInt(R.string.new_operation);
    private ObservableBoolean isNew = new ObservableBoolean(true);

    private ObservableField<List<AccountEntity>> mAccounts = new ObservableField<>();
    private ObservableField<List> mAnalytic = new ObservableField<>();

    private ObservableInt mAccountPosition = new ObservableInt(0);
    private ObservableInt mAnalyticPosition = new ObservableInt(0);

    private List<AccountEntity> mRecAccounts = new ArrayList<>();
    private List<Category> mCategoriesIn = new ArrayList<>();
    private List<Category> mCategoriesOut = new ArrayList<>();

    public OperationViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        mRepository.getAllAccounts(new DataSource.GetAccountsCallback() {
            @Override
            public void onAccountsLoaded(List<AccountEntity> accounts) {
                mAccounts.set(accounts);
                setAccountPosition();

                setRecAccounts();
                setAnalyticPosition();
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
        mRepository.getCategoriesByType(OperationType.IN, new DataSource.GetCategoriesByTypeCallback() {
            @Override
            public void onCategoriesByTypeLoaded(List<Category> categories, OperationType type) {
                mCategoriesIn = categories;
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
        mRepository.getCategoriesByType(OperationType.OUT, new DataSource.GetCategoriesByTypeCallback() {
            @Override
            public void onCategoriesByTypeLoaded(List<Category> categories, OperationType type) {
                mCategoriesOut = categories;
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    public void start(int operationId){
        mRepository.getOperationById(operationId, new DataSource.GetOperationCallback() {
            @Override
            public void onOperationLoaded(Operation operation) {
                mOperation.set(operation);
                isNew.set(false);
                activityTitle.set(R.string.operation);

                setAnalyticEntries();
                setAccountPosition();
                setAnalyticPosition();
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    public ObservableField<Operation> getOperation() {
        return mOperation;
    }

    public ObservableInt getActivityTitle() {
        return activityTitle;
    }

    public ObservableField<List> getAnalytic() {
        return mAnalytic;
    }

    public ObservableField<List<AccountEntity>> getAccounts() {
        return mAccounts;
    }

    public ObservableInt getAccountPosition() {
        return mAccountPosition;
    }

    public ObservableInt getAnalyticPosition() {
        return mAnalyticPosition;
    }

    public MutableLiveData<Status> getStatus() {
        return mStatus;
    }

    public void setOperationType(OperationType operationType) {
        mOperation.get().setType(operationType);
        setAnalyticEntries();
    }

    public void setOperationDate(Date date){
        mOperation.get().setDate(date);
        mOperation.notifyChange();
    }


    private void setAnalyticEntries() {

        OperationType type = mOperation.get().getType();
        switch (type){
            case IN:{
                mAnalytic.set(mCategoriesIn);
                break;
            }
            case OUT:{
                mAnalytic.set(mCategoriesOut);
                break;
            }
            case TRANSFER:{
                mAnalytic.set(mRecAccounts);
                break;
            }
        }
    }

    public void saveObject(){

        Operation operation = mOperation.get();

        if(operation == null){
            return;
        }

        if(operation.getSum() == 0){
            mStatus.setValue(Status.EMPTY_SUM);
            return;
        }

        operation.setAccountId(mAccounts.get().get(mAccountPosition.get()).getId());

        OperationType type = operation.getType();

        switch (type){
            case IN:{
                operation.setCategoryId(mCategoriesIn.get(mAnalyticPosition.get()).getId());
                break;
            }
            case OUT:{
                operation.setCategoryId(mCategoriesOut.get(mAnalyticPosition.get()).getId());
                break;
            }
            case TRANSFER:{
                operation.setRecipientAccountId(mRecAccounts.get(mAnalyticPosition.get()).getId());
                break;
            }
        }

        if(isNew.get()) {
            mRepository.insertOperation(operation, new DataSource.InsertOperationCallback() {
                @Override
                public void onOperationInsertedSuccess(int id) {
                    mStatus.setValue(Status.OPERATION_SAVED);
                }

                @Override
                public void onOperationInsertedFailed() {

                }
            });
        }else{
            mRepository.updateOperation(operation, new DataSource.UpdateOperationCallback() {
                @Override
                public void onOperationUpdatedSuccess(int updatedCol) {
                    mStatus.setValue(Status.OPERATION_SAVED);
                }

                @Override
                public void onOperationUpdatedFailed() {

                }
            });
        }
    }

    public void onAccountSelected(int pos){
        mAccountPosition.set(pos);
        setRecAccounts();
        setAnalyticEntries();
    }

    public Date getOperationDate(){
        return mOperation.get().getDate();
    }


    private void setRecAccounts(){
        List<AccountEntity> recAccounts = new ArrayList<>(mAccounts.get());
        recAccounts.remove(mAccountPosition.get());
        mRecAccounts = recAccounts;
    }

    public enum Status{
        EMPTY_SUM,
        EMPTY_ACCOUNT,
        EMPTY_ANALYTIC,
        OPERATION_SAVED
    }

    private int getPositionById(Object[] list, int id){

        for (int i=0; i<list.length;i++){
            Object object = list[i];

            if(object instanceof AccountEntity && ((AccountEntity) object).getId() == id){
                return i;
            }else if(object instanceof Category && ((Category) object).getId() == id) {
                return i;
            }
        }
        return 0;
    }

    private void setAccountPosition(){
        if(!isNew.get())
            mAccountPosition.set(getPositionById(mAccounts.get().toArray(), mOperation.get().getAccountId()));
    }

    private void setAnalyticPosition(){
        if(isNew.get()) return;
        OperationType type = mOperation.get().getType();
        switch (type){
            case IN:{
                mAnalyticPosition.set(getPositionById(mCategoriesIn.toArray(), mOperation.get().getCategoryId()));
                break;
            }
            case OUT:{
                mAnalyticPosition.set(getPositionById(mCategoriesOut.toArray(), mOperation.get().getCategoryId()));
                break;
            }
            case TRANSFER:{
                mAnalyticPosition.set(getPositionById(mRecAccounts.toArray(), mOperation.get().getRecipientAccountId()));
                break;
            }
        }
    }

}
