package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.epipasha.cashflow.Utils;
import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.objects.OperationType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class OperationDetailViewModel extends AndroidViewModel implements DataSource.GetOperationCallback, DataSource.GetAccountsCallback, DataSource.GetCategoriesByTypeCallback {

    private MutableLiveData<Status> mStatus = new MutableLiveData<>();

    private DataSource mRepository;
    private ObservableField<Operation> mOperation;
    private ObservableBoolean isNew = new ObservableBoolean(true);

    private ObservableField<List<Account>> mAccounts = new ObservableField<>();
    private ObservableField<List> mAnalytic = new ObservableField<>();

    private ObservableInt mAccountPosition = new ObservableInt(0);
    private ObservableInt mAnalyticPosition = new ObservableInt(0);

    private List<Account> mRecAccounts = new ArrayList<>();
    private List<Category> mCategoriesIn = new ArrayList<>();
    private List<Category> mCategoriesOut = new ArrayList<>();

    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            Operation operation = mOperation.get();
            int sum = (int) Utils.getLong(s.toString());

            if(operation.getSum() != sum){
                operation.setSum(sum);
                mOperation.notifyChange();
            }
        }
    };

    public OperationDetailViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        mOperation = new ObservableField<>(new Operation(Calendar.getInstance().getTime(), OperationType.IN, 0, 0, 0, 0));

        mRepository.getAllAccounts(this);
        mRepository.getCategoriesByType(OperationType.IN, this);
        mRepository.getCategoriesByType(OperationType.OUT, this);
    }

    public TextWatcher getWatcher() {
        return mWatcher;
    }

    public void start(int operationId){
        mRepository.getOperationById(operationId, this);
    }

    public ObservableField<Operation> getOperation() {
        return mOperation;
    }

    public ObservableBoolean getIsNew() {
        return isNew;
    }

    public void setOperationType(OperationType operationType) {
        mOperation.get().setType(operationType);
        setAnalyticEntries();
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

    public ObservableField<List> getAnalytic() {
        return mAnalytic;
    }

    public MutableLiveData<Status> getStatus() {
        return mStatus;
    }

    public ObservableField<List<Account>> getAccounts() {
        return mAccounts;
    }

    public ObservableInt getAccountPosition() {
        return mAccountPosition;
    }

    public ObservableInt getAnalyticPosition() {
        return mAnalyticPosition;
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

        if(isNew.get()){
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

    public void setOperationDate(Date date){
        mOperation.get().setDate(date);
        mOperation.notifyChange();
    }

    @BindingAdapter({"app:sum"})
    public static void getSum(EditText view, int sum) {
        view.setText(String.format(Locale.getDefault(),"%,d", sum));
        view.setSelection(view.getText().toString().length());
    }

    @BindingAdapter({"app:date"})
    public static void getDate(TextView view, Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        view.setText(format.format(date));
    }

    @BindingAdapter({"app:time"})
    public static void getTime(TextView view, Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        view.setText(format.format(date));
    }

    @Override
    public void onOperationLoaded(Operation operation) {
        mOperation.set(operation);
        isNew.set(false);

        setAnalyticEntries();
        setAccountPosition();
        setAnalyticPosition();
    }

    @Override
    public void onAccountsLoaded(List<Account> accounts) {
        mAccounts.set(accounts);
        setAccountPosition();

        setRecAccounts();
        setAnalyticPosition();

    }

    @Override
    public void onCategoriesByTypeLoaded(List<Category> categories, OperationType type) {
        if (type == OperationType.IN)
            mCategoriesIn = categories;
        else
            mCategoriesOut = categories;
    }

    private void setRecAccounts(){
        List<Account> recAccounts = new ArrayList<>(mAccounts.get());
        recAccounts.remove(mAccountPosition.get());
        mRecAccounts = recAccounts;
    }

    @Override
    public void onDataNotAvailable() {

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

            if(object instanceof Account && ((Account) object).getId() == id){
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
