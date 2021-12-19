package com.jobtick.android.network.retrofit

import com.jobtick.android.network.model.Response
import com.jobtick.android.network.model.response.NearJobsResponse
import com.jobtick.android.network.model.response.skills.SkillsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @Multipart
    @POST("media/temp-attachment")
    fun getTaskTempAttachmentMediaData( //@Header("Content-Type") String content,
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Header("authorization") auth: String?,  // @Part("file\"; filename=\"pp.png\" ") RequestBody file);
        @Part file: MultipartBody.Part?
    ): Call<String?>?

    @Multipart
    @POST("tasks/{task-slug}/attachment")
    fun getTasKAttachmentMediaUpload(
        @Path("task-slug") task_slug: String?,
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Header("authorization") auth: String?,  // @Part("file\"; filename=\"pp.png\" ") RequestBody file);
        @Part file: MultipartBody.Part?
    ): Call<String?>?

    @Multipart
    @POST("profile/portfolio")
    fun getPortfolioMediaUpload(
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Header("authorization") auth: String?,
        @Part file: MultipartBody.Part?
    ): Call<String?>?

    @Multipart
    @POST("profile/upload-avatar")
    fun uploadProfilePicture(
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Header("authorization") auth: String?,
        @Part file: MultipartBody.Part?
    ): Call<String?>?

    @POST("profile/update")
    fun uploadProfile(
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Body body: RequestBody
    ): Call<String?>?

    @Multipart
    @POST("chat/send")
    fun sendMessageWithImage(
        @Header("X-Requested-With") XMLHttpRequest: String,
        @Header("authorization") auth: String,
        @Part file: MultipartBody.Part?,
        @Query("conversation_id") conversation_id: String,
        @Query("message") message: String
    ): Call<String>

    @POST("chat/send")
    fun sendMessage(
        @Header("X-Requested-With") XMLHttpRequest: String,
        @Header("authorization") auth: String,
        @Query("conversation_id") conversation_id: String,
        @Query("message") message: String
    ): Call<String>

    //when you want to delete attachment from edit job
    @DELETE("tasks/{task-slug}/attachment")
    fun deleteEditTaskAttachment(
        @Path("task-slug") task_slug: String?,
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Header("authorization") auth: String?,  // @Part("file\"; filename=\"pp.png\" ") RequestBody file);
        @Query("media") attachmentId: Int
    ): Call<String?>?


    @GET("profile/{userId}/reviews/{whoIs}")
    suspend fun reviews(@Path("userId") userId: Int?, @Path("whoIs") whoIs: String?): Response

    @GET("jobs/map-based")
    suspend fun getNearJobs(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("radius") radius: Int,
        @Query("limit") limit: Int
    ): NearJobsResponse

    @GET("skills")
    suspend fun skills(
        @Query("query") query: String
    ): SkillsResponse
}