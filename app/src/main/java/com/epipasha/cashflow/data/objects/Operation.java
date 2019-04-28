package com.epipasha.cashflow.data.objects;

import androidx.annotation.Nullable;

import com.google.firebase.database.annotations.NotNull;

import java.util.Date;

public class Operation {

    private int id;
    private Date date;
    private OperationType type;
    @NotNull
    private Account account;
    @Nullable
    private Category category;
    @Nullable
    private Account recAccount;
    private int sum;

    public Operation(int id, Date date, OperationType type, @NotNull Account account, @Nullable Category category, @Nullable Account recAccount, int sum) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.account = account;
        this.category = category;
        this.recAccount = recAccount;
        this.sum = sum;
    }

    public Operation(Date date, OperationType type, @NotNull Account account, @Nullable Category category, @Nullable Account recAccount, int sum) {
        this.date = date;
        this.type = type;
        this.account = account;
        this.category = category;
        this.recAccount = recAccount;
        this.sum = sum;
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

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(@NotNull Account account) {
        this.account = account;
    }

    @Nullable
    public Category getCategory() {
        return category;
    }

    public void setCategory(@Nullable Category category) {
        this.category = category;
    }

    @Nullable
    public Account getRecAccount() {
        return recAccount;
    }

    public void setRecAccount(@Nullable Account recAccount) {
        this.recAccount = recAccount;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
