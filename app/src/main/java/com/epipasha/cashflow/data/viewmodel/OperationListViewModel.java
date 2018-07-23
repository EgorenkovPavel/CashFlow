package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.entites.Operation;

import java.util.List;

public class OperationListViewModel extends AndroidViewModel {

    private LiveData<List<Operation>> operations;

    public OperationListViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(this.getApplication());
        operations = db.operationDao().loadAllOperations();
    }

    public LiveData<List<Operation>> getOperations() {
        return operations;
    }
}
