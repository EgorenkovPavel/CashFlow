package com.epipasha.cashflow.data.objects;

public class Category {

    private int id;
    private String title;
    private OperationType type;

    public Category(int id, String title, OperationType type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public Category(String title, OperationType type) {
        this.title = title;
        this.type = type;
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

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }
}
