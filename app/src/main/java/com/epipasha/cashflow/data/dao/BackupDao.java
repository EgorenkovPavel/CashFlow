package com.epipasha.cashflow.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.epipasha.cashflow.data.entites.AccountEntity;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;

import java.util.List;

@Dao
public interface BackupDao {

    @Query("SELECT * FROM accounts")
    List<AccountEntity> loadAllAccounts();

    @Query("SELECT * FROM categories")
    List<Category> loadAllCategories();

    @Query("SELECT * FROM operations")
    List<Operation> loadAllOperations();

}
