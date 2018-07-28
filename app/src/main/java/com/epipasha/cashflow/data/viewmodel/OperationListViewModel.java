package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.entites.OperationWithData;

import java.util.List;

public class OperationListViewModel extends AndroidViewModel {

    private LiveData<List<OperationWithData>> operations;

    public OperationListViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(this.getApplication());
        operations = db.operationDao().loadOperationWithData();
    }

    public LiveData<List<OperationWithData>> getOperations() {
        return operations;
    }
}
