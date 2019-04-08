package com.epipasha.cashflow.data.dao;

import com.epipasha.cashflow.data.entites.Budget;
import com.epipasha.cashflow.data.entites.Category;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE category_id =:categoryId")
    List<Budget> getAllBudgetsByCategory(int categoryId);
}
