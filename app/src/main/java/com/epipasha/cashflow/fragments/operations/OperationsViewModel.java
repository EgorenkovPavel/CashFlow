package com.epipasha.cashflow.fragments.operations;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.complex.OperationWithData;

import java.util.List;

public class OperationsViewModel extends AndroidViewModel {

    private Repository mRepository;
    private LiveData<List<OperationWithData>> operations;

    public OperationsViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;

        operations = mRepository.loadOperationWithData();
    }

    public LiveData<List<OperationWithData>> getOperations() {
        return operations;
    }

    public void deleteOperation(final int operationId){
        mRepository.deleteOperationById(operationId, null);
    }

}
