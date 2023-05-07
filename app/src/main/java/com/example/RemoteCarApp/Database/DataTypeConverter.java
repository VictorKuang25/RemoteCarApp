package com.example.RemoteCarApp.Database;

import androidx.room.TypeConverter;

import com.example.RemoteCarApp.DataType;

public class DataTypeConverter {
    @TypeConverter
    public static int fromType(DataType type) {
        return type.ordinal();
    }

    @TypeConverter
    public static DataType toType(int ordinal) {
        return DataType.values()[ordinal];
    }
}
