package com.epipasha.cashflow.activities.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.objects.Category;
import com.epipasha.cashflow.data.objects.OperationType;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CategoryViewModel extends AndroidViewModel{

    private Repository mRepository;

    private MutableLiveData<Boolean> shouldClose = new MutableLiveData<>(false);

    private CompositeDisposable mDisposable = new CompositeDisposable();

    private MediatorLiveData<Category> mCurrentCategory = new MediatorLiveData<>();

    private MutableLiveData<Integer> activityTitle = new MutableLiveData<>(R.string.new_category);

    //TODO Rewrite to CategoryObject

    public CategoryViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        activityTitle.postValue(R.string.new_category);

        mCurrentCategory.addSource(new MutableLiveData<>(new Category("", OperationType.IN)),
                category -> mCurrentCategory.setValue(category));
    }

    public void start(int categoryId){

        activityTitle.postValue(R.string.category);

        mCurrentCategory.addSource(mRepository.getCategoryById(categoryId),
                category -> mCurrentCategory.setValue(category));

    }

    //TODO rewrite loading to rx. wait loading and then set parent id

    public LiveData<Integer> getActivityTitle() {
        return activityTitle;
    }

    public MutableLiveData<Boolean> getShouldClose() {
        return shouldClose;
    }

    public LiveData<Category> getCurrentCategory() {
        return mCurrentCategory;
    }

    public void saveObject(){
        Category category = mCurrentCategory.getValue();

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
