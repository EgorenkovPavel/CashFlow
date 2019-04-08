package com.epipasha.cashflow.activities.categories;

import android.app.Application;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;

public class CategoryViewModel extends AndroidViewModel{

    private DataSource mRepository;

    private ObservableInt activityTitle = new ObservableInt(R.string.new_category);
    private ObservableField<Category> mCategory = new ObservableField<>(
            new Category("", OperationType.IN, 0, null));
    private ObservableInt mParentCategoryPosition = new ObservableInt(0);
    private ObservableField<List<Category>> mParentCategories = new ObservableField<>();

    private ObservableBoolean isNew = new ObservableBoolean(true);

    public CategoryViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;
        mParentCategories.set(new ArrayList<>());
    }

    public void start(int categoryId){
        mRepository.getCategoryById(categoryId, new DataSource.GetCategoryCallback() {
            @Override
            public void onCategoryLoaded(Category category) {
                mCategory.set(category);
                isNew.set(false);
                activityTitle.set(R.string.category);
                loadParentCategories();
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    public void start(){
        loadParentCategories();
    }

    private void loadParentCategories(){
        mRepository.getParentCategories(mCategory.get().getType(), new DataSource.GetCategoriesCallback(){

            @Override
            public void onCategoriesLoaded(List<Category> categories) {
                if(!isNew.get()){
                    int id = mCategory.get().getId();
                    for (Category cat:categories) {
                        if (cat.getId() == id){
                            categories.remove(cat);
                            break;
                        }
                    }
                }
                categories.add(0, null);
                mParentCategories.set(categories);
                mParentCategoryPosition.set(getPositionById(categories.toArray(), mCategory.get().getParentId()));
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }
    //TODO rewrite loading to rx. wait loading and then set parent id

    public ObservableInt getParentCategoryPosition() {
        return mParentCategoryPosition;
    }

    public ObservableField<List<Category>> getParentCategories() {
        return mParentCategories;
    }

    public ObservableField<Category> getCategory() {
        return mCategory;
    }

    public ObservableInt getActivityTitle() {
        return activityTitle;
    }

    private int getPositionById(Object[] list, Integer id){

        for (int i=0; i<list.length;i++){
            Object category = list[i];

            if(category == null)
                if(id == null) return i;
                else continue;
            if(((Category)category).getId() == id) {
                return i;
            }
        }
        return 0;
    }

    public void setOperationType(OperationType type){
        Category category = mCategory.get();
        if(category == null) return;
        category.setType(type);
        category.setParentId(null);
        loadParentCategories();
    }

    public void saveObject(){
        Category category = mCategory.get();

        //TODO add check fields
        if(category == null){
            return;
        }

        List<Category> parentCategories = mParentCategories.get();
        int position = mParentCategoryPosition.get();
        if(parentCategories != null) {
            Category parentCategory = parentCategories.get(position);
            if(parentCategory != null)
                category.setParentId(parentCategory.getId());
            else
                category.setParentId(null);
        }

        mRepository.insertCategory(category);
    }
}
