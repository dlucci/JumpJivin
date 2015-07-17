package com.java;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.google.gson.Gson;
import com.java.model.*;
import com.java.model.Response;
import com.java.networking.JiveResponseInterceptor;
import com.java.networking.JiveService;
import com.squareup.okhttp.OkHttpClient;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import com.java.networking.RedditService;
import com.java.util.PyTeaser;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

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

        /*List<String> summary = new PyTeaser().SummarizeUrl(topScoreToday.url);

        for(String s : summary){
            s = s.replace('\n', '\0');
            	logger.info(s);
        }*/

        String ENDPOINT = "https://rosetta.jiveon.com/api/core/v3/contents";

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint("https://rosetta.jiveon.com")
                .setClient(new OkClient(new OkHttpClient())) // use OkHttp
                .setConverter(new JiveResponseInterceptor(new Gson())) // clean up Response

                .setRequestInterceptor(new RequestInterceptor() { // Add HTTP Authorization Header to each Request
                    @Override
                    public void intercept(RequestFacade request) {
                        final String username = "";
                        final String password =  "";
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
        content.type = "idea";
        content.subject = "Test";
        Inners inners = new Inners();
        inners.type = "text/utf8";
        inners.text = "This is a test";
        content.content = inners;
        jiveService.postContent(content, new Callback<Void>() {

            @Override
            public void success(Void aVoid, retrofit.client.Response response) {
                System.out.print("win");
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.print("fail");
            }
        });
    }
}
