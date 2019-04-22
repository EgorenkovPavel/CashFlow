package com.epipasha.cashflow.data.objects;

public class Account {

    private int id;
    private String title;
    private int sum;

    public Account() {
    }

    public Account(int id, String title, int sum) {
        this.id = id;
        this.title = title;
        this.sum = sum;
    }

    public int getId() {
        return id;
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
