package com.epipasha.cashflow.data;

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
    public void getAccountById(int id, GetAccountCallback callback);

    public void insertAccount(Account account);

    public void updateAccount(Account account);

    public void getAllAccounts(GetAccountsCallback callback);

    public void getAllAccountsWithBalance(GetAccountsWithBalanceCallback callback);

    // CATEGORIES
    public void getCategoryById(int id, GetCategoryCallback callback);

    public void insertCategory(Category category);

    public void updateCategory(Category category);

    public void getCategoriesByType(OperationType type, GetCategoriesByTypeCallback callback);

    // OPERATIONS
    public void getOperationById(int id, GetOperationCallback callback);

    public void insertOperation(Operation operation, InsertOperationCallback callback);

    public void updateOperation(Operation operation, UpdateOperationCallback callback);

    public void deleteOperation(Operation operation, DeleteOperationCallback callback);
}
