package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class ModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final int id;
    private Application application;

    public ModelFactory(@NonNull Application application, int id) {
        super(application);
        this.application = application;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == AccountDetailViewModel.class)
            return (T) new AccountDetailViewModel(application, id);
        else if (modelClass == CategoryDetailViewModel.class)
            return (T) new CategoryDetailViewModel(application, id);
        else if (modelClass == OperationDetailViewModel.class)
            return (T) new OperationDetailViewModel(application, id);

        return null;
    }
}