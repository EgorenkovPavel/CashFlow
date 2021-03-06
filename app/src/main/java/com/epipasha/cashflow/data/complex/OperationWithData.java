package com.epipasha.cashflow.data.complex;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.epipasha.cashflow.data.entites.AccountEntity;
import com.epipasha.cashflow.data.entites.CategoryEntity;
import com.epipasha.cashflow.data.objects.OperationType;

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
    private AccountEntity account;

    @Embedded(prefix = "category_")
    private CategoryEntity category;

    @Embedded(prefix = "recipient_account_")
    private AccountEntity repAccount;

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

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public AccountEntity getRepAccount() {
        return repAccount;
    }

    public void setRepAccount(AccountEntity repAccount) {
        this.repAccount = repAccount;
    }

}
