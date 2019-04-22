package com.epipasha.cashflow.data;

import androidx.lifecycle.LiveData;

import com.epipasha.cashflow.data.dao.AnalyticDao;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.complex.AccountWithBalance;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.complex.CategoryWithCashflow;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.complex.OperationWithData;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class Repository implements DataSource{

    private volatile static Repository INSTANCE = null;

    private LocalDataSource mLocalDataSource;

    private Repository(LocalDataSource localDataSource){
        mLocalDataSource = localDataSource;
    }

    public static Repository getInstance(LocalDataSource localDataSource) {
        if (INSTANCE == null) {
            synchronized (Repository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Repository(localDataSource);
                }
            }
        }
        return INSTANCE;
    }

    // ACCOUNTS
    public void insertAccount(Account account){
        mLocalDataSource.insertAccount(account);
    }

    public void updateAccount(Account account){
        mLocalDataSource.updateAccount(account);
    }

    @Override
    public void getAccountById(int id, GetAccountCallback callback) {
        mLocalDataSource.getAccountById(id, callback);
    }

    public Flowable<Account> getAccountById(int id) {
        return mLocalDataSource.getAccountById(id);
    }

    public Completable insertOrUpdateAccount(Account account) {
        return mLocalDataSource.insertOrUpdateAccount(account);
    }

    @Override
    public void getAllAccounts(GetAccountsCallback callback) {
        mLocalDataSource.getAllAccounts(callback);
    }

    @Override
    public void getAllAccountsWithBalance(GetAccountsWithBalanceCallback callback) {
        mLocalDataSource.getAllAccountsWithBalance(callback);
    }

    @Override
    public LiveData<List<AccountWithBalance>> loadAllAccountsWithBalance() {
        return mLocalDataSource.loadAllAccountsWithBalance();
    }

    @Override
    public LiveData<List<AccountWithBalance>> loadAllAccountsWithBalanceExceptId(int id) {
        return mLocalDataSource.loadAllAccountsWithBalanceExceptId(id);
    }

    // CATEGORIES
    public void getCategoryById(int id, GetCategoryCallback callback){
        mLocalDataSource.getCategoryById(id, callback);
    }

    public void insertCategory(Category category){
        mLocalDataSource.insertCategory(category);
    }

    public void updateCategory(Category category){
        mLocalDataSource.updateCategory(category);
    }

    @Override
    public void getCategoriesByType(OperationType type, GetCategoriesByTypeCallback callback) {
        mLocalDataSource.getCategoriesByType(type, callback);
    }

    public void getParentCategories(OperationType type, GetCategoriesCallback getCategoriesCallback){
        mLocalDataSource.getParentCategories(type, getCategoriesCallback);
    }

    @Override
    public LiveData<List<Category>> loadAllCategoriesByType(OperationType type) {
        return mLocalDataSource.loadAllCategoriesByType(type);
    }

    @Override
    public LiveData<List<Category>> loadCategoriesByType(OperationType type) {
        return mLocalDataSource.loadCategoriesByType(type);
    }

    @Override
    public LiveData<List<Category>> loadSubcategoriesByType(OperationType type) {
        return mLocalDataSource.loadSubcategoriesByType(type);
    }

    @Override
    public LiveData<List<Category>> loadSubcategoriesByParent(Category category) {
        return mLocalDataSource.loadSubcategoriesByParent(category);
    }

    public LiveData<List<CategoryWithCashflow>> loadAllCategoriesWithCashflow(Date start, Date end) {
        return mLocalDataSource.loadAllCategoriesWithCashflow(start, end);
    }

    // OPERATIONS
    public void getOperationById(int id, GetOperationCallback callback){
        mLocalDataSource.getOperationById(id, callback);
    }

    public void insertOperation(Operation operation, InsertOperationCallback callback){
        mLocalDataSource.insertOperation(operation, callback);
    }

    public void updateOperation(Operation operation, UpdateOperationCallback callback){
        mLocalDataSource.updateOperation(operation, callback);
    }

    @Override
    public void deleteOperation(Operation operation, DeleteOperationCallback callback) {
        mLocalDataSource.deleteOperation(operation, callback);
    }

    public LiveData<List<OperationWithData>> loadOperationWithData() {
        return mLocalDataSource.loadOperationWithData();
    }

    public void deleteOperationById(int operationId, DeleteOperationCallback callback) {
        mLocalDataSource.deleteOperationById(operationId, callback);
    }


    // ANALYTIC
    @Override
    public LiveData<List<AnalyticDao.MonthCashflow>> loadMonthCashflow(int categoryId) {
        return mLocalDataSource.loadMonthCashflow(categoryId);
    }

    public LiveData<List<AnalyticDao.MonthCashflow>> loadAllMonthCashflow() {
        return mLocalDataSource.loadAllMonthCashflow();
    }

    public LiveData<List<AnalyticDao.CategoryCashflow>> loadCategoryCashflow(int year, int month, OperationType type) {
        return mLocalDataSource.loadCategoryCashflow(year, month, type);
    }
}
