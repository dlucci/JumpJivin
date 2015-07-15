package com.java;

import com.gravity.goose.Article;
import com.gravity.goose.Configuration;
import com.gravity.goose.Goose;
import com.java.model.Children;
import com.java.model.InnerData;
import com.java.model.Response;
import com.java.networking.RedditService;
import com.java.util.PyTeaser;
import org.apache.commons.lang.StringUtils;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import retrofit.RestAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        InnerData topScoreToday = childrens.get(0).data;  //top scoring article from today

        List<String> summary = new PyTeaser().SummarizeUrl("http://nesn.com/2015/07/clay-buchholzs-injury-a-potentially-troubling-development-for-red-sox/");

        System.out.println("Success");
     }
}
