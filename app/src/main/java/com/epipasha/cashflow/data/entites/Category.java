package com.epipasha.cashflow.data.entites;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.epipasha.cashflow.data.objects.OperationType;

@Entity(tableName = "categories",
        foreignKeys = {
            @ForeignKey(
                    entity = Category.class,
                    parentColumns = "id",
                    childColumns = "parent_id")},
        indices = {@Index("parent_id")})
public class Category{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "parent_id")
    private Integer parentId;

    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "type")
    private OperationType type;
    @ColumnInfo(name = "budget")
    private int budget;

    @Ignore
    public Category(String title, OperationType type, int budget, Category parentCategory) {
        this.title = title;
        this.type = type;
        this.budget = budget;

        if(parentCategory == null)
            this.parentId = null;
        else
            this.parentId = parentCategory.getId();
    }

    public Category(int id, String title, OperationType type, int budget, Integer parentId) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.budget = budget;
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
