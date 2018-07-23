package com.epipasha.cashflow.data.entites;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "cashflow",
        foreignKeys = {@ForeignKey(entity = Account.class,
        parentColumns = "id",
        childColumns = "account_id",
        onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Operation.class,
                parentColumns = "id",
                childColumns = "operation_id",
                onDelete = ForeignKey.CASCADE)},
indices = {@Index("operation_id"),@Index("account_id"),@Index("category_id")})
public class Cashflow {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "date")
    private Date date;
    @ColumnInfo(name = "day")
    private int day;
    @ColumnInfo(name = "month")
    private int month;
    @ColumnInfo(name = "year")
    private int year;
    @ColumnInfo(name = "operation_id")
    private int operationId;
    @ColumnInfo(name = "account_id")
    private int accountId;
    @ColumnInfo(name = "category_id")
    private int categoryId;
    @ColumnInfo(name = "sum")
    private int sum;

    public Cashflow(int id, Date date, int day, int month, int year, int operationId, int accountId, int categoryId, int sum) {
        this.id = id;
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.operationId = operationId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.sum = sum;
    }

    @Ignore
    public Cashflow(Date date, int operationId, int accountId, int categoryId, int sum) {
        this.date = date;
        this.operationId = operationId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.sum = sum;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        this.day = cal.get(Calendar.DAY_OF_MONTH);
        this.month = cal.get(Calendar.MONTH);
        this.year = cal.get(Calendar.YEAR);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

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

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
