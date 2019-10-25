package com.crocobizness.laba2.observer;

public class ExoPlayerEventObserver extends BasicObserver {

    private long position;
    private long duration;

    public ExoPlayerEventObserver(String... strings){
        super();
    }

    public void updateProgressBar(){
        try {
            notifySubscribers(PROGRESS_CHANGE,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
