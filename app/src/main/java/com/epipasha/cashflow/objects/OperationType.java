package com.epipasha.cashflow.objects;

public enum OperationType {
    IN,
    OUT,
    TRANSFER;

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
