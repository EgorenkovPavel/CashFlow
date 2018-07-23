package com.epipasha.cashflow.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.epipasha.cashflow.data.dao.AccountDao;
import com.epipasha.cashflow.data.dao.AnalyticDao;
import com.epipasha.cashflow.data.dao.BackupDao;
import com.epipasha.cashflow.data.dao.CategoryDao;
import com.epipasha.cashflow.data.dao.OperationDao;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Balance;
import com.epipasha.cashflow.data.entites.Cashflow;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;

@Database(entities = {Account.class, Category.class, Operation.class, Balance.class, Cashflow.class}, version = 1)
@TypeConverters(DataConverter.class)
public abstract class AppDatabase extends RoomDatabase{

    private static final String DATABASE_NAME = "cashflow";
    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {

        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class,
                        AppDatabase.DATABASE_NAME).build();
            }
        }
        return sInstance;
    }

    public abstract AccountDao accountDao();

    public abstract CategoryDao categoryDao();

    public abstract OperationDao operationDao();

    public abstract BackupDao backupDao();

    public abstract AnalyticDao analyticDao();

}
