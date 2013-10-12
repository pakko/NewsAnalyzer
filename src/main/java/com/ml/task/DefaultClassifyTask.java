package com.ml.task;

import java.util.List;

import com.ml.db.MongoDB;
import com.ml.model.News;
import com.ml.nlp.classify.NaiveBayesClassifier;
import com.ml.qevent.QueueListenerManager;

public class DefaultClassifyTask extends ClassifyTask{

	private NaiveBayesClassifier classifier;
	
    public DefaultClassifyTask(MongoDB mongodb, QueueListenerManager manager, NaiveBayesClassifier classifier) {
    	super(mongodb, manager);
    	this.classifier = classifier;
	}

	public void analyze(List<News> newsList) {
		for(News news: newsList) {
			if (news == null || 
					news.getContent() == null || news.getContent().equals(""))
				continue;

			long start = System.currentTimeMillis();
			String result = classifier.classify(news.getContent());// 进行分类
			long end = System.currentTimeMillis();
			long time = (end - start)/1000;
			
			System.out.println("此项属于[" + result + "], 耗时：" + time);
			if(result != null) {
				// 更新该条新闻的类别
				news.setCategoryId(result);
			}
		}
    }
}
