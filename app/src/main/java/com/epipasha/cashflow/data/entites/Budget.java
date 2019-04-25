package com.epipasha.cashflow.data.entites;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets", foreignKeys = {
        @ForeignKey(
                entity = CategoryEntity.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.CASCADE)},
        indices = {@Index("category_id")})
public class Budget {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "sum")
    private int sum;

    public Budget(int id, Date date, int categoryId, int sum) {
        this.id = id;
        this.date = date;
        this.categoryId = categoryId;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getSum() {
        return sum;
    }
}
