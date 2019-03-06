package com.epipasha.cashflow.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.CategoryWithCashflow;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Date;
import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY title")
    LiveData<List<Category>> loadAllCategories();

    @Query("SELECT * FROM categories WHERE parent_id is null AND type =:type ORDER BY title")
    List<Category> getParentCategories(OperationType type);

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

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY title")
    List<Category> getAllCategoriesByType(OperationType type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(int id);
}
