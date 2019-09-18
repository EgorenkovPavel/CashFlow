package com.epipasha.cashflow.data.complex;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

@DatabaseView("SELECT accounts.id as id, "
        + "accounts.title as title, "
        + "balance.sum as sum "
        + "FROM accounts as accounts "
        + "LEFT OUTER JOIN "
        + "(SELECT "
        + "balance.account_id as account_id, "
        + "SUM(balance.sum) as sum "
        + "FROM balance "
        + "GROUP BY balance.account_id) as balance "
        + "ON accounts.id = balance.account_id ")
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

}
