package com.ml.qevent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class QueueListenerManager {

	private Collection listeners;

    /**
     * 添加事件
     * 
     * @param listener
     *            QueueListener
     */
    public void addQueueListener(QueueListener listener) {
        if (listeners == null) {
            listeners = new HashSet();
        }
        listeners.add(listener);
    }

    /**
     * 移除事件
     * 
     * @param listener
     *            QueueListener
     */
    public void removeQueueListener(QueueListener listener) {
        if (listeners == null)
            return;
        listeners.remove(listener);
    }

    /**
     * 触发事件
     */
    public void fireWorkspaceCommand(String command) {
        if (listeners == null)
            return;
        QueueEvent event = new QueueEvent(this, command);
        notifyListeners(event);
    }

    /**
     * 通知所有的QueueListener
     */
    private void notifyListeners(QueueEvent event) {
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
        	QueueListener listener = (QueueListener) iter.next();
            listener.queueEvent(event);
        }
    }
}
