package com.crocobizness.laba2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crocobizness.laba2.database.AudioRecord;

import java.util.List;

public class AudioRecordsAdaper extends RecyclerView.Adapter<AudioRecordsAdaper.ViewHolder> {

    private List<AudioRecord> records;
    private LayoutInflater layoutInflater;

    AudioRecordsAdaper(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.audio_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (records != null){
            AudioRecord current = records.get(position);
            holder.name.setText(current.getName());
            holder.time.setText(current.getTime());
        }
    }

    @Override
    public int getItemCount() {
        if (records != null){
            return records.size();
        }
        return 0;
    }

    public void setAudioRecords(List<AudioRecord> records){
        this.records = records;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView time;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.audio_item_time);
            name = itemView.findViewById(R.id.audio_item_name);
        }
    }
}
