package com.java;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit.RestAdapter;

import com.java.model.Children;
import com.java.model.InnerData;
import com.java.model.Response;
import com.java.networking.RedditService;
import com.java.util.PyTeaser;

/**
 * Created by derlucci on 7/9/15.
 */
public class Main implements Runnable{
	
	private final static String PROPERTIES_ACCOUNTS = "accounts.properties";
	private final static String PROPERTIES_ACCOUNTS_USERNAME = "jive.username";
	private final static String PROPERTIES_ACCOUNTS_PASSWORD = "jive.password";

	private final static Logger logger = LoggerFactory.getLogger(Main.class);
	
    public static void main(String[] args){
    	demoProperties();
        (new Thread(new Main())).run();
    }
    
    // TODO This is for demonstration purposes. Remove me.
    private static void demoProperties() {
    	Properties accounts = loadProperties(PROPERTIES_ACCOUNTS);
    	logger.debug("Username: "+accounts.getProperty(PROPERTIES_ACCOUNTS_USERNAME));
    	logger.debug("Password: "+accounts.getProperty(PROPERTIES_ACCOUNTS_PASSWORD));
    }
    
    private static Properties loadProperties(String propertyName) {
    	Properties properties = new Properties();
    	try {
    		properties.load(ClassLoader.getSystemResourceAsStream(propertyName));
    	} catch (IOException e) {
    		logger.error("Error loading properties file: "+propertyName+"\n"+e.getMessage());
    	}
    	return properties;
    }

    @Override
    public void run() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://reddit.com")
                .build();
        logger.info("Running");

        RedditService redditService = restAdapter.create(RedditService.class);
        logger.info("Got Data!");
        Response response = redditService.getArticles();

        List<Children> childrens = response.data.children;

        Collections.sort(childrens);

        InnerData topScoreToday = childrens.get(0).data;  //top scoring article from today

        List<String> summary = new PyTeaser().SummarizeUrl(topScoreToday.url);

        for(String s : summary){
            s = s.replace('\n', '\0');
            	logger.info(s);
        }

        logger.info("Success");
     }
}
