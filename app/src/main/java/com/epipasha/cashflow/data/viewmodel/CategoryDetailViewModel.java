package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.dao.AnalyticDao;
import com.epipasha.cashflow.data.entites.Category;

import java.util.List;

public class CategoryDetailViewModel extends AndroidViewModel {

    private static final int DEFAULT_ID = -1;
    private AppDatabase mDb;
    private int mCategoryId;
    private LiveData<Category> mCategory;
    private LiveData<List<AnalyticDao.MonthCashflow>> mMonthCashflow;

    public CategoryDetailViewModel(@NonNull Application application, int categoryId) {
        super(application);

        mCategoryId = categoryId;

        mDb = AppDatabase.getInstance(application);

        if (mCategoryId == DEFAULT_ID) {
            mCategory = new MutableLiveData<>();
            mMonthCashflow = new MutableLiveData<>();
        }else {
            mCategory = mDb.categoryDao().loadCategoryById(mCategoryId);
            mMonthCashflow = mDb.analyticDao().loadMonthCashflow(mCategoryId);
        }
    }

    public LiveData<Category> getCategory() {
        return mCategory;
    }

    public LiveData<List<AnalyticDao.MonthCashflow>> getMonthCashflow() {
        return mMonthCashflow;
    }

    public void saveObject(final Category category){
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mCategoryId == DEFAULT_ID){
                    mDb.categoryDao().insertCategory(category);
                }else{
                    category.setId(mCategoryId);
                    mDb.categoryDao().updateCategory(category);
                }
            }
        });
    }
}
