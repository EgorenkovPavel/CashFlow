package com.epipasha.cashflow.data.objects;

import java.util.Date;

public class Operation {

    private int id;
    private Date date;
    private OperationType type;
    private Account account;
    private Category category;
    private Account recAccount;
    private int sum;

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

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Account getRecAccount() {
        return recAccount;
    }

    public void setRecAccount(Account recAccount) {
        this.recAccount = recAccount;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
