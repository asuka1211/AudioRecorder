package com.crocobizness.laba2.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crocobizness.laba2.R;
import com.crocobizness.laba2.database.entity.AudioRecord;

import java.util.List;


public class AudioRecordsAdapter extends RecyclerView.Adapter<AudioRecordsAdapter.ViewHolder> {

    private List<AudioRecord> records;
    private LayoutInflater layoutInflater;
    public static final int IMG_VIEW = R.id.audio_item_img;
    public static final int CURRENT_AUDIO_RECORD = R.id.current_audio_view;

    public interface Listener {
        void onClick(View view);
        void deleteItem(AudioRecord record);
    }

    public interface AudioPlayBackListener{
        void seekBarStateChange(SeekBar seekBar);
        void currentTimeChange(TextView textView);
    }

    private Listener listener;

    AudioRecordsAdapter(Context context, Listener listener){
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
            holder.play.setTag(IMG_VIEW,holder.imageView);
            holder.play.setTag(CURRENT_AUDIO_RECORD,current);
            holder.play.setOnClickListener(this::onClick);
            holder.name.setText(current.getName());

        }
    }

    @Override
    public int getItemCount() {
        if (records != null){
            return records.size();
        }
        return 0;
    }

    public void removeItem(int position) {
        listener.deleteItem(records.get(position));
    }

    public void setAudioRecords(List<AudioRecord> records){
        this.records = records;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        private TextView name;
        private ImageButton play;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.audio_item_name);
            play = itemView.findViewById(R.id.audio_item_btnPlay);
            imageView = itemView.findViewById(R.id.audio_item_img);
        }
    }

    private void onClick(View view){
        listener.onClick(view);
    }



}
