package com.example.RemoteCarApp.View.DataPacketSetting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.RemoteCarApp.Database.DataPacket;
import com.example.RemoteCarApp.R;

public class DataPacketSettingRecyclerViewAdapter extends RecyclerView.Adapter<DataPacketSettingRecyclerViewAdapter.DataPacketSettingViewHolder> {

    private DataPacket[] dataPackets;
    private Context context;

    public DataPacketSettingRecyclerViewAdapter(DataPacket[] dataPackets) {
        this.dataPackets = dataPackets;
    }

    @NonNull
    @Override
    public DataPacketSettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.datapacket_setting_row, parent, false);
        return new DataPacketSettingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataPacketSettingViewHolder holder, int position) {
        Integer[] a = {3,5,9,1,3,5,0,5,7};
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<>(this.context, R.layout.support_simple_spinner_dropdown_item, a);
        holder.spinner.setAdapter(arrayAdapter);
    }

    @Override
    public int getItemCount() {
        return dataPackets.length;
    }


    //---------------------------------------------------------------------------------------------------
    public static class DataPacketSettingViewHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public Spinner spinner;

        public DataPacketSettingViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.spinner = itemView.findViewById(R.id.spinner);
        }
    }
}
