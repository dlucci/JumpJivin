package com.java.networking;

import com.java.model.Response;
import retrofit.http.GET;

/**
 * Created by derlucci on 7/9/15.
 */
public interface RedditService {

    @GET("/r/programming/.json")
    Response getArticles();
}
