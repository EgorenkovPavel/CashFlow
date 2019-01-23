package com.epipasha.cashflow.data.entites;

import androidx.room.ColumnInfo;

public class AccountWithBalance {

    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "sum")
    private int sum;

    public AccountWithBalance(int id, String title, int sum) {
        this.id = id;
        this.title = title;
        this.sum = sum;
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

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
