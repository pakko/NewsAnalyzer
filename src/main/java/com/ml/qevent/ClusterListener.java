package com.ml.qevent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.ml.db.MongoDB;
import com.ml.task.HadoopClusterTask;

public class ClusterListener implements QueueListener {

	private MongoDB mongodb;
	private ExecutorService service;
	private int count;
	
	public ClusterListener(MongoDB mongodb, ExecutorService service) {
		this.mongodb = mongodb;
		this.service = service;
		this.count = 0;
	}

	@Override
	public void queueEvent(QueueEvent event) {
		if (event.getQueueState() != null 
				&& event.getQueueState().equals("take_cluster")
				&& count == 0) {
			HadoopClusterTask it = new HadoopClusterTask(mongodb);
			count = 1;
			service.execute(it);
			waitForComplete(service, 1);
			count = 0;
        }
	}
	
	private void waitForComplete(ExecutorService executor, int hour) {
		try {  
            boolean loop = true;  
            while(loop) {    //等待所有任务完成  
                loop = !executor.awaitTermination(hour, TimeUnit.HOURS);
            }
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
	}

}
