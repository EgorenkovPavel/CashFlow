package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.entites.OperationWithData;

import java.util.List;

public class OperationListViewModel extends AndroidViewModel {

    private Repository mRepository;
    private LiveData<List<OperationWithData>> operations;

    public OperationListViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        operations = mRepository.loadOperationWithData();
    }

    public LiveData<List<OperationWithData>> getOperations() {
        return operations;
    }
}
