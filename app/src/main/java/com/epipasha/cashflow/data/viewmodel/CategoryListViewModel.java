package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.entites.CategoryWithCashflow;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CategoryListViewModel extends AndroidViewModel {

    private LiveData<List<CategoryWithCashflow>> categories;

    public CategoryListViewModel(@NonNull Application application) {
        super(application);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date end = cal.getTime();

        AppDatabase db = AppDatabase.getInstance(this.getApplication());
        categories = db.categoryDao().loadAllCategoriesWithCashflow(start, end);
    }

    public LiveData<List<CategoryWithCashflow>> getCategories() {
        return categories;
    }
}
