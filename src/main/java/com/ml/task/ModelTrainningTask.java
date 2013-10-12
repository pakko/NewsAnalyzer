package com.ml.task;

import java.io.File;

import com.ml.nlp.classify.BernoulliNB;
import com.ml.nlp.classify.IntermediateData;
import com.ml.nlp.classify.MultiNomialNB;
import com.ml.nlp.classify.NaiveBayesClassifier;
import com.ml.util.CompressUtils;
import com.ml.util.Constants;

public class ModelTrainningTask {
	public static void main(String[] args) {
		ModelTrainningTask mtt = new ModelTrainningTask();
		mtt.generateIntermediateData();
		mtt.trainingModel(Constants.defaultMultinomialType);
		mtt.trainingModel(Constants.defaultBernoulliType);
	}
	
	public void generateIntermediateData() {
		try {
			File corpusDir = new File(Constants.defaultCorpusFolder);
			if(!corpusDir.exists()) {
				// uncompress corpus data
				CompressUtils.uncompress(Constants.defaultCorpusZip, Constants.defaultTempDataFolder);
			}
			
			// generate intermediate data
			IntermediateData tdm = new IntermediateData();
	    	tdm.generate(Constants.defaultCorpusFolder, Constants.defaultCorpusEncoding, Constants.defaultIntermediateDataFile);
	    	System.out.println("中间数据生成！");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void trainingModel(String type) {
		NaiveBayesClassifier classifier = null;
		String[] args = null;
		if(type.equals(Constants.defaultBernoulliType)) {
			classifier = new BernoulliNB();
			args = new String[] {Constants.defaultIntermediateDataFile, Constants.defaultBernoulliModelFile};
		}
		else if(type.equals(Constants.defaultMultinomialType)) {
			classifier = new MultiNomialNB();
			args = new String[] {Constants.defaultIntermediateDataFile, Constants.defaultMultinomialModelFile};
		}
		classifier.train(args[0], args[1]);
		System.out.println(type + "训练完毕");
	}
	
	public double testAccuracyWithBernoulli(String type) {
		NaiveBayesClassifier classifier = null;
		String[] args = null;
		if(type.equals(Constants.defaultBernoulliType)) {
			classifier = new BernoulliNB();
			args = new String[] {Constants.defaultCorpusFolder, Constants.defaultCorpusEncoding, Constants.defaultBernoulliModelFile};
		}
		else if(type.equals(Constants.defaultMultinomialType)) {
			classifier = new MultiNomialNB();
			args = new String[] {Constants.defaultCorpusFolder, Constants.defaultCorpusEncoding, Constants.defaultMultinomialModelFile};
		}
		double ret = classifier.getCorrectRate(args[0], args[1], args[2]);
        System.out.println("正确率为：" + ret);
        return ret;
	}
	
	public String classifyDocument(String document, String type) {
		NaiveBayesClassifier classifier = null;
		String args = null;
		if(type.equals(Constants.defaultBernoulliType)) {
			classifier = new BernoulliNB();
			args = Constants.defaultBernoulliModelFile;
		}
		else if(type.equals(Constants.defaultMultinomialType)) {
			classifier = new MultiNomialNB();
			args = Constants.defaultMultinomialModelFile;
		}
		classifier.loadModel(args);

        String result = classifier.classify(document); // 进行分类

        System.out.println("此属于[" + result + "]");
        
        return result;
	}
}
