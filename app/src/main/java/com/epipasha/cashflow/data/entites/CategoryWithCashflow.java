package com.epipasha.cashflow.data.entites;

import androidx.room.ColumnInfo;

import com.epipasha.cashflow.objects.OperationType;

public class CategoryWithCashflow {

    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "parent_id")
    private Integer parentId;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "type")
    private OperationType type;
    @ColumnInfo(name = "budget")
    private int budget;
    @ColumnInfo(name = "cashflow")
    private int cashflow;

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getCashflow() {
        return cashflow;
    }

    public void setCashflow(int cashflow) {
        this.cashflow = cashflow;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
