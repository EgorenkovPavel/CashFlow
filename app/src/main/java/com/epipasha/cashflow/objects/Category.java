package com.epipasha.cashflow.objects;

import java.io.Serializable;

public class Category implements Serializable{
    private int ID;
    private String name;
    private OperationType type;
    private int budget;

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Category){
            return ((Category) obj).getID()==ID;
        }else{
            return super.equals(obj);
        }
    }
}
