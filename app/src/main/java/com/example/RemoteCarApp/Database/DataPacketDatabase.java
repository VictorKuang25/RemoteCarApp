package com.example.RemoteCarApp.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DataPacket.class},version = 1,exportSchema = false)
public abstract class DataPacketDatabase extends RoomDatabase {
    public abstract DataPacketDao getDataPacketDao();
}
