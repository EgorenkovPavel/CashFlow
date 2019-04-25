package com.epipasha.cashflow.activities.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.CategoryEntity;
import com.epipasha.cashflow.data.objects.Category;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CategoryViewModel extends AndroidViewModel{

    private Repository mRepository;

    private MutableLiveData<Boolean> shouldClose = new MutableLiveData<>(false);

    private CompositeDisposable mDisposable = new CompositeDisposable();

    private ObservableInt activityTitle = new ObservableInt(R.string.new_category);
    private ObservableField<Category> mCategory = new ObservableField<>(
            new Category("", OperationType.IN));
    private ObservableInt mParentCategoryPosition = new ObservableInt(0);
    private ObservableField<List<Category>> mParentCategories = new ObservableField<>();

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
                }, throwable -> {}));

    }

    //TODO rewrite loading to rx. wait loading and then set parent id

    public ObservableField<Category> getCategory() {
        return mCategory;
    }

    public ObservableInt getActivityTitle() {
        return activityTitle;
    }

    public MutableLiveData<Boolean> getShouldClose() {
        return shouldClose;
    }

    public void setOperationType(OperationType type){
        Category category = mCategory.get();
        if(category == null) return;
        category.setType(type);
    }

    public void saveObject(){
        Category category = mCategory.get();

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
