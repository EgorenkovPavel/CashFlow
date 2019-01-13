package com.epipasha.cashflow.data;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.accounts.AccountViewModel;
import com.epipasha.cashflow.accounts.AccountsViewModel;
import com.epipasha.cashflow.backup.BackupViewModel;
import com.epipasha.cashflow.categories.CategoriesViewModel;
import com.epipasha.cashflow.categories.CategoryViewModel;
import com.epipasha.cashflow.operations.OperationMasterViewModel;
import com.epipasha.cashflow.operations.OperationViewModel;
import com.epipasha.cashflow.operations.OperationsViewModel;

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
        if (modelClass == AccountsViewModel.class)
            return (T) new AccountsViewModel(mApplication, mRepository);
        else if (modelClass == CategoriesViewModel.class)
            return (T) new CategoriesViewModel(mApplication, mRepository);
        else if (modelClass == OperationsViewModel.class)
            return (T) new OperationsViewModel(mApplication, mRepository);

        else if (modelClass == AccountViewModel.class)
            return (T) new AccountViewModel(mApplication, mRepository);
        else if (modelClass == CategoryViewModel.class)
            return (T) new CategoryViewModel(mApplication, mRepository);
        else if (modelClass == OperationViewModel.class)
            return (T) new OperationViewModel(mApplication, mRepository);
        else if (modelClass == OperationMasterViewModel.class)
            return (T) new OperationMasterViewModel(mApplication, mRepository);
        else if (modelClass == BackupViewModel.class)
            return (T) new BackupViewModel(mApplication, mRepository);

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}