package com.java.networking;

import com.java.model.Content;
import com.java.model.InnerData;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by derlucci on 7/16/15.
 */
public interface JiveService {

    @POST("/api/core/v3/contents/")
    void postContent(@Body Content stuff, Callback<Void> cb);

}