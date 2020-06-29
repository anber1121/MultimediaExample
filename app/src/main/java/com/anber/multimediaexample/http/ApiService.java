package com.anber.multimediaexample.http;


import com.anber.multimediaexample.utils.KitLiveRoomQueryLiveListResponse;
import com.anber.multimediaexample.utils.KitLiveRoomQueryLiveListResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * @author LJ
 * @version 1.0
 * @date 2019-4-2
 * @fileName com.yjsm.host.cloudvideo.common.glide.http
 */
public interface ApiService {

    /**
     * 查询直播间列表
     */
//    @GET("/api/query/v1.0/live/stream")
    @GET("/api/live/v1.0/app/live_streams")
    Observable<KitLiveRoomQueryLiveListResponse> queryLiveList(@Query("limit") int limit, @Query("page") int page);

    @PUT("/api/live/v1.0/app/live_stream")
    Observable<KitLiveRoomQueryLiveListResponse> querylowCode(@Query("room_id") String room_id, @Query("codec") String codec, @Query("template_type") String template_type);

    /**
     * 查询直播间列表
     */
//    @GET("/api/query/v1.0/live/stream")
    @GET("/api/live/v1.0/app/live_streams")
    Observable<KitLiveRoomQueryLiveListResponse> queryKitLiveList(@Query("limit") int limit, @Query("page") int page);

}
