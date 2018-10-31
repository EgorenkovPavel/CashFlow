package com.epipasha.cashflow.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.Backuper;
import com.epipasha.cashflow.data.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class BackupViewModel extends AndroidViewModel {

    private Repository mRepository;

    private LiveData<Status> mStatus;

    public BackupViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;
    }

    public void fileBackup(){
        AppExecutors mAppExecutors = AppExecutors.getInstance();
        mAppExecutors.discIO().execute(new Runnable() {
            @Override
            public void run() {
                String data = Backuper.backupRoomDb(getApplication());
                backupToDisc(data);
            }
        });
    }

    public void fileRestore(){
        AppExecutors mAppExecutors = AppExecutors.getInstance();
        mAppExecutors.discIO().execute(new Runnable() {
            @Override
            public void run() {
                String data = restoreFromDisc();
                Backuper.restoreRoomDb(getApplication(), data);
            }
        });
    }

    private void backupToDisc(String data) {
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File file = new File(root.getAbsolutePath(), "myData.txt");
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String restoreFromDisc() {
        String data = "";
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File file = new File(root.getAbsolutePath(), "myData.txt");
            FileInputStream is = new FileInputStream(file);
            int size = is.available();


            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            data = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    enum Status{

    }

}
