package com.epipasha.cashflow.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.Transformations;

import com.epipasha.cashflow.data.dao.AnalyticDao;
import com.epipasha.cashflow.data.entites.AccountEntity;
import com.epipasha.cashflow.data.complex.AccountWithBalance;
import com.epipasha.cashflow.data.entites.CategoryEntity;
import com.epipasha.cashflow.data.complex.CategoryWithCashflow;
import com.epipasha.cashflow.data.entites.OperationEntity;
import com.epipasha.cashflow.data.complex.OperationWithData;
import com.epipasha.cashflow.data.objects.Account;
import com.epipasha.cashflow.data.objects.Category;
import com.epipasha.cashflow.data.objects.Operation;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class Repository implements DataSource {

    private volatile static Repository INSTANCE = null;

    private LocalDataSource mLocalDataSource;

    private Repository(LocalDataSource localDataSource) {
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
    public LiveData<Account> getAccountById(int id) {
        return Transformations.map(mLocalDataSource.getAccountById(id),
                this::transformToAccount);
    }

    private Account transformToAccount(AccountWithBalance accountEntity){
        return new Account(accountEntity.getId(), accountEntity.getTitle(), accountEntity.getSum());
    }

    private AccountEntity transformToAccountEntity(Account account){
        return new AccountEntity(account.getId(), account.getTitle());
    }

    public void insertOrUpdateAccount(Account account) {
        mLocalDataSource.insertAccount(transformToAccountEntity(account));
    }


    public Flowable<Account> getRxAccountById(int id) {
        //TODO get sum for account
        Flowable<AccountWithBalance> p = mLocalDataSource.getRxAccountById(id);
        return p.map(this::transformToAccount);
    }

    public Completable insertOrUpdateRxAccount(Account account) {
        return mLocalDataSource.insertOrUpdateAccount(transformToAccountEntity(account));
    }

    public Flowable<List<Account>> getAllAccounts() {
        Flowable<List<AccountWithBalance>> p = mLocalDataSource.getAllAccounts();
        return p.map(accountEntities ->
        {
            List<Account> accounts = new ArrayList<>();
            for (AccountWithBalance account : accountEntities) {
                accounts.add(new Account(account.getId(), account.getTitle(), account.getSum()));
            }
            return accounts;
        });
    }

    public LiveData<List<Account>> getAllAccountsLive() {
        return LiveDataReactiveStreams.fromPublisher(getAllAccounts());
    }



    public void insertAccount(AccountEntity account) {
        mLocalDataSource.insertAccount(account);
    }

    public void updateAccount(AccountEntity account) {
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

    @Override
    public LiveData<List<AccountWithBalance>> loadAllAccountsWithBalance() {
        return mLocalDataSource.loadAllAccountsWithBalance();
    }

    @Override
    public LiveData<List<AccountWithBalance>> loadAllAccountsWithBalanceExceptId(int id) {
        return mLocalDataSource.loadAllAccountsWithBalanceExceptId(id);
    }

    // CATEGORIES
    public LiveData<Category> getCategoryById(int id) {
        return Transformations.map(mLocalDataSource.getCategoryById(id), this::transformToCategory);
    }

    public Flowable<Category> getCategoryRxById(int id) {
        Flowable<CategoryEntity> f = mLocalDataSource.getCategoryRxById(id);
        return f.map(this::transformToCategory);
    }

    private Category transformToCategory(CategoryEntity categoryEntity) {
        return new Category(
                categoryEntity.getId(),
                categoryEntity.getTitle(),
                categoryEntity.getType());
    }

    private CategoryEntity transformToCategoryEntity(Category category) {
        return new CategoryEntity(
                category.getId(),
                category.getTitle(),
                category.getType(), 0);
    }


    public Flowable<List<Category>> getCategoriesByType(OperationType type) {
        return mLocalDataSource.getCategoriesByType(type).map(categoryEntities -> {
            List<Category> categories = new ArrayList<>();
            for (CategoryEntity entity : categoryEntities) {
                categories.add(transformToCategory(entity));
            }
            return categories;
        });
    }

    public Completable insertOrUpdateCategory(Category category) {
        return mLocalDataSource.insertOrUpdateCategory(transformToCategoryEntity(category));
    }


    public void getCategoryById(int id, GetCategoryCallback callback) {
        mLocalDataSource.getCategoryById(id, callback);
    }

    public void insertCategory(CategoryEntity category) {
        mLocalDataSource.insertCategory(category);
    }

    public void updateCategory(CategoryEntity category) {
        mLocalDataSource.updateCategory(category);
    }

    @Override
    public void getCategoriesByType(OperationType type, GetCategoriesByTypeCallback callback) {
        mLocalDataSource.getCategoriesByType(type, callback);
    }

    @Override
    public LiveData<List<CategoryEntity>> loadAllCategoriesByType(OperationType type) {
        return mLocalDataSource.loadAllCategoriesByType(type);
    }

    @Override
    public LiveData<List<CategoryEntity>> loadCategoriesByType(OperationType type) {
        return mLocalDataSource.loadCategoriesByType(type);
    }

    public LiveData<List<CategoryWithCashflow>> loadAllCategoriesWithCashflow(Date start, Date end) {
        return mLocalDataSource.loadAllCategoriesWithCashflow(start, end);
    }

    // OPERATIONS
    public Flowable<Operation> getOperationById(int id) {
        return mLocalDataSource.getOperationById(id).flatMap(this::toOperation);
    }

    public Single<Integer> insertOrUpdateOperation(Operation operation) {
        return mLocalDataSource.insertOrUpdateOperation(
                new OperationEntity(
                        operation.getId(),
                        operation.getDate(),
                        operation.getType(),
                        operation.getAccount().getId(),
                        operation.getCategory() == null ? null : operation.getCategory().getId(),
                        operation.getRecAccount() == null ? null : operation.getRecAccount().getId(),
                        operation.getSum()));
    }

    private Flowable<Operation> toOperation(OperationEntity entity) {
        if(!entity.getType().equals(OperationType.TRANSFER)) {
            Flowable<Account> accountFlowable = getRxAccountById(entity.getAccountId());
            Flowable<Category> categoryFlowable = getCategoryRxById(entity.getCategoryId());
            return Flowable.zip(accountFlowable, categoryFlowable,
                    (account, category) -> new Operation(
                            entity.getId(),
                            entity.getDate(),
                            entity.getType(),
                            account,
                            category,
                            null,
                            entity.getSum()));
        }else{
            Flowable<Account> accountFlowable = getRxAccountById(entity.getAccountId());
            Flowable<Account> recAccountFlowable = getRxAccountById(entity.getRecipientAccountId());
            return Flowable.zip(accountFlowable, recAccountFlowable,
                    (account, recAccount) -> new Operation(
                            entity.getId(),
                            entity.getDate(),
                            entity.getType(),
                            account,
                            null,
                            recAccount,
                            entity.getSum()));

        }
    }

    public Completable deleteOperation(int id){
        return mLocalDataSource.deleteOperation(id);
    }

    public void getOperationById(int id, GetOperationCallback callback) {
        mLocalDataSource.getOperationById(id, callback);
    }

    public void insertOperation(OperationEntity operation, InsertOperationCallback callback) {
        mLocalDataSource.insertOperation(operation, callback);
    }

    public void updateOperation(OperationEntity operation, UpdateOperationCallback callback) {
        mLocalDataSource.updateOperation(operation, callback);
    }

    @Override
    public void deleteOperation(OperationEntity operation, DeleteOperationCallback callback) {
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
