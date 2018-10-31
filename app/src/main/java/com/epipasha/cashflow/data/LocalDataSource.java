package com.epipasha.cashflow.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.dao.AnalyticDao;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.AccountWithBalance;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.CategoryWithCashflow;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.entites.OperationWithData;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Date;
import java.util.List;

public class LocalDataSource implements DataSource{

    private static volatile LocalDataSource INSTANCE;

    private AppDatabase mDb;
    private AppExecutors mAppExecutors;

    private LocalDataSource(@NonNull AppDatabase db) {
        mAppExecutors = AppExecutors.getInstance();
        mDb = db;
    }

    public static LocalDataSource getInstance(@NonNull AppDatabase db) {
        if (INSTANCE == null) {
            synchronized (LocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSource(db);
                }
            }
        }
        return INSTANCE;
    }

    // ACCOUNTS
    public void getAccountById(final int id, final DataSource.GetAccountCallback callback){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Account account = mDb.accountDao().getAccountById(id);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (account != null) {
                            callback.onAccountLoaded(account);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.discIO().execute(runnable);
    }

    public void insertAccount(final Account account){
        mAppExecutors.discIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.accountDao().insertAccount(account);
            }
        });
    }

    public void updateAccount(final Account account){
        mAppExecutors.discIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.accountDao().updateAccount(account);
            }
        });
    }

    public void getAllAccounts(final GetAccountsCallback callback){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Account> accounts = mDb.accountDao().getAllAccounts();

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (accounts != null) {
                            callback.onAccountsLoaded(accounts);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.discIO().execute(runnable);
    }

    @Override
    public void getAllAccountsWithBalance(final GetAccountsWithBalanceCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<AccountWithBalance> accounts = mDb.accountDao().getAllAccountsWithBalance();

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (accounts != null) {
                            callback.onAccountsWithBalanceLoaded(accounts);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.discIO().execute(runnable);
    }

    @Override
    public LiveData<List<AccountWithBalance>> loadAllAccountsWithBalance() {
        return mDb.accountDao().loadAllAccountsWithBalance();
    }

    @Override
    public LiveData<List<AccountWithBalance>> loadAllAccountsWithBalanceExceptId(int id) {
        return mDb.accountDao().loadAllAccountsWithBalanceExceptId(id);
    }

    // CATEGORIES
    public void getCategoryById(final int id, final DataSource.GetCategoryCallback callback){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Category category = mDb.categoryDao().getCategoryById(id);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (category != null) {
                            callback.onCategoryLoaded(category);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.discIO().execute(runnable);
    }

    public void insertCategory(final Category category){
        mAppExecutors.discIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.categoryDao().insertCategory(category);
            }
        });
    }

    public void updateCategory(final Category category){
        mAppExecutors.discIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.categoryDao().updateCategory(category);
            }
        });
    }

    public void getCategoriesByType(final OperationType type, final GetCategoriesByTypeCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Category> categories = mDb.categoryDao().getAllCategoriesByType(type);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (categories != null) {
                            callback.onCategoriesByTypeLoaded(categories, type);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.discIO().execute(runnable);
    }

    @Override
    public LiveData<List<Category>> loadAllCategoriesByType(OperationType type) {
        return mDb.categoryDao().loadAllCategoriesByType(type);
    }

    public LiveData<List<CategoryWithCashflow>> loadAllCategoriesWithCashflow(Date start, Date end) {
        return mDb.categoryDao().loadAllCategoriesWithCashflow(start, end);
    }

    // OPERATIONS
    public void getOperationById(final int id, final DataSource.GetOperationCallback callback){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Operation operation = mDb.operationDao().getOperationById(id);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (operation != null) {
                            callback.onOperationLoaded(operation);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.discIO().execute(runnable);
    }

    public void insertOperation(final Operation operation, final InsertOperationCallback callback){
        mAppExecutors.discIO().execute(new Runnable() {
            @Override
            public void run() {
                final int id = (int)mDb.operationDao().insertOperationWithAnalytic(operation);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (id != -1) {
                            callback.onOperationInsertedSuccess(id);
                        } else {
                            callback.onOperationInsertedFailed();
                        }
                    }
                });
            }
        });
    }

    public void updateOperation(final Operation operation, final UpdateOperationCallback callback){
        mAppExecutors.discIO().execute(new Runnable() {
            @Override
            public void run() {
                final int updatedCol = mDb.operationDao().updateOperationWithAnalytic(operation);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (updatedCol > 0) {
                            callback.onOperationUpdatedSuccess(updatedCol);
                        } else {
                            callback.onOperationUpdatedFailed();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void deleteOperation(final Operation operation, final DeleteOperationCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final int numCol = mDb.operationDao().deleteOperation(operation);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (numCol > 0) {
                            callback.onOperationDeletedSuccess(numCol);
                        } else {
                            callback.onOperationDeletedFailed();
                        }
                    }
                });
            }
        };

        mAppExecutors.discIO().execute(runnable);
    }

    public void deleteOperationById(final int operationId, final DeleteOperationCallback callback) {

        mAppExecutors.discIO().execute(new Runnable() {
            @Override
            public void run() {
                int numColumn = mDb.operationDao().deleteOperationById(operationId);
                if (callback != null) callback.onOperationDeletedSuccess(numColumn);
            }
        });
    }

    public LiveData<List<OperationWithData>> loadOperationWithData() {
        return mDb.operationDao().loadOperationWithData();
    }

    // ANALYTIC
    @Override
    public LiveData<List<AnalyticDao.MonthCashflow>> loadMonthCashflow(int categoryId) {
        return mDb.analyticDao().loadMonthCashflow(categoryId);
    }

}
