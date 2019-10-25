package com.crocobizness.laba2.observer;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicObserver {
    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<EventListener>> listeners = new HashMap<>();

    public void subscribe(int eventType, EventListener listener){
        List<EventListener> users = listeners.get(eventType);
        if (users != null) {
            users.add(listener);
        } else {
            users = new ArrayList<>();
            users.add(listener);
            listeners.put(eventType,users);
        }
    }

    public void unsubscribe(Integer eventType, EventListener listener) throws Exception {
        List<EventListener> users = listeners.get(eventType);
        if (users != null) {
            users.remove(listener);
            if (users.size() == 0){
                listeners.remove(eventType);
            }
        } else {
            throw new Exception("No such element");
        }
    }

    public <T> void notifySubscribers(Integer eventType, T t) throws Exception {
        List<EventListener> users = listeners.get(eventType);
        if (users != null) {
            for (EventListener listener : users) {
                listener.update(eventType, t);
            }
        } else {
            throw new Exception("No such element");
        }
    }
}
