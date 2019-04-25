package com.epipasha.cashflow.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.epipasha.cashflow.data.entites.CategoryEntity;
import com.epipasha.cashflow.data.complex.CategoryWithCashflow;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY title")
    LiveData<List<CategoryEntity>> loadAllCategories();

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
    LiveData<List<CategoryEntity>> loadAllCategoriesByType(OperationType type);

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY title")
    List<CategoryEntity> getAllCategoriesByType(OperationType type);

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY title")
    LiveData<List<CategoryEntity>> loadCategoriesByType(OperationType type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(CategoryEntity category);

    @Insert
    void insertCategories(List<CategoryEntity> categories);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCategory(CategoryEntity category);

    @Delete
    void deleteCategory(CategoryEntity category);

    @Query("DELETE FROM categories")
    void deleteAll();

    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<CategoryEntity> loadCategoryById(int id);

    @Query("SELECT * FROM categories WHERE id = :id")
    CategoryEntity getCategoryById(int id);

    //RX
    @Query("SELECT * FROM categories WHERE id =:id")
    Flowable<CategoryEntity> getRxCategoryById(int id);

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY title")
    Flowable<List<CategoryEntity>> getRxAllCategoriesByType(OperationType type);

    @Insert
    Completable insertRxCategory(CategoryEntity category);

    @Update
    Completable updateRxCategory(CategoryEntity category);
}
