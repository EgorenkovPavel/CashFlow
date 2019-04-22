package com.epipasha.cashflow.data.entites;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.Nullable;

import com.epipasha.cashflow.data.objects.OperationType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "operations",
        foreignKeys = {
            @ForeignKey(
                    entity = AccountEntity.class,
                    parentColumns = "id",
                    childColumns = {"account_id"},
                    onDelete = ForeignKey.CASCADE),
            @ForeignKey(
                    entity = AccountEntity.class,
                    parentColumns = "id",
                    childColumns = {"recipient_account_id"}),
            @ForeignKey(
                    entity = Category.class,
                    parentColumns = "id",
                    childColumns = "category_id")},
indices = {
        @Index("account_id"),
        @Index("category_id"),
        @Index("recipient_account_id")})
public class Operation{

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "date")
    private Date date;
    @ColumnInfo(name = "type")
    private OperationType type;
    @ColumnInfo(name = "account_id")
    private int accountId;
    @ColumnInfo(name = "category_id")
    @Nullable
    private Integer categoryId;
    @ColumnInfo(name = "recipient_account_id")
    @Nullable
    private Integer recipientAccountId;
    @ColumnInfo(name = "sum")
    private int sum;

    @Ignore
    public Operation(Date date, OperationType type, int accountId, @Nullable Integer categoryId, @Nullable Integer recipientAccountId, int sum) {
        this.date = date;
        this.type = type;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.recipientAccountId = recipientAccountId;
        this.sum = sum;
    }

    public Operation(int id, Date date, OperationType type, int accountId, @Nullable Integer categoryId, @Nullable Integer recipientAccountId, int sum) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.recipientAccountId = recipientAccountId;
        this.sum = sum;
    }

    public List<Balance> getBalance(){
        List<Balance> list = new ArrayList<>();

        switch (getType()){
            case IN: {
                list.add(new Balance(getDate(), getAccountId(), getId(), getSum()));
                break;
            }
            case OUT:{
                list.add(new Balance(getDate(), getAccountId(), getId(), -1*getSum()));
                break;
            }
            case TRANSFER:{
                if(getRecipientAccountId() == null) throw new IllegalArgumentException("Recipient account Id is null");
                list.add(new Balance(getDate(), getAccountId(), getId(), -1*getSum()));
                list.add(new Balance(getDate(), getRecipientAccountId(), getId(), getSum()));
                break;
            }
        }
        return list;
    }

    public List<Cashflow> getCashflow(){

        List<Cashflow> list = new ArrayList<>();
        OperationType type = getType();
        if (type == OperationType.IN || type == OperationType.OUT){
            if(getCategoryId() == null) throw new IllegalArgumentException("Category id is null");
            list.add(new Cashflow(getDate(), getId(), getAccountId(), getCategoryId(), getSum()));
        }

        return list;
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

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(Integer recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

}
