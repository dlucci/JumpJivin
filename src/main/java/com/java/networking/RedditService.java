package com.java.networking;

import com.java.model.Response;
import retrofit.http.GET;

import java.util.Properties;

/**
 * Created by derlucci on 7/9/15.
 */
public interface RedditService {

    String end = "/r/programming/.json";

    @GET(end)
    Response getArticles();
}
