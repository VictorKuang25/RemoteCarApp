package com.example.RemoteCarApp.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.RemoteCarApp.DataType;

@Entity
@TypeConverters({DataTypeConverter.class})
public class DataPacket {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "data_code")
    public int dataCode;

    @ColumnInfo(name = "data_type")
    public DataType dataType;

    @ColumnInfo(name = "data_name")
    public String dataName;

    public DataPacket(int dataCode, DataType dataType, String dataName) {
        this.dataCode = dataCode;
        this.dataType = dataType;
        this.dataName = dataName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getDataCode() {
        return dataCode;
    }

    public void setDataCode(int dataCode) {
        this.dataCode = dataCode;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataBytes) {
        this.dataType = dataBytes;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }
}
