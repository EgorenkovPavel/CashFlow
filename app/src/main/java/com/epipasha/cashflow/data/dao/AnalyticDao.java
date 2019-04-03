package com.epipasha.cashflow.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Query;

import com.epipasha.cashflow.data.objects.OperationType;

import java.util.List;

@Dao
public interface AnalyticDao {

    @Query("SELECT cashflow.month, "
            + "cashflow.year, "
            + "categories.type, "
            + "SUM(cashflow.sum) as sum "
            + "FROM cashflow "
            + "INNER JOIN categories "
            + "ON cashflow.category_id = categories.id "
            + "WHERE category_id = :categoryId "
            + "GROUP BY month, year, type "
            + "ORDER BY year, month")
    LiveData<List<MonthCashflow>> loadMonthCashflow(int categoryId);

    @Query("SELECT cashflow.month, "
            + "cashflow.year, "
            + "categories.type, "
            + "SUM(cashflow.sum) as sum "
            + "FROM cashflow "
            + "INNER JOIN categories "
            + "ON cashflow.category_id = categories.id "
            + "GROUP BY month, year, type "
            + "ORDER BY year, month")
    LiveData<List<MonthCashflow>> loadAllMonthCashflow();

    @Query("SELECT categories.title, "
            + "SUM(cashflow.sum) as sum "
            + "FROM cashflow "
            + "INNER JOIN categories "
            + "ON cashflow.category_id = categories.id "
            + "WHERE cashflow.month = :month "
            + "AND cashflow.year = :year "
            + "AND categories.type = :type "
            + "GROUP BY title ")
    LiveData<List<CategoryCashflow>> loadCategoryCashflow(int year, int month, OperationType type);


    class CategoryCashflow{

        @ColumnInfo(name = "title")
        private String title;
        @ColumnInfo(name = "sum")
        private int sum;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }
    }


    class MonthCashflow{

        @ColumnInfo(name = "month")
        private int month;
        @ColumnInfo(name = "year")
        private int year;
        @ColumnInfo(name = "type")
        private OperationType type;
        @ColumnInfo(name = "sum")
        private int sum;

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public OperationType getType() {
            return type;
        }

        public void setType(OperationType type) {
            this.type = type;
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }
    }
}
