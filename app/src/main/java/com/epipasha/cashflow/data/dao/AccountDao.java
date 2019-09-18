package com.epipasha.cashflow.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.epipasha.cashflow.data.entites.AccountEntity;
import com.epipasha.cashflow.data.complex.AccountWithBalance;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM accounts ORDER BY title")
    LiveData<List<AccountEntity>> getAccountEntitiesLiveData();

    @Query("SELECT * FROM accounts ORDER BY title")
    List<AccountEntity> getAccountEntitiesList();

    @Query("SELECT * FROM accounts WHERE id = :id")
    LiveData<AccountEntity> getAccountEntityLiveData(int id);

    @Query("SELECT * FROM accounts WHERE id = :id")
    AccountEntity getAccountEntityById(int id);

    @Query("SELECT * FROM accounts WHERE id != :id ORDER BY title")
    LiveData<List<AccountEntity>> getAccountEntitiesLiveDataExceptId(int id);

    @Query("SELECT * FROM AccountWithBalance WHERE AccountWithBalance.id = :id ORDER BY title")
    LiveData<AccountWithBalance> getAccountWithBalanceLiveDataById(int id);

    @Query("SELECT * FROM AccountWithBalance ORDER BY title")
    LiveData<List<AccountWithBalance>> getAccountWithBalanceLiveData();

    @Query("SELECT * FROM AccountWithBalance ORDER BY title")
    List<AccountWithBalance> getAccountWithBalanceList();

    @Query("SELECT * FROM AccountWithBalance WHERE AccountWithBalance.id != :id ORDER BY title")
    LiveData<List<AccountWithBalance>> loadAllAccountsWithBalanceExceptId(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAccount(AccountEntity account);

    @Insert
    void insertAccounts(List<AccountEntity> accounts);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAccount(AccountEntity account);

    @Delete
    void deleteAccount(AccountEntity account);

    @Query("DELETE FROM accounts")
    void deleteAll();



    //RX
    @Query("SELECT * FROM AccountWithBalance WHERE AccountWithBalance.id = :id ORDER BY title")
    Flowable<AccountWithBalance> getRxAccountById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdateAccountRx(AccountEntity account);

    @Update
    Completable updateRxAccount(AccountEntity account);

    @Query("SELECT * FROM AccountWithBalance ORDER BY title")
    Flowable<List<AccountWithBalance>> getRxAllAccounts();
}
