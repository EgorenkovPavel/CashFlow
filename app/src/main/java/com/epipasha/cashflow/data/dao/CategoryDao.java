package com.epipasha.cashflow.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.CategoryWithCashflow;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Date;
import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY title")
    LiveData<List<Category>> loadAllCategories();

    @Query("SELECT categories.id as id, "
            + "categories.title as title, "
            + "categories.type as type, "
            + "categories.budget as budget,"
            + "cashflow.sum as cashflow  "
            + "FROM categories "
            + "LEFT OUTER JOIN "
            + "(SELECT "
            + "cashflow.category_id, "
            + "SUM(cashflow.sum) as sum "
            + "FROM cashflow as cashflow "
            + "WHERE cashflow.date between :start AND :end "
            + "GROUP BY cashflow.category_id) as cashflow "
            + "ON categories.id = cashflow.category_id "
            + "ORDER BY categories.type, categories.title")
    LiveData<List<CategoryWithCashflow>> loadAllCategoriesWithCashflow(Date start, Date end);

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY title")
    LiveData<List<Category>> loadAllCategoriesByType(OperationType type);

    @Insert
    void insertCategory(Category category);

    @Insert
    void insertCategories(List<Category> categories);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCategory(Category category);

    @Delete
    void deleteCategory(Category category);

    @Query("DELETE FROM categories")
    void deleteAll();

    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> loadCategoryById(int id);
}
