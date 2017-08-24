package com.epipasha.cashflow.objects;

import android.content.ContentValues;
import android.content.Context;

import com.epipasha.cashflow.db.CashFlowDbManager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Pavel on 09.01.2017.
 */

public abstract class DBObject implements Serializable {

    private int ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public abstract ContentValues getValues();

    public static String getTableName() {
        return "";
    }

    public static void delete(DBObject dbObject, Context context) {
        int res = CashFlowDbManager.getInstance(context).delete(dbObject);
        dbObject = null;
    };

    public void write(Context context){
        if(getID()==0){
            int id = CashFlowDbManager.getInstance(context).add(this);
            setID(id);
        }else{
            CashFlowDbManager.getInstance(context).update(this);
        }
    };

    public static ArrayList<DBObject> get(){
        return null;
    };

}
