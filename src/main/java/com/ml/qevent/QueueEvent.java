package com.ml.qevent;

import java.util.EventObject;

public class QueueEvent extends EventObject {

	private static final long serialVersionUID = 6496098798146410884L;

    private String queueState = "";// 表示门的状态，有“开”和“关”两种

    public QueueEvent(Object source, String queueState) {
        super(source);
        this.queueState = queueState;
    }

	public String getQueueState() {
		return queueState;
	}

	public void setQueueState(String queueState) {
		this.queueState = queueState;
	}


}