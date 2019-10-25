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

import java.util.Formatter;
import java.util.List;
import java.util.Locale;


public class AudioRecordsAdapter extends RecyclerView.Adapter<AudioRecordsAdapter.ViewHolder> {

    private List<AudioRecord> records;
    private LayoutInflater layoutInflater;
    public static final int VIEW_HOLDER = R.id.view_holder;
    public static final int CURRENT_AUDIO_RECORD = R.id.current_audio_view;

    public interface Listener {
        void onClick(View view);
    }

    public interface AudioPlayBackListener{
        void seekBarStateChange(SeekBar seekBar);
        void currentTimeChange(TextView textView);
    }

    private Listener listener;
    private AudioPlayBackListener playBackListener;

    AudioRecordsAdapter(Context context, Listener listener, AudioPlayBackListener playBackListener){
        layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
        this.playBackListener = playBackListener;
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
            holder.play.setTag(VIEW_HOLDER,holder);
            holder.play.setTag(CURRENT_AUDIO_RECORD,current);
            holder.play.setOnClickListener(this::onClick);
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
        ViewHolder holder = (ViewHolder) view.getTag(VIEW_HOLDER);
        playBackListener.seekBarStateChange(holder.seekBar);
        playBackListener.currentTimeChange(holder.timeStart);
    }



}
