package com.jobtick.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.Button.ButtonRegular;
import com.jobtick.R;
import com.jobtick.activities.TaskCreateActivity;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    @BindView(R.id.btn_add_image)
    ButtonRegular btnAddImage;
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.btn_send_to_server)
    ButtonRegular btnSendToServer;
    @BindView(R.id.btn_upload_task_detail)
    ButtonRegular btnUploadTaskDetail;
    @BindView(R.id.btn_task_attachment_media_upload)
    ButtonRegular btnTaskAttachmentMediaUpload;
    @BindView(R.id.btn_delete_task)
    ButtonRegular btnDeleteTask;
    @BindView(R.id.btn_delete_task_media)
    ButtonRegular btnDeleteTaskMedia;
    @BindView(R.id.btn_create_draft_task)
    ButtonRegular btnCreateDraftTask;
    @BindView(R.id.btn_update_task)
    ButtonRegular btnUpdateTask;
    @BindView(R.id.btn_get_single_task)
    ButtonRegular btnGetSingleTask;
    @BindView(R.id.btn_list_user_task)
    ButtonRegular btnListUserTask;

    private String imagePath;
    private Uri filePath;

    SessionManager sessionManager;
    private Bitmap bitmap;
    private List<Integer> attachment_id;

    public TaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, view);

        attachment_id = new ArrayList<>();

        sessionManager = new SessionManager(getActivity());





        return view;
    }

    //working
    //create task method
    private void uploadData() {

        String str_category = String.valueOf(1);
        String str_title = "title";
        String str_description = "description";
        String str_location = "sumerpur";
        String str_latitude = "12.23456";
        String str_longitude = "34.876543";
        String str_budget = String.valueOf(1500);
        String str_payment_terms = String.valueOf(1);
        String str_task_type = String.valueOf(1);
        String str_status = String.valueOf(1);
        String str_required_persons = String.valueOf(4);
        String str_due_date = "12/02/2020";
        ((TaskCreateActivity) getActivity()).showProgressDialog();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TASKS_CREATE,
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());


                            ((TaskCreateActivity) getActivity()).showToast("Create Task SuccessFully!!!", getActivity());


                        } catch (JSONException e) {
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();

                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Log.e("intent22", jsonError);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Toast.makeText(getActivity(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                 /*   if (jsonObject_errors.has("email")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                        String email = jsonArray_mobile.getString(0);
                                        parentEdtEmailAddresss.setErrorEnabled(true);
                                        parentEdtEmailAddresss.setError(email);
                                    }
                                    if (jsonObject_errors.has("password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                        String password = jsonArray_mobile.getString(0);
                                        parentEdtPassword.setErrorEnabled(true);
                                        parentEdtPassword.setError(password);
                                    }*/


                                }

                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ((TaskCreateActivity) getActivity()).showToast("Something Went Wrong", getActivity());
                        }
                        Log.e("error", error.toString());
                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();


                map1.put("category", str_category);
                map1.put("title", str_title);
                map1.put("description", str_description);
                map1.put("location", str_location);
                map1.put("latitude", str_latitude);
                map1.put("longitude", str_longitude);
                map1.put("budget", str_budget);
                map1.put("task_type", str_task_type);
                map1.put("payment_terms", str_payment_terms);
                map1.put("status", str_status);
                map1.put("required_persons", str_required_persons);
                map1.put("due_date", str_due_date);
                if (attachment_id.size() != 0) {
                    for (Integer id : attachment_id) {
                        map1.put("attachments", String.valueOf(id));
                    }
                }

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public String getPath(Uri uri) {

        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        Log.e("path", path);
        cursor.close();

        return path;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
                filePath = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imagePath = getPath(data.getData());
                imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));

                Log.e("imagePath++1", imagePath);
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.msg_image_select),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_image_select),
                        Toast.LENGTH_LONG).show();
            }
                /*if (data != null) {
                    Uri contentURI = data.getData();
                   // filePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                        String path = saveImage(bitmap);
                        fileImg = new File(path);
                        imgScreenshot.setImageBitmap(bitmap);
                        cardScreenshot.setVisibility(View.VISIBLE);
                        *//*if ("Y".equals(crop_flag)) {
                            performCrop(Uri.fromFile(file));
                        }*//*

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                    }*/


        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

    }

    //working
    //upload media using
    private void uploadImageWithAmount() {

        //  if(filePath!=null) {
        ((TaskCreateActivity) getActivity()).showProgressDialog();


        Call<String> call;

        if (imagePath != null) {

            File file = new File(imagePath);
            Log.e("AttachmentApi: ", file.getName());
            Log.e("uploadProfile++:11 ", imagePath);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", file.getName(), requestFile);


            call = ApiClient.getClient().getTaskTempAttachmentMediaData(/*"application/x-www-form-urlencoded",*/ "XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);
        } else {
            ((TaskCreateActivity) getActivity()).hideProgressDialog();

            Toast.makeText(getActivity(), "Please select the image", Toast.LENGTH_SHORT).show();
            return;
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ((TaskCreateActivity) getActivity()).hideProgressDialog();
                Log.e("Response", response.toString());
                String strResponse = response.body();
                Log.e("body", strResponse);

                try {
                    JSONObject jsonObject = new JSONObject(strResponse);
                    Log.e("json", jsonObject.toString());
                    if (jsonObject.has("data")) {
                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                        int id = jsonObject_data.getInt("id");
                        attachment_id.add(id);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // Toast.makeText(getActivity(), response.body().toString(), Toast.LENGTH_SHORT).show();
               /* if (response.isSuccessful()) {
                    TaskMedia taskMedia = response.body();


                    Log.e("Response", "" + taskMedia.getMessage());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Log.e("error", jsonObject.toString());
                    } catch (Exception e) {

                    }
                }*/
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ((TaskCreateActivity) getActivity()).hideProgressDialog();
                Log.e("Response", call.toString());
            }
        });


    }

    @OnClick({R.id.image_view, R.id.btn_add_image, R.id.btn_send_to_server, R.id.btn_task_attachment_media_upload, R.id.btn_upload_task_detail, R.id.btn_delete_task, R.id.btn_delete_task_media, R.id.btn_create_draft_task, R.id.btn_update_task, R.id.btn_get_single_task, R.id.btn_list_user_task})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_view:
                break;
            case R.id.btn_add_image:
                if (checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {
                    Intent opengallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(opengallary, "Open Gallary"), 1);
                }
                break;
            case R.id.btn_send_to_server:
                uploadImageWithAmount();
                break;
            case R.id.btn_task_attachment_media_upload:
                taskAttachmentMediaUpload();
                break;
            case R.id.btn_upload_task_detail:
                uploadData();
                break;
            case R.id.btn_delete_task:
                deleteTask();
                break;
            case R.id.btn_delete_task_media:
                deleteTaskMedia();
                break;
            case R.id.btn_create_draft_task:
                createDraftTask();
                break;
            case R.id.btn_update_task:
                updateTask();
                break;
            case R.id.btn_get_single_task:
                getSingleUserTask();
                break;
            case R.id.btn_list_user_task:
                listUserTask();
                break;
        }
    }

    //working
    private void deleteTaskMedia() {

        ((TaskCreateActivity) getActivity()).showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_DELETE_TASK_MEDIA+"/title-15852200400"+"/attachment?media="+"22,23",
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());


                            ((TaskCreateActivity) getActivity()).showToast("Delete SuccessFully!!!", getActivity());


                        } catch (JSONException e) {
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();

                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;

                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Log.e("intent22", jsonError);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Toast.makeText(getActivity(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                 /*   if (jsonObject_errors.has("email")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                        String email = jsonArray_mobile.getString(0);
                                        parentEdtEmailAddresss.setErrorEnabled(true);
                                        parentEdtEmailAddresss.setError(email);
                                    }
                                    if (jsonObject_errors.has("password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                        String password = jsonArray_mobile.getString(0);
                                        parentEdtPassword.setErrorEnabled(true);
                                        parentEdtPassword.setError(password);
                                    }*/


                                }

                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ((TaskCreateActivity) getActivity()).showToast("Something Went Wrong", getActivity());
                        }
                        Log.e("error", error.toString());
                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    //working
    //upload media using
    private void taskAttachmentMediaUpload() {

        //  if(filePath!=null) {
        ((TaskCreateActivity) getActivity()).showProgressDialog();


        Call<String> call;

        if (imagePath != null) {

            File file = new File(imagePath);
            Log.e("AttachmentApi: ", file.getName());
            Log.e("uploadProfile++:11 ", imagePath);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", file.getName(), requestFile);


            call = ApiClient.getClient().getTasKAttachmentMediaUpload("title-15852200400", "XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);
        } else {
            ((TaskCreateActivity) getActivity()).hideProgressDialog();

            Toast.makeText(getActivity(), "Please select the image", Toast.LENGTH_SHORT).show();
            return;
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ((TaskCreateActivity) getActivity()).hideProgressDialog();
                Log.e("Response", response.toString());
                String strResponse = response.body();
                Log.e("body", strResponse);

                try {
                    JSONObject jsonObject = new JSONObject(strResponse);
                    Log.e("json", jsonObject.toString());
                    if (jsonObject.has("data")) {
                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                     //   int id = jsonObject_data.getInt("id");
                       // attachment_id.add(id);
                    }
                    Toast.makeText(getActivity(), "attachment added", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ((TaskCreateActivity) getActivity()).hideProgressDialog();
                Log.e("Response", call.toString());
            }
        });


    }

    //not working
    private void deleteTask() {

        ((TaskCreateActivity) getActivity()).showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_JOB_DELETE+"/"+"title-15853140431",
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());


                            ((TaskCreateActivity) getActivity()).showToast("Delete SuccessFully!!!", getActivity());


                        } catch (JSONException e) {
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();

                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Log.e("intent22", jsonError);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Toast.makeText(getActivity(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                 /*   if (jsonObject_errors.has("email")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                        String email = jsonArray_mobile.getString(0);
                                        parentEdtEmailAddresss.setErrorEnabled(true);
                                        parentEdtEmailAddresss.setError(email);
                                    }
                                    if (jsonObject_errors.has("password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                        String password = jsonArray_mobile.getString(0);
                                        parentEdtPassword.setErrorEnabled(true);
                                        parentEdtPassword.setError(password);
                                    }*/


                                }

                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ((TaskCreateActivity) getActivity()).showToast("Something Went Wrong", getActivity());
                        }
                        Log.e("error", error.toString());
                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }


    //working
    private void updateTask() {

        String str_title = "update_title";
        String str_description = "update_description";
        String str_assignedpersons = String.valueOf(4);
        String str_required_persons = String.valueOf(2);

        ((TaskCreateActivity) getActivity()).showProgressDialog();


        StringRequest stringRequest = new StringRequest(Request.Method.PUT, Constant.URL_UPDATE_TASK + "/"+"title-15852200400",
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());


                            ((TaskCreateActivity) getActivity()).showToast("Update Task SuccessFully!!!", getActivity());


                        } catch (JSONException e) {
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();

                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Log.e("intent22", jsonError);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Toast.makeText(getActivity(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                 /*   if (jsonObject_errors.has("email")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                        String email = jsonArray_mobile.getString(0);
                                        parentEdtEmailAddresss.setErrorEnabled(true);
                                        parentEdtEmailAddresss.setError(email);
                                    }
                                    if (jsonObject_errors.has("password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                        String password = jsonArray_mobile.getString(0);
                                        parentEdtPassword.setErrorEnabled(true);
                                        parentEdtPassword.setError(password);
                                    }*/


                                }

                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ((TaskCreateActivity) getActivity()).showToast("Something Went Wrong", getActivity());
                        }
                        Log.e("error", error.toString());
                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();



                map1.put("title", str_title);
                map1.put("description", str_description);
                map1.put("assignedpersons", str_assignedpersons);
                map1.put("required_persons", str_required_persons);

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    //working
    private void listUserTask() {

        ((TaskCreateActivity) getActivity()).showProgressDialog();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_LIST_USER_TASK + "?page="+"1",
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());


                            ((TaskCreateActivity) getActivity()).showToast("List of Task success!!!", getActivity());


                        } catch (JSONException e) {
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();

                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Log.e("intent22", jsonError);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Toast.makeText(getActivity(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                 /*   if (jsonObject_errors.has("email")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                        String email = jsonArray_mobile.getString(0);
                                        parentEdtEmailAddresss.setErrorEnabled(true);
                                        parentEdtEmailAddresss.setError(email);
                                    }
                                    if (jsonObject_errors.has("password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                        String password = jsonArray_mobile.getString(0);
                                        parentEdtPassword.setErrorEnabled(true);
                                        parentEdtPassword.setError(password);
                                    }*/


                                }

                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ((TaskCreateActivity) getActivity()).showToast("Something Went Wrong", getActivity());
                        }
                        Log.e("error", error.toString());
                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    //working
    private void getSingleUserTask() {

        ((TaskCreateActivity) getActivity()).showProgressDialog();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_GET_SINGLE_TASK + "/"+ "title-15851442704",
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());


                            ((TaskCreateActivity) getActivity()).showToast("List of Task success!!!", getActivity());


                        } catch (JSONException e) {
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();

                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Log.e("intent22", jsonError);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Toast.makeText(getActivity(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                 /*   if (jsonObject_errors.has("email")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                        String email = jsonArray_mobile.getString(0);
                                        parentEdtEmailAddresss.setErrorEnabled(true);
                                        parentEdtEmailAddresss.setError(email);
                                    }
                                    if (jsonObject_errors.has("password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                        String password = jsonArray_mobile.getString(0);
                                        parentEdtPassword.setErrorEnabled(true);
                                        parentEdtPassword.setError(password);
                                    }*/


                                }

                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ((TaskCreateActivity) getActivity()).showToast("Something Went Wrong", getActivity());
                        }
                        Log.e("error", error.toString());
                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    //working
    //create task method
    private void createDraftTask() {

        String str_category = String.valueOf(1);
        String str_title = "title";
        String str_description = "description";
        String str_location = "sumerpur";
        String str_latitude = "12.23456";
        String str_longitude = "34.876543";
        String str_budget = String.valueOf(1500);
        String str_payment_terms = String.valueOf(1);
        String str_task_type = String.valueOf(1);
        String str_status = String.valueOf(0);
        String str_draft = String.valueOf(1);
        ((TaskCreateActivity) getActivity()).showProgressDialog();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TASKS_CREATE,
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());


                            ((TaskCreateActivity) getActivity()).showToast("Create Task SuccessFully!!!", getActivity());


                        } catch (JSONException e) {
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();

                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Log.e("intent22", jsonError);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Toast.makeText(getActivity(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                 /*   if (jsonObject_errors.has("email")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                        String email = jsonArray_mobile.getString(0);
                                        parentEdtEmailAddresss.setErrorEnabled(true);
                                        parentEdtEmailAddresss.setError(email);
                                    }
                                    if (jsonObject_errors.has("password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                        String password = jsonArray_mobile.getString(0);
                                        parentEdtPassword.setErrorEnabled(true);
                                        parentEdtPassword.setError(password);
                                    }*/


                                }

                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ((TaskCreateActivity) getActivity()).showToast("Something Went Wrong", getActivity());
                        }
                        Log.e("error", error.toString());
                        ((TaskCreateActivity) getActivity()).hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();


                map1.put("category", str_category);
                map1.put("title", str_title);
                map1.put("description", str_description);
                map1.put("location", str_location);
                map1.put("latitude", str_latitude);
                map1.put("longitude", str_longitude);
                map1.put("budget", str_budget);
                map1.put("task_type", str_task_type);
                map1.put("payment_terms", str_payment_terms);
                map1.put("status", str_status);
                map1.put("draft", str_draft);

                if (attachment_id.size() != 0) {
                    for (Integer id : attachment_id) {
                        map1.put("attachments", String.valueOf(id));
                    }
                }

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }



}
