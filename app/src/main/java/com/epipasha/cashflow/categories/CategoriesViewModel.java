package com.epipasha.cashflow.categories;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.complex.CategoryWithCashflow;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {

    private Repository mRepository;
    private LiveData<List<CategoryWithCashflow>> categories;

    public CategoriesViewModel(@NonNull Application application, Repository repository) {
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
