package com.java;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.java.util.PyTeaser;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

import com.google.gson.Gson;
import com.java.model.Children;
import com.java.model.Content;
import com.java.model.InnerData;
import com.java.model.Inners;
import com.java.model.Response;
import com.java.networking.JiveResponseInterceptor;
import com.java.networking.JiveService;
import com.java.networking.RedditService;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by derlucci on 7/9/15.
 */
public class Main implements Runnable{
	
	private final static String PROPERTIES_JIVE = "jive.properties";
	private final static String PROPERTIES_JIVE_ENDPOINT = "jive.endpoint";
	private final static String PROPERTIES_JIVE_USERNAME = "jive.username";
	private final static String PROPERTIES_JIVE_PASSWORD = "jive.password";
	private final static String PROPERTIES_JIVE_DESTINATION = "jive.destination";

	private final static Logger logger = LoggerFactory.getLogger(Main.class);
	
    public static void main(String[] args){
    	demoProperties();
        (new Thread(new Main())).run();
    }
    
    // TODO This is for demonstration purposes. Remove me.
    private static void demoProperties() {
    	Properties accounts = loadProperties(PROPERTIES_JIVE);
    	logger.debug("Username: "+accounts.getProperty(PROPERTIES_JIVE_USERNAME));
    	logger.debug("Password: "+accounts.getProperty(PROPERTIES_JIVE_PASSWORD));
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
    	Properties properties = loadProperties(PROPERTIES_JIVE);

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

        String data = new String();
        for(int x = 0 ; x < childrens.size(); x++){
            if(summary == null){
                topScoreToday = childrens.get(x).data;
                summary = new PyTeaser().SummarizeUrl(topScoreToday.url);
                continue;
            } else
                break;
        }

        for(String s : summary){
            data = data.concat(s + " ");
        }


        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(properties.getProperty(PROPERTIES_JIVE_ENDPOINT))
                .setClient(new OkClient(new OkHttpClient())) // use OkHttp
                .setConverter(new JiveResponseInterceptor(new Gson())) // clean up Response

                .setRequestInterceptor(new RequestInterceptor() { // Add HTTP Authorization Header to each Request
                    @Override
                    public void intercept(RequestFacade request) {
                    	Properties properties = loadProperties(PROPERTIES_JIVE);
                        final String username = properties.getProperty(PROPERTIES_JIVE_USERNAME);
                        final String password =  properties.getProperty(PROPERTIES_JIVE_PASSWORD);
                        final String credentials = username + ":" + password;
                        Base64 base64 = new Base64();
                        String string = "Basic " + base64.encodeToString(credentials.getBytes());
                        request.addHeader("Accept", "application/json");
                        request.addHeader("Authorization", string);
                    }
                });

        RestAdapter adapter = builder.build();

        JiveService jiveService = adapter.create(JiveService.class);
        Content content = new Content();
        content.parent = properties.getProperty(PROPERTIES_JIVE_DESTINATION);
        content.type = "idea";
        content.subject = topScoreToday.title;
        content.visibility = "place";
        Inners inners = new Inners();
        inners.type = "text/html";
        inners.text = "<a href = \"" + topScoreToday.url + "\">" + data + "</a>";
        content.content = inners;
        logger.debug("Response:\n" + jiveService.postContent(content));
    }
}
