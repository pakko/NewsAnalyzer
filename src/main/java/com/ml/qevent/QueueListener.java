package com.ml.qevent;

import java.util.EventListener;

public interface QueueListener extends EventListener {
    public void queueEvent(QueueEvent event);
}
