package com.crocobizness.laba2.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeListenerImpl implements ItemSwipeManager.SwipeListener {

    private final AudioRecordsAdapter recordsAdapter;

    public SwipeListenerImpl(@NonNull AudioRecordsAdapter moviesAdapter) {
        this.recordsAdapter = moviesAdapter;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            recordsAdapter.removeItem(position);
        }
    }

}