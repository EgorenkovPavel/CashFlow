package com.epipasha.cashflow.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.objects.OperationType;

public class Repository implements DataSource{

    private volatile static Repository INSTANCE = null;

    private DataSource mLocalDataSource;

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

    @Override
    public void getAllAccounts(GetAccountsCallback callback) {
        mLocalDataSource.getAllAccounts(callback);
    }

    @Override
    public void getAllAccountsWithBalance(GetAccountsWithBalanceCallback callback) {
        mLocalDataSource.getAllAccountsWithBalance(callback);
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

}
