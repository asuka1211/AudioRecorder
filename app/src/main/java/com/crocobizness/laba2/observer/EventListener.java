package com.crocobizness.laba2.observer;

public interface EventListener<T> {
    void update(Integer eventType, T t);
}
