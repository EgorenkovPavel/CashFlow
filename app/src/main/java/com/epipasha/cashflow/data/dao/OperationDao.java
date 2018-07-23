package com.epipasha.cashflow.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.epipasha.cashflow.data.entites.Balance;
import com.epipasha.cashflow.data.entites.Cashflow;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.entites.OperationWithData;

import java.util.List;

@Dao
public abstract class OperationDao {

    @Query("SELECT * FROM operations ORDER BY Date DESC")
    public abstract LiveData<List<Operation>> loadAllOperations();

    @Insert
    public abstract long insertOperation(Operation operation);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateOperation(Operation operation);

    @Delete
    public abstract int deleteOperation(Operation operation);

    @Query("SELECT * FROM operations WHERE id = :id")
    public abstract LiveData<Operation> loadOperationById(int id);

    @Transaction
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
    public long insertOperationWithAnalytic(final Operation operation){
        long id = insertOperation(operation);
        if(id != -1){
            operation.setId((int)id);
            insertBalance(operation.getBalance());
            insertCashflow(operation.getCashflow());
        }
        return id;
    }

    @Transaction
    public int updateOperationWithAnalytic(Operation operation){
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

}
