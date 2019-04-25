package com.epipasha.cashflow.activities.operations;

import android.app.Application;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.AccountEntity;
import com.epipasha.cashflow.data.entites.CategoryEntity;
import com.epipasha.cashflow.data.entites.OperationEntity;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class OperationViewModel extends AndroidViewModel{

    private CompositeDisposable mDisposable = new CompositeDisposable();


    private MutableLiveData<Status> mStatus = new MutableLiveData<>();

    private Repository mRepository;
    private ObservableField<OperationEntity> mOperation = new ObservableField<>(
            new OperationEntity(Calendar.getInstance().getTime(), OperationType.IN, 0, 0, 0, 0));
    private ObservableInt activityTitle = new ObservableInt(R.string.new_operation);
    private ObservableBoolean isNew = new ObservableBoolean(true);

    private ObservableField<List<AccountEntity>> mAccounts = new ObservableField<>();
    private ObservableField<List> mAnalytic = new ObservableField<>();

    private ObservableInt mAccountPosition = new ObservableInt(0);
    private ObservableInt mAnalyticPosition = new ObservableInt(0);

    private List<AccountEntity> mRecAccounts = new ArrayList<>();
    private List<CategoryEntity> mCategoriesIn = new ArrayList<>();
    private List<CategoryEntity> mCategoriesOut = new ArrayList<>();

    //TODO rewrite to OrerationObject
    //TODO Add 1 observer for analytics and onNext(type) get analytic
    //TODO add model to spinner adapters

    public OperationViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        mDisposable.add(mRepository.getAllAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(accountEntities -> {
                    mAccounts.set(accountEntities);
                    setAccountPosition();

                    setRecAccounts();
                    setAnalyticPosition();
                }, throwable -> {
                }));

        mDisposable.add(mRepository.getCategoriesByType(OperationType.IN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> mCategoriesIn = categories, throwable -> {}));

        mDisposable.add(mRepository.getCategoriesByType(OperationType.OUT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> mCategoriesOut = categories, throwable -> {}));
    }

    public void start(int operationId) {
        mDisposable.add(mRepository.getOperationById(operationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(operation -> {
                    mOperation.set(operation);
                    isNew.set(false);
                    activityTitle.set(R.string.operation);

                    setAnalyticEntries();
                    setAccountPosition();
                    setAnalyticPosition();
                }, throwable -> {
                }));
    }

    public ObservableField<OperationEntity> getOperation() {
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

        OperationEntity operation = mOperation.get();

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

        mDisposable.add(mRepository.insertOrUpdateOperation(operation)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(integer -> {
            mStatus.setValue(Status.OPERATION_SAVED);
        }, throwable -> {}));

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
            }else if(object instanceof CategoryEntity && ((CategoryEntity) object).getId() == id) {
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

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
    }
}
