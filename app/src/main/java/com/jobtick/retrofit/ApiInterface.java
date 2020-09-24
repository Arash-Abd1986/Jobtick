package com.jobtick.retrofit;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface ApiInterface {

    @Multipart
    @POST("media/temp-attachment")
    Call<String> getTaskTempAttachmentMediaData(//@Header("Content-Type") String content,
                                                @Header("X-Requested-With") String XMLHttpRequest,
                                                @Header("authorization") String auth,
                                                // @Part("file\"; filename=\"pp.png\" ") RequestBody file);
                                                @Part MultipartBody.Part file);


    @Multipart
    @POST("tasks/{task-slug}/attachment")
    Call<String> getTasKAttachmentMediaUpload(@Path("task-slug") String task_slug,
                                              @Header("X-Requested-With") String XMLHttpRequest,
                                              @Header("authorization") String auth,
                                              // @Part("file\"; filename=\"pp.png\" ") RequestBody file);
                                              @Part MultipartBody.Part file);

    @Multipart
    @POST("profile/portfolio")
    Call<String> getPortfolioMediaUpload(@Header("X-Requested-With") String XMLHttpRequest,
                                         @Header("authorization") String auth,
                                         @Part MultipartBody.Part file);

    @Multipart
    @POST("profile/upload-avatar")
    Call<String> uploadProfilePicture(@Header("X-Requested-With") String XMLHttpRequest,
                                                @Header("authorization") String auth,
                                                @Part MultipartBody.Part file);

    @Multipart
    @POST("chat/send")
    Call<String> getChatMediaUpload(@Header("X-Requested-With") String XMLHttpRequest,
                                         @Header("authorization") String auth,
                                         @Part MultipartBody.Part file,
                                         @Part ("conversation_id") RequestBody conversation_id);


}

