package com.epipasha.cashflow.objects;

import android.content.res.Resources;

/**
 * Created by Pavel on 25.09.2016.
 */

public enum OperationType {
    IN,
    OUT,
    TRANSFER;

    @Override
    public String toString() {
        return super.toString();
    }

    public static OperationType toEnum(String type){
        switch (type) {
            case "IN":
                return IN;
            case "OUT":
                return OUT;
            case "TRANSFER":
                return TRANSFER;
            default:
                return null;
        }
    }

}
