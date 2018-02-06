package com.epipasha.cashflow.objects;

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

}
