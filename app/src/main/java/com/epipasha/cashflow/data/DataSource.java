package com.epipasha.cashflow.data;

import androidx.lifecycle.LiveData;

import com.epipasha.cashflow.data.dao.AnalyticDao;
import com.epipasha.cashflow.data.entites.AccountEntity;
import com.epipasha.cashflow.data.complex.AccountWithBalance;
import com.epipasha.cashflow.data.entites.CategoryEntity;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.List;

public interface DataSource {

    interface GetAccountCallback {
        void onAccountLoaded(AccountEntity account);
        void onDataNotAvailable();
    }

    interface GetCategoryCallback{
        void onCategoryLoaded(CategoryEntity category);
        void onDataNotAvailable();
    }

    interface GetCategoriesCallback{
        void onCategoriesLoaded(List<CategoryEntity> categories);
        void onDataNotAvailable();
    }

    interface GetOperationCallback{
        void onOperationLoaded(Operation operation);
        void onDataNotAvailable();
    }

    interface GetAccountsCallback{
        void onAccountsLoaded(List<AccountEntity> accounts);
        void onDataNotAvailable();
    }

    interface GetAccountsWithBalanceCallback{
        void onAccountsWithBalanceLoaded(List<AccountWithBalance> accounts);
        void onDataNotAvailable();
    }

    interface GetCategoriesByTypeCallback{
        void onCategoriesByTypeLoaded(List<CategoryEntity> categories, OperationType type);
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

    void insertAccount(AccountEntity account);

    void updateAccount(AccountEntity account);

    void getAllAccounts(GetAccountsCallback callback);

    void getAllAccountsWithBalance(GetAccountsWithBalanceCallback callback);

    LiveData<List<AccountWithBalance>> loadAllAccountsWithBalance();

    LiveData<List<AccountWithBalance>> loadAllAccountsWithBalanceExceptId(int id);

    // CATEGORIES
    void getCategoryById(int id, GetCategoryCallback callback);

    void insertCategory(CategoryEntity category);

    void updateCategory(CategoryEntity category);

    void getCategoriesByType(OperationType type, GetCategoriesByTypeCallback callback);

    LiveData<List<CategoryEntity>> loadAllCategoriesByType(OperationType type);

    LiveData<List<CategoryEntity>> loadCategoriesByType(OperationType type);

    // OPERATIONS
    void getOperationById(int id, GetOperationCallback callback);

    void insertOperation(Operation operation, InsertOperationCallback callback);

    void updateOperation(Operation operation, UpdateOperationCallback callback);

    void deleteOperation(Operation operation, DeleteOperationCallback callback);

    //ANALYTIC
    LiveData<List<AnalyticDao.MonthCashflow>> loadMonthCashflow(int categoryId);
}
