package com.epipasha.cashflow.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.epipasha.cashflow.data.entites.AccountEntity;
import com.epipasha.cashflow.data.entites.CategoryEntity;
import com.epipasha.cashflow.data.entites.Operation;

import java.util.List;

@Dao
public interface BackupDao {

    @Query("SELECT * FROM accounts")
    List<AccountEntity> loadAllAccounts();

    @Query("SELECT * FROM categories")
    List<CategoryEntity> loadAllCategories();

    @Query("SELECT * FROM operations")
    List<Operation> loadAllOperations();

}
