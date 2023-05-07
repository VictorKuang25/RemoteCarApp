package com.example.RemoteCarApp.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DataPacketDao {
    @Insert
    Void insertDataPacket(DataPacket... dataPackets);

    @Update
    Void updateDataPacket(DataPacket... dataPackets);

    @Delete
    Void deleteDataPacket(DataPacket... dataPackets);

    @Query("SELECT * FROM DATAPACKET ORDER BY data_code")
    List<DataPacket> getAllDataPackets();

    @Query("DELETE FROM DATAPACKET")
    Void deleteAllDataPackets();
}
