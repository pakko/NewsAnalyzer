package com.ml.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.News;
import com.ml.qevent.QueueListenerManager;
import com.ml.util.Constants;

public abstract class ClassifyTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ClassifyTask.class);

	private MongoDB mongodb;
	private QueueListenerManager manager;
	
	public ClassifyTask(MongoDB mongodb, QueueListenerManager manager) {
		this.mongodb = mongodb;
		this.manager = manager;
	}
	
    protected abstract void analyze(List<News> newsList);

	
	@Override
	public void run() {
		//get uncategory news list
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(null));
		List<News> newsList = mongodb.find(query, News.class, Constants.newsCollectionName);		
		logger.info("Analyzing size: " + newsList.size());
		
		if(newsList.size() > 0) {
			analyze(newsList);

			//delete it first, then save
			mongodb.delete(query, Constants.newsCollectionName);
			mongodb.insert(newsList, Constants.newsCollectionName);
			
			logger.info("End of analyzing...");
			
			//notify clustering
			manager.fireWorkspaceCommand("take_cluster");
		}
	}
}
