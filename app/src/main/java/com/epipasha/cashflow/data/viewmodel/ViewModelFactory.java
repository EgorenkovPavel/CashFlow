package com.epipasha.cashflow.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.LocalDataSource;
import com.epipasha.cashflow.data.Repository;

public class ViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;
    private final Repository mRepository;

    public static ViewModelFactory getInstance(Application application) {

        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application,
                            Repository.getInstance(LocalDataSource.getInstance(AppDatabase.getInstance(application))));
                }
            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(@NonNull Application application, Repository repository) {
        super(application);
        mApplication = application;
        mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == AccountDetailViewModel.class)
            return (T) new AccountDetailViewModel(mApplication, mRepository);
        else if (modelClass == CategoryDetailViewModel.class)
            return (T) new CategoryDetailViewModel(mApplication, mRepository);
        else if (modelClass == OperationDetailViewModel.class)
            return (T) new OperationDetailViewModel(mApplication, mRepository);
        else if (modelClass == OperationMasterViewModel.class)
            return (T) new OperationMasterViewModel(mApplication);//, mRepository);

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}