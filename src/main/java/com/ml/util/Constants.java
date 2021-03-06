package com.ml.util;

public class Constants {
	
	public static final String separator = "/";
	public static String currentDir = Constants.class.getResource("/").getPath();

	public static final String defaultConfigFile = currentDir + separator + "default.properties";

	public static final String defaultDataFolder = currentDir + separator + "data";
	public static final String stopWordListFolder = defaultDataFolder + separator + "stop_words_list";
	
	public static final String defaultCorpusZip = defaultDataFolder + separator + "TrainingSet.zip";
	public static final String defaultTempDataFolder = currentDir + separator + "data" + separator + "tmp" + separator;
	public static final String defaultCorpusFolder = defaultTempDataFolder + "TrainingSet";

	public static final String defaultIntermediateDataFile = defaultDataFolder + separator + "intermediate.db";
	public static final String defaultBernoulliModelFile = defaultDataFolder + separator + "bernoulli.model";
	public static final String defaultMultinomialModelFile = defaultDataFolder + separator + "multinomial.model";

	public static final String defaultBernoulliType = "bernoulli";
	public static final String defaultMultinomialType = "multinomial";

	
	public static final String defaultCorpusEncoding = "GBK";

	public static final String newsCollectionName = "news";
	public static final String categoryCollectionName = "category";
	public static final String clusterCollectionName = "cluster";
	
    public static final String sshUser = "root";
    public static final String sshPass = "123123";
    public static final String sshIP = "10.74.68.13";
    
    public static final int minAnalyzerNum = 100;
    public static final int minClusterNum = 1000;
    public static final int clusterKvalue = 10;

    public static final int batchAnalyzeSize = 100;
    public static final String fileExt = ".txt";
    public static final String defaultFileEncoding = "UTF8";
    public static final String zipFileExt = ".zip";
    //public static final String defaultUploadDir = "/root/";
    public static final String defaultUploadDir = "/opt/";
    public static final String scriptSeparator = "\n";
    public static final String scriptPath = "script.sh";
    public static final String vectorWeight = "tfidf_update";
    public static final String vectorTokenAnalyzer = "com.chenlb.mmseg4j.analysis.MMSegAnalyzer";

    public static final String newFileName = "newfile";
    public static final String newFileSeq = "newfile-seq";
    public static final String newFileVectors = "newfile-vectors";
    public static final String newFileTFIDFVectors = "tfidf-vectors";

    public static final String corpusFileVectors = "news-vectors";
    public static final String corpusFileDictionaryFile = corpusFileVectors + "/" + "dictionary.file-0";
    public static final String corpusFileFrequencyFile = corpusFileVectors + "/" + "frequency.file-0";
    public static final String corpusFileTFVectors = corpusFileVectors + "/" + "tf-vectors";
    public static final String corpusFileModel = "model";
    public static final String corpusFileLabelIndex = "labelindex";

    public static final String newFileResult = "newfile-result";
    public static final String newFileResultFile = "result.res";

    public static final String distanceMesasure = "org.apache.mahout.common.distance.CosineDistanceMeasure";
    public static final int maxIter = 10;

    public static String tmpFileDir;
    static
	{
		// in windows, need to remove the beginning "/"
		String osType = System.getProperty("os.name").toLowerCase();
		if(osType.indexOf("win") >= 0){
			tmpFileDir = "c:\\";
		}
		else {
			tmpFileDir = "/tmp/";
		}
	}


}
