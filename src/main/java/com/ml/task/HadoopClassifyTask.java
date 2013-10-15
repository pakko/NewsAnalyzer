package com.ml.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.db.MongoDB;
import com.ml.model.News;
import com.ml.qevent.QueueListenerManager;
import com.ml.util.CompressUtils;
import com.ml.util.Constants;
import com.ml.util.SSHUtil;
import com.ml.util.ShortUrlUtil;

public class HadoopClassifyTask extends ClassifyTask {
	private static final Logger logger = LoggerFactory.getLogger(HadoopClassifyTask.class);

	private String[] categoryIndex = new String[] {
			"C000007", "C000008", "C000010", "C000013",
			"C000014", "C000016", "C000020", "C000022",
			"C000023", "C000024"
	};
	
    public HadoopClassifyTask(MongoDB mongodb, QueueListenerManager manager) {
    	super(mongodb, manager);
	}
	
	public void analyze(List<News> newsList) {
		try{
			long start = System.currentTimeMillis();
			
			long taskId = System.currentTimeMillis();
			logger.info("taskId：" + taskId + ", news size: " + newsList.size());
	
			String newFileName = Constants.newFileName + taskId;
			String newFileSeq = Constants.newFileSeq + taskId;
			String newFileVectors = Constants.newFileVectors + taskId;
			String newFileResult = Constants.newFileResult + taskId;
			String newFileResultFile = Constants.newFileResultFile + taskId;
			
			String destCompressFilePath = this.compressContent(newsList, taskId, newFileName);
			
			SSHUtil ssh = new SSHUtil();
			ssh.sshLogin(Constants.sshIP, Constants.sshUser, Constants.sshPass);
			
			// 1) upload zip file
			ssh.scpUploadFile(Constants.defaultUploadDir, destCompressFilePath, destCompressFilePath);
			
			// 2) generate scripts
			String scriptPath = this.generateClassifyScripts(taskId, destCompressFilePath,
					newFileName, newFileSeq, newFileVectors, newFileResult, newFileResultFile);
			ssh.scpUploadFile(Constants.defaultUploadDir, scriptPath, scriptPath);
			
			// 3) exec script and get the result
			String destResultFile = Constants.tmpFileDir + newFileResultFile;
			ssh.sshExec("chmod a+x " + Constants.defaultUploadDir + scriptPath + "; nohup sh " + Constants.defaultUploadDir + scriptPath);
			ssh.scpDownloadFile(Constants.defaultUploadDir, newFileResultFile, destResultFile);

			// 4) do clean work
			String cleanScript = this.generateCleanScripts(destCompressFilePath, scriptPath, 
					newFileName, newFileSeq, newFileVectors, newFileResult, newFileResultFile);
			
			ssh.sshExec(cleanScript);
			ssh.sshExit();
			
			String result = FileUtils.readFileToString(new File(destResultFile));
			processResult(newsList, result);
			
			long end = System.currentTimeMillis();
			long time = (end - start) / 1000;
			logger.info("taskId：" + taskId + ", 耗时：" + time);

			FileUtils.forceDelete(new File(destCompressFilePath)); 
			FileUtils.forceDelete(new File(newFileName)); 
			FileUtils.forceDelete(new File(scriptPath)); 
			FileUtils.forceDelete(new File(destResultFile)); 

			
		} catch (FileNotFoundException e) {
			//ignore it, for if code run in jar mode, file could not found
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, Integer> resolveResult(String result) {
		String[] lines = result.split("\n");
		Map<String, Integer> resultMap = new HashMap<String, Integer>(lines.length);
		for(String line: lines) {
			String[] fields = line.split(": ");
			if(fields.length < 4)
				continue;
			resultMap.put(fields[1], Integer.parseInt(fields[3].split(":")[0].substring(1)));
		}
		//System.out.println(resultMap.toString());
		return resultMap;
	}
	
	private String compressContent(List<News> newsList, long taskId, String newFileName) throws Exception {
		//1, mkdir
		File dir = new File(newFileName);
		if( dir.exists() ) {
			FileUtils.forceDelete(dir); 
		}
		dir.mkdir();
		
		//2, write file
		for(News news: newsList) {
			String filePath = dir.getPath() + File.separator + ShortUrlUtil.shortText(news.getUrl())[0] + Constants.fileExt;
			FileUtils.writeStringToFile(new File(filePath), news.getContent(), Constants.defaultFileEncoding);
		}
		
		//3, compress
		String destCompressFilePath = newFileName + Constants.zipFileExt;
		CompressUtils.compress(newFileName, destCompressFilePath);
		
		return destCompressFilePath;
	}
	
	private String generateClassifyScripts(long taskId, String destCompressFilePath, 
			String newFileName, String newFileSeq, String newFileVectors, String newFileResult, String newFileResultFile) throws IOException {
		// 2) generate scripts
		String newFileTFIDFVectors = newFileVectors + "/" + Constants.newFileTFIDFVectors;

		StringBuffer scriptSB = new StringBuffer();
		scriptSB.append("unzip " + Constants.defaultUploadDir + destCompressFilePath + " -d " + Constants.defaultUploadDir + Constants.scriptSeparator);
		scriptSB.append("hadoop fs -put " + Constants.defaultUploadDir + newFileName + " ." + Constants.scriptSeparator);
		scriptSB.append("mahout seqdirectory -ow -i " + newFileName + " -o " + newFileSeq + Constants.scriptSeparator);
		scriptSB.append("mvector -lnorm -nv -ow -wt " + Constants.vectorWeight + " -a " + Constants.vectorTokenAnalyzer
				+ " -i " + newFileSeq + " -o " + newFileVectors
				+ " -dp " + Constants.corpusFileDictionaryFile + " -dfp " + Constants.corpusFileFrequencyFile
				+ " -tfvp " + Constants.corpusFileTFVectors + Constants.scriptSeparator);
		scriptSB.append("mtest -ow -c"
				+ " -m " + Constants.corpusFileModel + " -l " + Constants.corpusFileLabelIndex
				+ " -i " + newFileTFIDFVectors + " -o " + newFileResult + Constants.scriptSeparator);
		scriptSB.append("mahout seqdumper -i " + newFileResult + " -o " + Constants.defaultUploadDir + newFileResultFile + Constants.scriptSeparator);
		//System.out.println(scriptSB.toString());
		
		// 3) upload script
		String scriptPath = Constants.scriptPath + taskId;
		FileUtils.writeStringToFile(new File(scriptPath), scriptSB.toString(), Constants.defaultFileEncoding);
		
		return scriptPath;
	}
	
	private String generateCleanScripts(String destCompressFilePath, String scriptPath,
			String newFileName, String newFileSeq, String newFileVectors, String newFileResult, String newFileResultFile) {
		
		StringBuffer cleanSB = new StringBuffer();
		cleanSB.append("rm -rf " + Constants.defaultUploadDir + newFileName + Constants.scriptSeparator);
		cleanSB.append("rm -rf " + Constants.defaultUploadDir + destCompressFilePath + Constants.scriptSeparator);
		cleanSB.append("hadoop fs -rmr " + newFileName + Constants.scriptSeparator);
		cleanSB.append("hadoop fs -rmr " + newFileSeq + Constants.scriptSeparator);
		cleanSB.append("hadoop fs -rmr " + newFileVectors + Constants.scriptSeparator);
		cleanSB.append("hadoop fs -rmr " + newFileResult + Constants.scriptSeparator);
		cleanSB.append("rm -rf " + Constants.defaultUploadDir + scriptPath + Constants.scriptSeparator);
		cleanSB.append("rm -rf " + Constants.defaultUploadDir + newFileResultFile + Constants.scriptSeparator);
		
		return cleanSB.toString();
	}
	
	private void processResult(List<News> newsList, String result) {
    	Map<String, Integer> resultMap = resolveResult(result);
		//System.out.println("news size " + newsList.size());
		
		for(News news: newsList) {
			String key = ShortUrlUtil.shortText(news.getUrl())[0] + Constants.fileExt;
			int category = resultMap.get(key);
			news.setCategoryId(categoryIndex[category]);
		}
    }
}
