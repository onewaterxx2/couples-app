package com.example.myp.data.api

import com.example.myp.data.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface ApiService {

    @POST("auth/send-code")
    fun sendCode(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @POST("auth/register")
    fun register(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @POST("auth/login")
    fun login(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @POST("auth/create-couple")
    fun createCouple(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @POST("auth/join-couple")
    fun joinCouple(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @GET("auth/couple-status")
    fun coupleStatus(@Query("coupleId") coupleId: Long): Call<Map<String, Any>>

    @Multipart
    @POST("photos/upload")
    fun uploadPhoto(
        @Part("coupleId") coupleId: Long,
        @Part("userId") userId: Long,
        @Part("description") description: String,
        @Part file: MultipartBody.Part
    ): Call<Map<String, Any>>

    @GET("photos/list")
    fun getPhotos(
        @Query("coupleId") coupleId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<Map<String, Any>>

    @GET("photos/all")
    fun getAllPhotos(@Query("coupleId") coupleId: Long): Call<List<Photo>>

    @POST("photos/{id}/like")
    fun likePhoto(@Path("id") id: Long): Call<Map<String, Any>>

    @HTTP(method = "DELETE", path = "photos/{photoId}", hasBody = false)
    fun deletePhoto(
        @Path("photoId") photoId: Long,
        @Query("userId") userId: Long
    ): Call<Map<String, Any>>

    @POST("messages/send")
    fun sendMessage(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @GET("messages/list")
    fun getMessages(
        @Query("coupleId") coupleId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<Map<String, Any>>

    @GET("messages/all")
    fun getAllMessages(@Query("coupleId") coupleId: Long): Call<List<Message>>

    @POST("moods/set")
    fun setMood(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @GET("moods/today")
    fun getTodayMoods(@Query("coupleId") coupleId: Long): Call<List<Mood>>

    @GET("moods/list")
    fun getAllMoods(@Query("coupleId") coupleId: Long): Call<List<Mood>>

    @POST("tasks/create")
    fun createTask(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @GET("tasks/list")
    fun getTasks(@Query("coupleId") coupleId: Long): Call<Map<String, Any>>

    @POST("tasks/toggle")
    fun toggleTask(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @HTTP(method = "DELETE", path = "tasks/delete", hasBody = false)
    fun deleteTask(@Query("taskId") taskId: Long): Call<Map<String, Any>>

    // 位置共享
    @POST("location/update")
    fun updateLocation(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @GET("location/partner")
    fun getPartnerLocation(
        @Query("userId") userId: Long,
        @Query("coupleId") coupleId: Long
    ): Call<Map<String, Any>>

    @GET("location/couple")
    fun getCoupleLocations(@Query("coupleId") coupleId: Long): Call<Map<String, Any>>

    @POST("location/toggle")
    fun toggleLocationSharing(@Body body: Map<String, Any>): Call<Map<String, Any>>

    @GET("location/status")
    fun getLocationStatus(
        @Query("userId") userId: Long,
        @Query("coupleId") coupleId: Long
    ): Call<Map<String, Any>>
}