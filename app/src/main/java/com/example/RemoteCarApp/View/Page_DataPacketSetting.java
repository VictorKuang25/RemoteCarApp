package com.example.RemoteCarApp.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.RemoteCarApp.DataType;
import com.example.RemoteCarApp.Database.DataPacket;
import com.example.RemoteCarApp.Database.DataPacketDao;
import com.example.RemoteCarApp.Database.DataPacketDatabase;
import com.example.RemoteCarApp.R;
import com.example.RemoteCarApp.View.DataPacketSetting.DataPacketSettingRecyclerViewAdapter;

import java.util.List;

public class Page_DataPacketSetting extends Fragment {

    DataPacketDatabase dataPacketDatabase;
    DataPacketDao dataPacketDao;
    DataPacket[] dataPackets = new DataPacket[256];

    //-------------------
    RecyclerView rv_rx_data_setting;
    Button btn_rx_add;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.page_transport_setting,container,false);

        dataPacketDatabase = Room.databaseBuilder(requireContext(), DataPacketDatabase.class, "data_packet_database").allowMainThreadQueries().build();
        dataPacketDao = dataPacketDatabase.getDataPacketDao();

        rv_rx_data_setting = view.findViewById(R.id.rv_rx_data_setting);
        btn_rx_add = view.findViewById(R.id.btn_rx_add);

        DataPacketSettingRecyclerViewAdapter adapter = new DataPacketSettingRecyclerViewAdapter(dataPackets);

        rv_rx_data_setting.setAdapter(adapter);
        rv_rx_data_setting.setHasFixedSize(true);
        rv_rx_data_setting.setLayoutManager(new LinearLayoutManager(requireContext()));

        btn_rx_add.setOnClickListener(v -> {
            DataPacket dataPacket = new DataPacket(16, DataType.FLOAT,"name");
            insertDataPacket(dataPacket);
        });
//        btn_clear.setOnClickListener(v -> {
//            deleteAllDataPackets();
//        });

        return view;
    }




    //-------------------
    public void insertDataPacket(DataPacket dataPacket) {
        if(dataPacket.getDataCode() > 256) toast("index > 256");
        else if(dataPackets[dataPacket.getDataCode()] != null) toast(dataPacket.getDataCode() + " index exist");
        else dataPacketDao.insertDataPacket(dataPacket);
        updateDataPackets();
    }
//
    public void updateDataPacket(DataPacket dataPacket) {

    }

    public void deleteAllDataPackets() {
        dataPacketDao.deleteAllDataPackets();
        updateDataPackets();
    }

    //-------------------
    public void updateDataPackets() {
        List<DataPacket> list = dataPacketDao.getAllDataPackets();

        dataPackets = new DataPacket[256];
        for(DataPacket dataPacket : list) dataPackets[dataPacket.getDataCode()] = dataPacket;

        updateView();
    }

    //-------------------
    public void updateView() {
        for(DataPacket dataPacket : dataPackets){
            if(dataPacket == null) continue;
        }
    }

    public void toast(String s){
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
    }

}
