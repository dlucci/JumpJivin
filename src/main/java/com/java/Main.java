package com.java;

import com.java.model.Children;
import com.java.model.Response;
import com.java.networking.RedditService;
import retrofit.RestAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by derlucci on 7/9/15.
 */
public class Main implements Runnable{

    public static void main(String[] args){
        //System.out.println("Hello World");

        (new Thread(new Main())).run();
    }

    @Override
    public void run() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://reddit.com")
                .build();
        System.out.println("Running");

        RedditService redditService = restAdapter.create(RedditService.class);
        System.out.println("Got Data!");
        Response response = redditService.getArticles();

        List<Children> childrens = response.data.children;

        Collections.sort(childrens);

        System.out.println("biggest score is:  " + childrens.get(0).data.score);
     }
}
