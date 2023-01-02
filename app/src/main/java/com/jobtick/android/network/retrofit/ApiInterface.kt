package com.jobtick.android.network.retrofit

import com.jobtick.android.models.event.EventRequest
import com.jobtick.android.network.model.Response
import com.jobtick.android.network.model.request.BlockUserRequest
import com.jobtick.android.network.model.response.BudgetPlansResponse
import com.jobtick.android.network.model.response.NearJobsResponse
import com.jobtick.android.network.model.response.draft.DraftResponse
import com.jobtick.android.network.model.response.skills.SkillsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @Multipart
    @POST("media/temp-attachment")
    fun getTaskTempAttachmentMediaData( // @Header("Content-Type") String content,
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Header("authorization") auth: String?, // @Part("file\"; filename=\"pp.png\" ") RequestBody file);
        @Part file: MultipartBody.Part?
    ): Call<String?>?

    @Multipart
    @POST("tasks/{task-slug}/attachment")
    fun getTasKAttachmentMediaUpload(
        @Path("task-slug") task_slug: String?,
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Header("authorization") auth: String?, // @Part("file\"; filename=\"pp.png\" ") RequestBody file);
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

    @POST("email-verification/resent-otp")
    fun emailResendOtp(
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Body body: RequestBody
    ): Call<String?>?

    @POST("email-verification")
    fun emailOtpVerification(
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Body body: RequestBody
    ): Call<String?>?


    @POST("account/phone_verification")
    fun mobileOtpVerification(
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Body body: RequestBody
    ): Call<String?>?

    @POST("account/phone_send_otp")
    fun mobileSendOtp(
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Body body: RequestBody
    ): Call<String?>?

    @GET("profile/skill/list")
    fun getSkills(
        @Header("X-Requested-With") XMLHttpRequest: String?,
        //@Header("authorization") auth: String?
    ): Call<String?>?

    @GET("profile/skill/all")
    fun getAllSkills(
        @Header("X-Requested-With") XMLHttpRequest: String?,
        //@Header("authorization") auth: String?
    ): Call<String?>?
    @GET("profile/skill/add")
    fun addSkills(
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Body body: RequestBody
        //@Header("authorization") auth: String?
    ): Call<String?>?

    @GET("account/bankaccount")
    fun getBankAccount(
        @Header("X-Requested-With") XMLHttpRequest: String?
    ): Call<String?>?

    @GET("account/billingaddress")
    fun getBillingAddress(
        @Header("X-Requested-With") XMLHttpRequest: String?
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

    // when you want to delete attachment from edit job
    @DELETE("tasks/{task-slug}/attachment")
    fun deleteEditTaskAttachment(
        @Path("task-slug") task_slug: String?,
        @Header("X-Requested-With") XMLHttpRequest: String?,
        @Header("authorization") auth: String?, // @Part("file\"; filename=\"pp.png\" ") RequestBody file);
        @Query("media") attachmentId: Int
    ): Call<String?>?

    @GET("profile/{userId}/reviews/{whoIs}")
    suspend fun reviews(@Path("userId") userId: Int?, @Path("whoIs") whoIs: String?): Response

    @POST("event")
    suspend fun event(
        @Body eventRequest: EventRequest
    ): Response

    @POST("user/block")
    suspend fun blockUser(
        @Body blockUserRequest: BlockUserRequest
    ): Response

    @GET("jobs/map-based")
    suspend fun getNearJobs(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("radius") radius: Int,
        @Query("limit") limit: Int,
        @Query("query") query: String
    ): NearJobsResponse

    @GET("budget-plans")
    suspend fun budgetPlans(
    ): BudgetPlansResponse

    @GET("draft")
    suspend fun draft(
    ): DraftResponse

    @GET("skills")
    suspend fun skills(
        @Query("query") query: String
    ): SkillsResponse
}
