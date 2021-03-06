package com.epipasha.cashflow.data;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.epipasha.cashflow.data.dao.AnalyticDao;
import com.epipasha.cashflow.data.complex.AccountWithBalance;
import com.epipasha.cashflow.data.entites.AccountEntity;
import com.epipasha.cashflow.data.entites.CategoryEntity;
import com.epipasha.cashflow.data.complex.CategoryWithCashflow;
import com.epipasha.cashflow.data.entites.OperationEntity;
import com.epipasha.cashflow.data.complex.OperationWithData;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

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
    public Flowable<AccountWithBalance> getRxAccountById(int id){
        return mDb.accountDao().getRxAccountById(id);
    }

    public Completable insertOrUpdateAccount(AccountEntity account) {
        return mDb.accountDao().insertOrUpdateAccountRx(account);
    }

    public Flowable<List<AccountWithBalance>> getAllAccounts(){
        return mDb.accountDao().getRxAllAccounts();
    }

    public Flowable<Integer> getAccountBalance(int accountId){
        return mDb.analyticDao().getRxAccountBalance(accountId);
    }

    public LiveData<AccountWithBalance> getAccountById(int id){
        return mDb.accountDao().getAccountWithBalanceLiveDataById(id);
    }

    public void getAccountById(final int id, final DataSource.GetAccountCallback callback){
        Runnable runnable = () -> {
            final AccountEntity account = mDb.accountDao().getAccountEntityById(id);

            mAppExecutors.mainThread().execute(() -> {
                if (account != null) {
                    callback.onAccountLoaded(account);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };

        mAppExecutors.discIO().execute(runnable);
    }

    public void insertAccount(final AccountEntity account){
        mAppExecutors.discIO().execute(() -> mDb.accountDao().insertAccount(account));
    }

    public void updateAccount(final AccountEntity account){
        mAppExecutors.discIO().execute(() -> mDb.accountDao().updateAccount(account));
    }

    public void getAllAccounts(final GetAccountsCallback callback){
        Runnable runnable = () -> {
            final List<AccountEntity> accounts = mDb.accountDao().getAccountEntitiesList();

            mAppExecutors.mainThread().execute(() -> {
                if (accounts != null) {
                    callback.onAccountsLoaded(accounts);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };

        mAppExecutors.discIO().execute(runnable);
    }

    @Override
    public void getAllAccountsWithBalance(final GetAccountsWithBalanceCallback callback) {
        Runnable runnable = () -> {
            final List<AccountWithBalance> accounts = mDb.accountDao().getAccountWithBalanceList();

            mAppExecutors.mainThread().execute(() -> {
                if (accounts != null) {
                    callback.onAccountsWithBalanceLoaded(accounts);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };

        mAppExecutors.discIO().execute(runnable);
    }

    @Override
    public LiveData<List<AccountWithBalance>> loadAllAccountsWithBalance() {
        return mDb.accountDao().getAccountWithBalanceLiveData();
    }

    @Override
    public LiveData<List<AccountWithBalance>> loadAllAccountsWithBalanceExceptId(int id) {
        return mDb.accountDao().loadAllAccountsWithBalanceExceptId(id);
    }

    // CATEGORIES
    public LiveData<CategoryEntity> getCategoryById(int id){
        return mDb.categoryDao().loadCategoryById(id);
    }

    public Flowable<CategoryEntity> getCategoryRxById(int id){
        return mDb.categoryDao().getRxCategoryById(id);
    }

    public Flowable<List<CategoryEntity>> getCategoriesByType(final OperationType type) {
        return mDb.categoryDao().getRxAllCategoriesByType(type);
    }

    public Completable insertOrUpdateCategory(CategoryEntity category) {
        if(category.getId() == 0)
            return mDb.categoryDao().insertRxCategory(category);
        else
            return mDb.categoryDao().updateRxCategory(category);
    }



    public void getCategoryById(final int id, final DataSource.GetCategoryCallback callback){
        Runnable runnable = () -> {
            final CategoryEntity category = mDb.categoryDao().getCategoryById(id);

            mAppExecutors.mainThread().execute(() -> {
                if (category != null) {
                    callback.onCategoryLoaded(category);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };

        mAppExecutors.discIO().execute(runnable);
    }

    public void insertCategory(final CategoryEntity category){
        mAppExecutors.discIO().execute(() -> mDb.categoryDao().insertCategory(category));
    }

    public void updateCategory(final CategoryEntity category){
        mAppExecutors.discIO().execute(() -> mDb.categoryDao().updateCategory(category));
    }

    public void getCategoriesByType(final OperationType type, final GetCategoriesByTypeCallback callback) {
        Runnable runnable = () -> {
            final List<CategoryEntity> categories = mDb.categoryDao().getAllCategoriesByType(type);

            mAppExecutors.mainThread().execute(() -> {
                if (categories != null) {
                    callback.onCategoriesByTypeLoaded(categories, type);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };

        mAppExecutors.discIO().execute(runnable);
    }

    @Override
    public LiveData<List<CategoryEntity>> loadAllCategoriesByType(OperationType type) {
        return mDb.categoryDao().loadAllCategoriesByType(type);
    }

    @Override
    public LiveData<List<CategoryEntity>> loadCategoriesByType(OperationType type) {
        return mDb.categoryDao().loadCategoriesByType(type);
    }

    public LiveData<List<CategoryWithCashflow>> loadAllCategoriesWithCashflow(Date start, Date end) {
        return mDb.categoryDao().loadAllCategoriesWithCashflow(start, end);
    }

    // OPERATIONS
    public Flowable<OperationEntity> getOperationById(final int id){
        return mDb.operationDao().getRxOperationById(id);
    }

    public Single<Integer> insertOrUpdateOperation(OperationEntity operation) {
        Single<Integer> res = Single.create(emitter -> {
            if(operation.getId() == 0)
                emitter.onSuccess((int)mDb.operationDao().insertOperationWithAnalytic(operation));
            else
                emitter.onSuccess(mDb.operationDao().updateOperationWithAnalytic(operation));
        });
        return res;
    }

    public Completable deleteOperation(int id){
        return null;//mDb.operationDao().deleteRxOperation(id);
    }




    public void getOperationById(final int id, final DataSource.GetOperationCallback callback){
        Runnable runnable = () -> {
            final OperationEntity operation = mDb.operationDao().getOperationById(id);

            mAppExecutors.mainThread().execute(() -> {
                if (operation != null) {
                    callback.onOperationLoaded(operation);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };

        mAppExecutors.discIO().execute(runnable);
    }

    public void insertOperation(final OperationEntity operation, final InsertOperationCallback callback){
        mAppExecutors.discIO().execute(() -> {
            final int id = (int)mDb.operationDao().insertOperationWithAnalytic(operation);

            mAppExecutors.mainThread().execute(() -> {
                if (id != -1) {
                    callback.onOperationInsertedSuccess(id);
                } else {
                    callback.onOperationInsertedFailed();
                }
            });
        });
    }

    public void updateOperation(final OperationEntity operation, final UpdateOperationCallback callback){
        mAppExecutors.discIO().execute(() -> {
            final int updatedCol = mDb.operationDao().updateOperationWithAnalytic(operation);

            mAppExecutors.mainThread().execute(() -> {
                if (updatedCol > 0) {
                    callback.onOperationUpdatedSuccess(updatedCol);
                } else {
                    callback.onOperationUpdatedFailed();
                }
            });
        });
    }

    @Override
    public void deleteOperation(final OperationEntity operation, final DeleteOperationCallback callback) {
        Runnable runnable = () -> {
            final int numCol = mDb.operationDao().deleteOperation(operation);

            mAppExecutors.mainThread().execute(() -> {
                if (numCol > 0) {
                    callback.onOperationDeletedSuccess(numCol);
                } else {
                    callback.onOperationDeletedFailed();
                }
            });
        };

        mAppExecutors.discIO().execute(runnable);
    }

    public void deleteOperationById(final int operationId, final DeleteOperationCallback callback) {

        mAppExecutors.discIO().execute(() -> {
            int numColumn = mDb.operationDao().deleteOperationById(operationId);
            if (callback != null) callback.onOperationDeletedSuccess(numColumn);
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

    public LiveData<List<AnalyticDao.MonthCashflow>> loadAllMonthCashflow() {
        return mDb.analyticDao().loadAllMonthCashflow();
    }

    public LiveData<List<AnalyticDao.CategoryCashflow>> loadCategoryCashflow(int year, int month, OperationType type) {
        return mDb.analyticDao().loadCategoryCashflow(year, month, type);
    }
}
