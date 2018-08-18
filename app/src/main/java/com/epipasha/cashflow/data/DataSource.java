package com.epipasha.cashflow.data;

import android.arch.lifecycle.LiveData;

import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.AccountWithBalance;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.objects.OperationType;

import java.util.List;

public interface DataSource {

    interface GetAccountCallback {
        void onAccountLoaded(Account account);
        void onDataNotAvailable();
    }

    interface GetCategoryCallback{
        void onCategoryLoaded(Category category);
        void onDataNotAvailable();
    }

    interface GetOperationCallback{
        void onOperationLoaded(Operation operation);
        void onDataNotAvailable();
    }

    interface GetAccountsCallback{
        void onAccountsLoaded(List<Account> accounts);
        void onDataNotAvailable();
    }

    interface GetAccountsWithBalanceCallback{
        void onAccountsWithBalanceLoaded(List<AccountWithBalance> accounts);
        void onDataNotAvailable();
    }

    interface GetCategoriesByTypeCallback{
        void onCategoriesByTypeLoaded(List<Category> categories, OperationType type);
        void onDataNotAvailable();
    }

    interface InsertOperationCallback{
        void onOperationInsertedSuccess(int id);
        void onOperationInsertedFailed();
    }

    interface UpdateOperationCallback{
        void onOperationUpdatedSuccess(int updatedCol);
        void onOperationUpdatedFailed();
    }

    interface DeleteOperationCallback{
        void onOperationDeletedSuccess(int numCol);
        void onOperationDeletedFailed();
    }

    // ACCOUNTS
    void getAccountById(int id, GetAccountCallback callback);

    void insertAccount(Account account);

    void updateAccount(Account account);

    void getAllAccounts(GetAccountsCallback callback);

    void getAllAccountsWithBalance(GetAccountsWithBalanceCallback callback);

    LiveData<List<AccountWithBalance>> loadAllAccountsWithBalance();

    LiveData<List<AccountWithBalance>> loadAllAccountsWithBalanceExceptId(int id);

    // CATEGORIES
    void getCategoryById(int id, GetCategoryCallback callback);

    void insertCategory(Category category);

    void updateCategory(Category category);

    void getCategoriesByType(OperationType type, GetCategoriesByTypeCallback callback);

    LiveData<List<Category>> loadAllCategoriesByType(OperationType type);

    // OPERATIONS
    void getOperationById(int id, GetOperationCallback callback);

    void insertOperation(Operation operation, InsertOperationCallback callback);

    void updateOperation(Operation operation, UpdateOperationCallback callback);

    void deleteOperation(Operation operation, DeleteOperationCallback callback);

}
