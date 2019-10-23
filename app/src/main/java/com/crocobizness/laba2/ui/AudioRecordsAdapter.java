package com.crocobizness.laba2.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crocobizness.laba2.R;
import com.crocobizness.laba2.database.AudioRecord;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.List;

public class AudioRecordsAdapter extends RecyclerView.Adapter<AudioRecordsAdapter.ViewHolder> implements View.OnClickListener {

    private List<AudioRecord> records;
    private LayoutInflater layoutInflater;
    private SimpleExoPlayer player;
    private DataSource.Factory dataSourceFactory;

    AudioRecordsAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
        DefaultTrackSelector selection = new DefaultTrackSelector();
        player = ExoPlayerFactory.newSimpleInstance(context,selection);
        dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Audio recorder"));
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
            holder.play.setTag(position);
            holder.play.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        playAudio(records.get((Integer) view.getTag()));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView timeEnd;
        private TextView timeStart;
        private TextView name;
        private Button play;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeEnd = itemView.findViewById(R.id.audio_item_time_end);
            timeStart = itemView.findViewById(R.id.audio_item_time_start);
            name = itemView.findViewById(R.id.audio_item_name);
            play = itemView.findViewById(R.id.audio_item_btnPlay);
        }
    }

    private void playAudio(AudioRecord audioRecord){
        File audioTrack = new File(audioRecord.getPath());
        Uri trackUri = Uri.fromFile(audioTrack);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .setExtractorsFactory(new DefaultExtractorsFactory())
                .createMediaSource(trackUri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }
}
