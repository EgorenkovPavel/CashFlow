package com.epipasha.cashflow.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.epipasha.cashflow.objects.OperationType;

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
            + "ORDER BY year, month")
    LiveData<List<MonthCashflow>> loadAllMonthCashflow();

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
