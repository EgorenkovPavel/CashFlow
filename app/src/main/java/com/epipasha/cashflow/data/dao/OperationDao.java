package com.epipasha.cashflow.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.epipasha.cashflow.data.entites.Balance;
import com.epipasha.cashflow.data.entites.Cashflow;
import com.epipasha.cashflow.data.entites.OperationEntity;
import com.epipasha.cashflow.data.complex.OperationWithData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;

@Dao
public abstract class OperationDao {

    @Query("SELECT * FROM operations ORDER BY Date DESC")
    public abstract LiveData<List<OperationEntity>> loadAllOperations();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertOperation(OperationEntity operation);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateOperation(OperationEntity operation);

    @Delete
    public abstract int deleteOperation(OperationEntity operation);

    @Query("DELETE FROM operations WHERE id == :id")
    public abstract int deleteOperationById(int id);

    @Query("SELECT * FROM operations WHERE id = :id")
    public abstract LiveData<OperationEntity> loadOperationById(int id);

    @Query("SELECT * FROM operations WHERE id = :id")
    public abstract OperationEntity getOperationById(int id);

    @Query("SELECT operations.id as id, "
            + "operations.date as date, "
            + "operations.type as type, "
            + "operations.sum as sum, "
            + "accounts.id as account_id, "
            + "accounts.title as account_title, "
            + "categories.id as category_id, "
            + "categories.title as category_title, "
            + "categories.type as category_type, "
            + "categories.budget as category_budget, "
            + "recipient_accounts.id as recipient_account_id, "
            + "recipient_accounts.title as recipient_account_title "
            + "FROM operations as operations "
            + "INNER JOIN accounts as accounts "
            + "ON operations.account_id = accounts.id "
            + "LEFT OUTER JOIN categories as categories "
            + "ON operations.category_id = categories.id "
            + "LEFT OUTER JOIN accounts as recipient_accounts "
            + "ON operations.recipient_account_id = recipient_accounts.id "
            + "ORDER BY operations.date DESC")
    public abstract LiveData<List<OperationWithData>> loadOperationWithData();

    @Query("SELECT operations.id as id, "
            + "operations.date as date, "
            + "operations.type as type, "
            + "operations.sum as sum, "
            + "accounts.id as account_id, "
            + "accounts.title as account_title, "
            + "categories.id as category_id, "
            + "categories.title as category_title, "
            + "categories.type as category_type, "
            + "categories.budget as category_budget, "
            + "recipient_accounts.id as recipient_account_id, "
            + "recipient_accounts.title as recipient_account_title "
            + "FROM operations as operations "
            + "INNER JOIN accounts as accounts "
            + "ON operations.account_id = accounts.id "
            + "LEFT OUTER JOIN categories as categories "
            + "ON operations.category_id = categories.id "
            + "LEFT OUTER JOIN accounts as recipient_accounts "
            + "ON operations.recipient_account_id = recipient_accounts.id "
            + "WHERE operations.id = :id")
    public abstract LiveData<OperationWithData> loadOperationWithDataById(int id);

    @Transaction
    public long insertOperationWithAnalytic(final OperationEntity operation){
        long id = insertOperation(operation);
        if(id != -1){
            operation.setId((int)id);
            insertBalance(operation.getBalance());
            insertCashflow(operation.getCashflow());
        }
        return id;
    }

    @Transaction
    public int updateOperationWithAnalytic(OperationEntity operation){
        int numDeletedCol = deleteOperation(operation);
        if(numDeletedCol > 0){
            insertOperationWithAnalytic(operation);
        }
        return numDeletedCol;
    }

    @Transaction
    public void deleteAll(){
        deleteAllOperations();
        deleteAllBalances();
        deleteAllCashflow();
    }

    @Insert
    public abstract void insertBalance(List<Balance> list);

    @Insert
    public abstract void insertCashflow(List<Cashflow> list);

    @Query("DELETE FROM operations")
    public abstract void deleteAllOperations();

    @Query("DELETE FROM balance")
    public abstract void deleteAllBalances();

    @Query("DELETE FROM cashflow")
    public abstract void deleteAllCashflow();

    //RX

    @Query("SELECT * FROM operations WHERE id = :id")
    public abstract Flowable<OperationEntity> getRxOperationById(int id);

}
