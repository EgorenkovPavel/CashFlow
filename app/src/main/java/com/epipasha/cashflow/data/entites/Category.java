package com.epipasha.cashflow.data.entites;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.epipasha.cashflow.objects.OperationType;

@Entity(tableName = "categories")
public class Category implements IEntity{

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "type")
    private OperationType type;
    @ColumnInfo(name = "budget")
    private int budget;

    @Ignore
    public Category(String title, OperationType type, int budget) {
        this.title = title;
        this.type = type;
        this.budget = budget;
    }

    public Category(int id, String title, OperationType type, int budget) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.budget = budget;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
