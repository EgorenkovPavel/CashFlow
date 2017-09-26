package com.epipasha.cashflow.objects;

import android.content.ContentValues;

import com.epipasha.cashflow.db.CashFlowDbHelper;

import java.io.Serializable;

public class Account implements Serializable {
    private int ID;
    private String name;

    // not from db
    private int balance = 0;

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }



    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Account){
            return ((Account) obj).getID()==ID;
        }else{
            return super.equals(obj);
        }
    }

    /*
    @Override
    public ContentValues getValues() {
        ContentValues values = new ContentValues(2);

        values.put(CashFlowDbHelper.ACCOUNT_NAME, getName());
        values.put(CashFlowDbHelper.ACCOUNT_CURRENCY_ID, getCurrency().getId());

        return values;
    }

    @Override
    public String getTableName() {
        return CashFlowDbHelper.TABLE_ACCOUNT;
    }

    */
}
