package com.epipasha.cashflow.data;

import android.arch.persistence.room.TypeConverter;

import com.epipasha.cashflow.objects.OperationType;

import java.util.Date;

public class DataConverter {

    @TypeConverter
    public static Date toDate(Long timestamp){
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Integer toInt(OperationType type){
        return type == null ? null : type.toDbValue();
    }

    @TypeConverter
    public static OperationType toOperationType(Integer value){
        return value == null ? null : OperationType.toEnum(value);
    }

}
