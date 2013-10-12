import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import com.ml.db.MongoDB;
import com.ml.model.News;
import com.ml.util.Constants;


public class Test {

	public static void main(String[] args) throws Exception  {
		String confFile = Constants.defaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);

		List<News> newsList = mongodb.findAll(News.class, Constants.newsCollectionName);
		for(News news: newsList) {
			news.setCategoryId(null);
		}
		mongodb.drop(Constants.newsCollectionName);
		mongodb.insert(newsList, Constants.newsCollectionName);
	}
}
