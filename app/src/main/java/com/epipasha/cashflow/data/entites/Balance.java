package com.epipasha.cashflow.data.entites;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "balance",
        foreignKeys = {@ForeignKey(entity = AccountEntity.class,
                                    parentColumns = "id",
                                    childColumns = "account_id",
                                    onDelete = ForeignKey.CASCADE),
                        @ForeignKey(entity = Operation.class,
                                parentColumns = "id",
                                childColumns = "operation_id",
                                onDelete = ForeignKey.CASCADE)},
        indices = {@Index("account_id"),@Index("operation_id")})
public class Balance {

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
    @ColumnInfo(name = "account_id")
    private int accountId;
    @ColumnInfo(name = "operation_id")
    private int operationId;
    @ColumnInfo(name = "sum")
    private int sum;

    public Balance(int id, Date date, int day, int month, int year, int accountId, int operationId, int sum) {
        this.id = id;
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.accountId = accountId;
        this.operationId = operationId;
        this.sum = sum;
    }

    @Ignore
    public Balance(Date date, int accountId, int operationId, int sum) {
        this.date = date;
        this.accountId = accountId;
        this.operationId = operationId;
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

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
