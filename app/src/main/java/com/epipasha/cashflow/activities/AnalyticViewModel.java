package com.epipasha.cashflow.activities;

import android.app.Application;

import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.dao.AnalyticDao;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class AnalyticViewModel extends AndroidViewModel {

    private Repository mRepository;

    private LiveData<List<AnalyticDao.MonthCashflow>> mMonthCashflow;
    private LiveData<List<AnalyticDao.CategoryCashflow>> mMonthOutCashflow;

    public AnalyticViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        mMonthCashflow = mRepository.loadAllMonthCashflow();

        Calendar cal = Calendar.getInstance();

        mMonthOutCashflow = mRepository.loadCategoryCashflow(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                OperationType.OUT);
    }

    public LiveData<List<AnalyticDao.MonthCashflow>> getMonthCashflow() {
        return mMonthCashflow;
    }

    public LiveData<List<AnalyticDao.CategoryCashflow>> getMonthOutCashflow() {
        return mMonthOutCashflow;
    }
}
