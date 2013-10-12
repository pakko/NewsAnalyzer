package com.ml.qevent;

import java.util.concurrent.ExecutorService;

import com.ml.db.MongoDB;
import com.ml.task.HadoopClusterTask;

public class ClusterListener implements QueueListener {

	private MongoDB mongodb;
	private ExecutorService service;
	
	public ClusterListener(MongoDB mongodb, ExecutorService service) {
		this.mongodb = mongodb;
		this.service = service;
	}

	@Override
	public void queueEvent(QueueEvent event) {
		if (event.getQueueState() != null && event.getQueueState().equals("take_cluster")) {
			HadoopClusterTask it = new HadoopClusterTask(mongodb);
			service.execute(it);
        }
	}

}
