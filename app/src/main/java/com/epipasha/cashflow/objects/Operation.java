package com.epipasha.cashflow.objects;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;


public class Operation implements Serializable{
    private int ID;
    private Date date;
    private OperationType type;
    private Account account;
    private Category category;
    private Account recipientAccount;
    private int sum;

    public Operation(){
        GregorianCalendar c = new GregorianCalendar();
        date = new Date(c.getTimeInMillis());

        type = OperationType.IN;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public Account getRecipientAccount() {
        return recipientAccount;
    }

    public void setRecipientAccount(Account recipientAccount) {
        this.recipientAccount = recipientAccount;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

}
