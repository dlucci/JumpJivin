package com.java.networking;

import java.util.Map;

import retrofit.http.Body;
import retrofit.http.POST;

import com.java.model.Content;

/**
 * Created by derlucci on 7/16/15.
 */
public interface JiveService {

    @POST("/api/core/v3/contents/")
    Map<String, Object> postContent(@Body Content stuff);

}