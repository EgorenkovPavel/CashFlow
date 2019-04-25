package com.epipasha.cashflow.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.epipasha.cashflow.data.entites.Budget;

import java.util.List;

@Dao
public interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE category_id =:categoryId")
    List<Budget> getAllBudgetsByCategory(int categoryId);
}
