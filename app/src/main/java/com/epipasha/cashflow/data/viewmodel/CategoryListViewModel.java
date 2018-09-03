package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.CategoryWithCashflow;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CategoryListViewModel extends AndroidViewModel {

    private Repository mRepository;
    private LiveData<List<CategoryWithCashflow>> categories;

    public CategoryListViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date end = cal.getTime();

        categories = mRepository.loadAllCategoriesWithCashflow(start, end);
    }

    public LiveData<List<CategoryWithCashflow>> getCategories() {
        return categories;
    }
}
