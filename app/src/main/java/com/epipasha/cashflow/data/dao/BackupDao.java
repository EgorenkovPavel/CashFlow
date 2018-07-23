package com.epipasha.cashflow.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;

import java.util.List;

@Dao
public interface BackupDao {

    @Query("SELECT * FROM accounts")
    List<Account> loadAllAccounts();

    @Query("SELECT * FROM categories")
    List<Category> loadAllCategories();

    @Query("SELECT * FROM operations")
    List<Operation> loadAllOperations();

}
