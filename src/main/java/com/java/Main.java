package com.java;

import java.util.Collections;
import java.util.List;

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

    public static void main(String[] args){
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

        InnerData topScoreToday = childrens.get(0).data;  //top scoring article from today

        List<String> summary = new PyTeaser().SummarizeUrl(topScoreToday.url);

        for(String s : summary){
            s = s.replace('\n', '\0');
                System.out.println(s);
        }

        System.out.println("Success");
     }
}
