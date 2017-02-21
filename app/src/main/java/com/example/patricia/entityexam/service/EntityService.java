package com.example.patricia.entityexam.service;

import com.example.patricia.entityexam.domain.Entity;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Patricia on 04.02.2017.
 */

public interface EntityService {
    String SERVICE_ENDPOINT = "http://172.25.14.138:3100/";

    @GET("tables")
    Observable<List<Entity>> getEntities();

    @POST("add")
    Observable<Entity> addEntity(@Body Entity e);

    @DELETE("presence/{id}")
    Observable<Entity> deleteEntity(@Path("id") int entityId);

    @POST("presence")
    Observable<Entity> updateEntity(@Body Entity entity);

//    @FormUrlEncoded
//    @PUT("updateEntity2")
//    Observable<Entity> updateEntity2(@Field("id") int entityId, @Field("field1") String field1,@Field("field2") String field2,@Field("field3") String field3);



}
