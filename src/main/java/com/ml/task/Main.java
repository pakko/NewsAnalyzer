package com.ml.task;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ml.db.MongoDB;
import com.ml.nlp.classify.MultiNomialNB;
import com.ml.nlp.classify.NaiveBayesClassifier;
import com.ml.qevent.ClusterListener;
import com.ml.qevent.QueueListenerManager;
import com.ml.util.Constants;

public class Main {
	
	public static void main(String[] args) throws Exception  {
		String confFile = Constants.defaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		System.out.println(confFile);

		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		
		long initialDelay = Long.valueOf(props.getProperty("schedule.initial.delay"));
		long delay = Long.valueOf(props.getProperty("schedule.delay"));
		int isSequential = Integer.valueOf(props.getProperty("is.sequential"));
		
		//add listener
		ExecutorService service = Executors.newSingleThreadExecutor();
        QueueListenerManager manager = new QueueListenerManager();
        manager.addQueueListener(new ClusterListener(mongodb, service));
        
		//schedule analyzer to run
		ClassifyTask ct = null;
		if(isSequential == 1) {
			NaiveBayesClassifier classifier = new MultiNomialNB();
			classifier.loadModel(Main.class.getResourceAsStream(Constants.defaultMultinomialModelFile));
			ct = new DefaultClassifyTask(mongodb, manager, classifier);
		}
		else {
			ct = new HadoopClassifyTask(mongodb, manager);
		}
		
        // 从现在开始1分钟之后，每隔1小时执行一次job
		ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();
		scheduledService.scheduleWithFixedDelay(ct, initialDelay, delay, TimeUnit.MINUTES);

	}

}
