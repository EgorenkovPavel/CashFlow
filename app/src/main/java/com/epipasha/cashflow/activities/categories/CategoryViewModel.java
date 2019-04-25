package com.epipasha.cashflow.activities.categories;

import android.app.Application;
import android.util.Log;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.CategoryEntity;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CategoryViewModel extends AndroidViewModel{

    private Repository mRepository;

    private MutableLiveData<Boolean> shouldClose = new MutableLiveData<>(false);

    private CompositeDisposable mDisposable = new CompositeDisposable();

    private ObservableInt activityTitle = new ObservableInt(R.string.new_category);
    private ObservableField<CategoryEntity> mCategory = new ObservableField<>(
            new CategoryEntity("", OperationType.IN, 0, null));
    private ObservableInt mParentCategoryPosition = new ObservableInt(0);
    private ObservableField<List<CategoryEntity>> mParentCategories = new ObservableField<>();

    private ObservableBoolean isNew = new ObservableBoolean(true);

    //TODO Rewrite to CategoryObject

    public CategoryViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;
        mParentCategories.set(new ArrayList<>());
    }

    public void start(int categoryId){
        mDisposable.add(mRepository.getCategoryById(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(category -> {
                    mCategory.set(category);
                    isNew.set(false);
                    activityTitle.set(R.string.category);
                    loadParentCategories();
                }, throwable -> {}));

    }

    public void start(){
        loadParentCategories();
    }

    private void loadParentCategories(){
        mDisposable.add(mRepository.getParentCategories(mCategory.get().getType())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    if (!isNew.get()) {
                        int id = mCategory.get().getId();
                        for (CategoryEntity cat : categories) {
                            if (cat.getId() == id) {
                                categories.remove(cat);
                                break;
                            }
                        }
                    }
                    categories.add(0, null);
                    mParentCategories.set(categories);
                    mParentCategoryPosition.set(getPositionById(categories.toArray(), mCategory.get().getParentId()));

                }, throwable -> {
                }));

    }
    //TODO rewrite loading to rx. wait loading and then set parent id

    public ObservableInt getParentCategoryPosition() {
        return mParentCategoryPosition;
    }

    public ObservableField<List<CategoryEntity>> getParentCategories() {
        return mParentCategories;
    }

    public ObservableField<CategoryEntity> getCategory() {
        return mCategory;
    }

    public ObservableInt getActivityTitle() {
        return activityTitle;
    }

    public MutableLiveData<Boolean> getShouldClose() {
        return shouldClose;
    }

    private int getPositionById(Object[] list, Integer id){

        for (int i=0; i<list.length;i++){
            Object category = list[i];

            if(category == null)
                if(id == null) return i;
                else continue;
            if(((CategoryEntity)category).getId() == id) {
                return i;
            }
        }
        return 0;
    }

    public void setOperationType(OperationType type){
        CategoryEntity category = mCategory.get();
        if(category == null) return;
        category.setType(type);
        category.setParentId(null);
        loadParentCategories();
    }

    public void saveObject(){
        CategoryEntity category = mCategory.get();

        List<CategoryEntity> parentCategories = mParentCategories.get();
        int position = mParentCategoryPosition.get();
        if(parentCategories != null) {
            CategoryEntity parentCategory = parentCategories.get(position);
            if(parentCategory != null)
                category.setParentId(parentCategory.getId());
            else
                category.setParentId(null);
        }

        mDisposable.add(mRepository.insertOrUpdateCategory(category)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> shouldClose.setValue(true), throwable -> {}));

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
    }
}
