package com.crocobizness.laba2.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crocobizness.laba2.R;
import com.crocobizness.laba2.database.AudioRecord;
import com.crocobizness.laba2.observer.EventListener;
import com.crocobizness.laba2.observer.ExoPlayerEventObserver;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static com.crocobizness.laba2.ui.MainActivity.getRecordTime;

public class AudioRecordsAdapter extends RecyclerView.Adapter<AudioRecordsAdapter.ViewHolder> {

    private List<AudioRecord> records;
    private LayoutInflater layoutInflater;
    private ExoPlayerEventObserver observer;

    public void setObserver(ExoPlayerEventObserver observer) {
        this.observer = observer;
    }

    public interface Listener {
        void onClick(View view);
    }


    private Listener listener;

    AudioRecordsAdapter(Context context,Listener listener){
        layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
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
            holder.play.setTag(current);
            holder.play.setOnClickListener(this::onClick);
            holder.name.setText(current.getName());
            holder.timeEnd.setText(current.getTime());
            holder.timeStart.setText("0:00");
            observer.subscribe(position,
                    (EventListener<ExoPlayerEventObserver>) (eventType, t) -> {
                holder.seekBar.setProgress((int) ((t.getPosition()*100)/t.getDuration()));
                holder.timeStart.setText(stringForTime((int) t.getPosition()));
            });
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
        private Button play;
        private SeekBar seekBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeEnd = itemView.findViewById(R.id.audio_item_time_end);
            timeStart = itemView.findViewById(R.id.audio_item_time_start);
            name = itemView.findViewById(R.id.audio_item_name);
            play = itemView.findViewById(R.id.audio_item_btnPlay);
            seekBar = itemView.findViewById(R.id.audio_item_seek_bar);
        }
    }

    private void onClick(View view){
        listener.onClick(view);
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

}
