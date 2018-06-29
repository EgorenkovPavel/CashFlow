package com.epipasha.cashflow.objects;

import android.content.Context;

import com.epipasha.cashflow.R;

public enum OperationType {
    IN,
    OUT,
    TRANSFER;

    public static OperationType toEnum(int type){
        switch (type) {
            case 1:
                return IN;
            case 2:
                return OUT;
            case 3:
                return TRANSFER;
            default:
                return null;
        }
    }

    public int toDbValue(){
        switch (this){
            case IN: return 1;
            case OUT: return 2;
            case TRANSFER: return 3;
            default: return 0;
        }
    }

    public String getTitle(Context context){
        switch (this){
            case IN:
                return context.getString(R.string.in);
            case OUT:
                return context.getString(R.string.out);
            case TRANSFER:
                return context.getString(R.string.transfer);
            default:
                return "";
        }
    }

}
