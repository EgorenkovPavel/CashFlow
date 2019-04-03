package com.epipasha.cashflow.data.complex;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Date;

public class OperationWithData {

    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "date")
    private Date date;
    @ColumnInfo(name = "type")
    private OperationType type;
    @ColumnInfo(name = "sum")
    private int sum;

    @Embedded(prefix = "account_")
    private Account account;

    @Embedded(prefix = "category_")
    private Category category;

    @Embedded(prefix = "recipient_account_")
    private Account repAccount;

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Account getRepAccount() {
        return repAccount;
    }

    public void setRepAccount(Account repAccount) {
        this.repAccount = repAccount;
    }

}
