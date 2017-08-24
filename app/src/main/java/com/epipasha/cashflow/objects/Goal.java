package com.epipasha.cashflow.objects;

import java.io.Serializable;

/**
 * Created by Pavel on 11.12.2016.
 */

public class Goal implements Serializable{

    private int id;
    private String name;
    private int sum;
    private boolean done;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
