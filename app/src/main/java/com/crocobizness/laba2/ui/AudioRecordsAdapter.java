package com.crocobizness.laba2.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crocobizness.laba2.R;
import com.crocobizness.laba2.database.AudioRecord;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;
import java.util.List;

public class AudioRecordsAdapter extends RecyclerView.Adapter<AudioRecordsAdapter.ViewHolder> {

    private List<AudioRecord> records;
    private LayoutInflater layoutInflater;
    private SimpleExoPlayer player;

    AudioRecordsAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
        player = ExoPlayerFactory.newSimpleInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.audio_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (records != null){
            AudioRecord current = records.get(position);
            holder.name.setText(current.getName());
            holder.timeEnd.setText(current.getTime());
            holder.timeStart.setText("0:00");
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

        private TextView timeEnd;
        private TextView timeStart;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeEnd = itemView.findViewById(R.id.audio_item_time_end);
            timeStart = itemView.findViewById(R.id.audio_item_time_start);
            name = itemView.findViewById(R.id.audio_item_name);
        }
    }

    private void playAudio(AudioRecord audioRecord){
        File audioTrack = new File(audioRecord.getPath());
        Uri trackUri = Uri.fromFile(audioTrack);
        MediaSource mediaSource =
    }
}
