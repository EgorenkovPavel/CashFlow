package com.epipasha.cashflow;

import android.widget.Spinner;

import com.epipasha.cashflow.data.entites.IEntity;

public class Utils {

    public static void setPositionById(Spinner spinner, int rowId){
        for (int i = 0; i < spinner.getCount(); i++) {
            IEntity value = (IEntity) spinner.getItemAtPosition(i);
            int id = value.getId();
            if (id == rowId) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public static int getSelectedId(Spinner spinner){
        IEntity entity = (IEntity) spinner.getSelectedItem();
        if (entity == null){
            return -1;
        } else {
            return entity.getId();
        }
    }
}
