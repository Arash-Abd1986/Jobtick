package com.jobtick.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jobtick.R;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.adapers.AttachmentAdapterEditProfile;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.GeocodeObject;
import com.jobtick.models.UserAccountModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.CameraUtils;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.Tools;
import com.jobtick.widget.ExtendedEntryText;
import com.jobtick.widget.SpacingItemDecoration;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
import timber.log.Timber;

import static com.jobtick.activities.DashboardActivity.onProfileupdatelistenerSideMenu;
import static com.jobtick.fragments.ProfileFragment.onProfileupdatelistener;
import static com.jobtick.utils.Constant.URL_REMOVE_AVTAR;

public class
EditProfileActivity extends ActivityBase implements AttachmentAdapterEditProfile.OnItemClickListener {

    private final int PLACE_SELECTION_REQUEST_CODE = 1;
    private static final int GALLERY_PICKUP_VIDEO_REQUEST_CODE = 300;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400;

    /*@BindView(R.id.toolbar)
    MaterialToolbar toolbar;*/
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_first_name)
    ExtendedEntryText edtFirstName;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_last_name)
    ExtendedEntryText edtLastName;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_suburb)
    ExtendedEntryText txtSuburb;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_tagline)
    ExtendedEntryText edtTagline;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_phone_number)
    EditText edtPhoneNumber;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_about_me)
    ExtendedEntryText edtAboutMe;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_email_address)
    ExtendedEntryText edtEmailAddress;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_birth_date)
    TextView txtBirthDate;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_business_number)
    ExtendedEntryText edtBusinessNumber;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_transportation_back)
    ImageView imgTransportationBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_transportation)
    TextView txtTransportation;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_btn_transportation)
    RelativeLayout rltBtnTransportation;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_languages)
    TextView txtLanguages;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_languages_back)
    ImageView imgLanguagesBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_btn_languages)
    RelativeLayout rltBtnLanguages;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_education)
    TextView txtEducation;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_education_back)
    ImageView imgEducationBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_btn_education)
    RelativeLayout rltBtnEducation;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_experience)
    TextView txtExperience;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_experience_back)
    ImageView imgExperienceBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_btn_experience)
    RelativeLayout rltBtnExperience;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_specialities)
    TextView txtSpecialities;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_specialities_back)
    ImageView imgSpecialitiesBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_btn_specialities)
    RelativeLayout rltBtnSpecialities;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.bottom_sheet)
    FrameLayout bottomSheet;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lytDeletePicture)
    RelativeLayout lytDeletePicture;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_save_profile)
    Button card_save_profile;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivBack)
    ImageView ivBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_user_avatar)
    CircularImageView imgAvatar;

    private ArrayList<AttachmentModel> attachmentArrayList;
    private UserAccountModel userAccountModel;
    private GeocodeObject geoCodeObject;
    private String str_latitude = null;
    private String str_longitude = null;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private int year, month, day;
    private String str_DOB = null;
    private String str_DOB_MODEL = "";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static String imageStoragePath;
    public static final long MAX_VIDEO_DURATION = 30;
    public static final long MAX_VIDEO_SIZE = 20 * 1024 * 1024;
    private boolean isUploadPortfolio = false;
    private boolean isFabHide = false;
    private AttachmentAdapterEditProfile adapter;
    private LinearLayout btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        attachmentArrayList = new ArrayList<>();
        mBehavior = BottomSheetBehavior.from(bottomSheet);
        btnVerify=findViewById(R.id.lyt_btn_close);
        btnVerify.setOnClickListener(view -> verifyPhone());
        ivBack.setOnClickListener(v -> onBackPressed());
        txtBirthDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    mDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        mDateSetListener = (view1, year, month, dayOfMonth) -> {
            month = month + 1;
            str_DOB_MODEL = year + "-" + month + "-" + dayOfMonth;
            str_DOB = Tools.getDayMonthDateTimeFormat2(year + "-" + month + "-" + dayOfMonth);
            txtBirthDate.setText(str_DOB);
        };
        init();
        getAllUserProfileDetails();
        initComponentScroll();
    }

    private void initComponentScroll() {
        NestedScrollView nested_content = findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY < oldScrollY) { // up
                animateFab(false);
            }
            if (scrollY > oldScrollY) { // down
                animateFab(true);
            }
        });
    }

    private void animateFab(final boolean hide) {
        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;
        int moveY = hide ? (2 * card_save_profile.getHeight()) : 0;
        card_save_profile.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtFirstName.getText().trim())) {
            edtFirstName.setError("Enter first name");
            return false;
        } else if (TextUtils.isEmpty(edtLastName.getText().trim())) {
            edtLastName.setError("Enter last name");
            return false;
        } else if (TextUtils.isEmpty(txtSuburb.getText().trim())) {
            txtSuburb.setError("Select your location");
            return false;
        } else if (TextUtils.isEmpty(edtTagline.getText().trim())) {
            edtTagline.setError("Enter your tagline");
            return false;
        } else if (TextUtils.isEmpty(edtAboutMe.getText().trim())) {
            edtAboutMe.setError("Enter your about");
            return false;
        } else if (TextUtils.isEmpty(edtBusinessNumber.getText().trim())) {
            edtBusinessNumber.setError("Enter your Business Number");
            return false;
        } else if (TextUtils.isEmpty(txtBirthDate.getText().toString().trim())) {
            edtBusinessNumber.setError("Enter your Birth Date");

        }
        return true;
    }

    private void submitProfile() {
        showProgressDialog();
        Helper.closeKeyboard(this);
        String str_fname = edtFirstName.getText().trim();
        String str_lname = edtLastName.getText().trim();
        String str_suburb = txtSuburb.getText().trim();
        String str_latitude = sessionManager.getLatitude();
        String str_longitude = sessionManager.getLongitude();
        String str_tag = edtTagline.getText().trim();
        String str_about_me = edtAboutMe.getText().trim();
        String str_business_number = edtBusinessNumber.getText().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_USER_PROFILE_INFO,
                response -> {
                    hideProgressDialog();
                    try {
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            showToast(jsonObject.getString("message"), EditProfileActivity.this);
                            if (onProfileupdatelistener != null) {
                                onProfileupdatelistener.updateProfile();
                            }
                        }

                    } catch (JSONException ignored) {
                    }
                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!

                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            String message = jsonObject_error.getString("message");

                            showToast(message, EditProfileActivity.this);


                            if (jsonObject_error.has("errors")) {

                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", EditProfileActivity.this);
                    }
                    hideProgressDialog();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());

                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("fname", str_fname);
                map1.put("lname", str_lname);
                map1.put("location", str_suburb);
                map1.put("latitude", str_latitude);
                map1.put("longitude", str_longitude);
                map1.put("tagline", str_tag);
                map1.put("business_number", str_business_number);
                map1.put("about", str_about_me);
                if (!str_DOB_MODEL.equals("")) {
                    map1.put("dob", str_DOB_MODEL);
                }
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void init() {
        recyclerView.setLayoutManager(new GridLayoutManager(EditProfileActivity.this, 4));
        recyclerView.addItemDecoration(new SpacingItemDecoration(4, Tools.dpToPx(EditProfileActivity.this, 8), true));
        recyclerView.setHasFixedSize(true);
        //set data and list adapter
        adapter = new AttachmentAdapterEditProfile(EditProfileActivity.this, attachmentArrayList, true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private void getAllUserProfileDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PROFILE + "/" + sessionManager.getUserAccount().getId(),
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {

                            userAccountModel = new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"));
                            /*
                             * Add Button for empty attachment
                             * */
                            setUpAllEditFields(userAccountModel);
                            attachmentArrayList = userAccountModel.getPortfolio();
                            attachmentArrayList.add(new AttachmentModel());
                            Timber.e("%s", attachmentArrayList.size());
                            if (attachmentArrayList.size() != 0) {
                                recyclerView.setVisibility(View.VISIBLE);
                                adapter.addItems(attachmentArrayList);
                            }
                        } else {
                            showToast("Something went wrong", EditProfileActivity.this);
                        }

                    } catch (JSONException e) {
                        showToast("JSONException", EditProfileActivity.this);
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    errorHandle1(error.networkResponse);
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(EditProfileActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }

    private void setUpAllEditFields(UserAccountModel userAccountModel) {
        transportationSetUp(userAccountModel);
        languagesSetUp(userAccountModel);
        educationSetUp(userAccountModel);
        experienceSetUp(userAccountModel);
        specialitiesSetUp(userAccountModel);

        edtFirstName.seteContent(userAccountModel.getFname());
        edtLastName.seteContent(userAccountModel.getLname());
        //edtPaymentId.setText(userAccountModel.get());
        if (userAccountModel.getLocation() != null) {
            txtSuburb.seteContent(userAccountModel.getLocation());
            str_latitude = String.valueOf(userAccountModel.getLatitude());
            str_longitude = String.valueOf(userAccountModel.getLongitude());
        }
        edtTagline.seteContent(userAccountModel.getTagline());
        edtAboutMe.seteContent(userAccountModel.getAbout());
        edtBusinessNumber.seteContent(userAccountModel.getBusiness_number());
        edtEmailAddress.seteContent(userAccountModel.getEmail());
        txtBirthDate.setText(userAccountModel.getDob());
        if (userAccountModel.getAvatar() != null) {
            ImageUtil.displayImage(imgAvatar, userAccountModel.getAvatar().getThumbUrl(), null);
            lytDeletePicture.setVisibility(View.VISIBLE);
        } else {
            lytDeletePicture.setVisibility(View.GONE);
        }

    }

    @SuppressLint("SetTextI18n")
    private void specialitiesSetUp(UserAccountModel userAccountModel) {
        if (userAccountModel.getSkills().getSpecialities() != null && userAccountModel.getSkills().getSpecialities().size() != 0) {
            String str_tag = convertArrayToString(userAccountModel.getSkills().getSpecialities());
            txtSpecialities.setText("" + userAccountModel.getSkills().getSpecialities().size());
            txtSpecialities.setVisibility(View.VISIBLE);
        } else {
            txtSpecialities.setVisibility(View.VISIBLE);
            txtSpecialities.setText("0");
        }
    }

    @SuppressLint("SetTextI18n")
    private void experienceSetUp(UserAccountModel userAccountModel) {
        if (userAccountModel.getSkills().getExperience() != null && userAccountModel.getSkills().getExperience().size() != 0) {
            String str_tag = convertArrayToString(userAccountModel.getSkills().getExperience());
            txtExperience.setText("" + userAccountModel.getSkills().getExperience().size());
            txtExperience.setVisibility(View.VISIBLE);
        } else {
            txtExperience.setVisibility(View.VISIBLE);
            txtExperience.setText("0");
        }
    }

    @SuppressLint("SetTextI18n")
    private void educationSetUp(UserAccountModel userAccountModel) {
        if (userAccountModel.getSkills().getEducation() != null && userAccountModel.getSkills().getEducation().size() != 0) {
            String str_tag = convertArrayToString(userAccountModel.getSkills().getEducation());
            txtEducation.setText("" + userAccountModel.getSkills().getEducation().size());
            txtEducation.setVisibility(View.VISIBLE);
        } else {
            txtEducation.setVisibility(View.VISIBLE);
            txtEducation.setText("0");
        }
    }

    @SuppressLint("SetTextI18n")
    private void languagesSetUp(UserAccountModel userAccountModel) {
        if (userAccountModel.getSkills().getLanguage() != null && userAccountModel.getSkills().getLanguage().size() != 0) {
            String str_tag = convertArrayToString(userAccountModel.getSkills().getLanguage());
            txtLanguages.setText("" + userAccountModel.getSkills().getLanguage().size());
            txtLanguages.setVisibility(View.VISIBLE);
        } else {
            txtLanguages.setVisibility(View.VISIBLE);
            txtLanguages.setText("0");
        }
    }

    @SuppressLint("SetTextI18n")
    private void transportationSetUp(UserAccountModel userAccountModel) {
        if (userAccountModel.getSkills().getTransportation() != null && userAccountModel.getSkills().getTransportation().size() != 0) {
            String str_tag = convertArrayToString(userAccountModel.getSkills().getTransportation());
            txtTransportation.setText("" + userAccountModel.getSkills().getTransportation().size());
            txtTransportation.setVisibility(View.VISIBLE);

        } else {
            txtTransportation.setVisibility(View.generateViewId());
            txtTransportation.setText("0");
        }

    }

    private String convertArrayToString(ArrayList<String> tag) {
        String str_tag = "";
        for (String s : tag) {
            if (str_tag.equals("")) {
                str_tag = s;
            } else {
                str_tag = String.format("%s, %s", str_tag, s);
            }
        }
        return str_tag;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemClick(View view, AttachmentModel obj, int position, String action) {
        if (action.equalsIgnoreCase("add")) {


            showBottomSheetDialog(true);

        } else if (action.equalsIgnoreCase("delete")) {
            deleteMediaInAttachment(position);
        }
    }

    private void deleteMediaInAttachment(int position) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_PROFILE + "/portfolio/" + attachmentArrayList.get(position).getId(),
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());


                        /*attachmentArrayList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeRemoved(position, attachmentArrayList.size());*/

                        attachmentArrayList.remove(position);
                        adapter.DeleteItem(position);

                        showToast("Portfolio Deleted", EditProfileActivity.this);


                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser();
                            hideProgressDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                Toast.makeText(EditProfileActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", EditProfileActivity.this);
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(EditProfileActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.txt_suburb, R.id.txt_birth_date, R.id.rlt_btn_transportation, R.id.rlt_btn_languages,
            R.id.rlt_btn_education, R.id.rlt_btn_experience, R.id.rlt_btn_specialities, R.id.img_user_avatar, R.id.lytDeletePicture,
            R.id.card_save_profile, R.id.lyt_btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.card_save_profile:

                new MaterialAlertDialogBuilder(EditProfileActivity.this)
                        .setTitle("Update Profile")
                        .setMessage("Are you sure you want to update your Profile?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (validation())
                                submitProfile();
                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
            case R.id.txt_suburb:
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken())
                        .placeOptions(PlaceOptions.builder()
                                // .backgroundColor(Helper.getAttrColor(taskCreateActivity, R.attr.colorBackground))
                                .backgroundColor(getResources().getColor(R.color.background))
                                .limit(10)
                                .country("AU")

                                /*.addInjectedFeature(home)
                                .addInjectedFeature(work)*/
                                .build(PlaceOptions.MODE_FULLSCREEN))
                        .build(EditProfileActivity.this);
                startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);

                break;
            case R.id.txt_birth_date:
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        EditProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;
            case R.id.rlt_btn_transportation:
                intent = new Intent(EditProfileActivity.this, SkillsTagActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel.getSkills().getTransportation());
                bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.TRANSPORTATION);
                bundle.putString(ConstantKey.TITLE, "Add your transportation");
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                break;
            case R.id.rlt_btn_languages:
                intent = new Intent(EditProfileActivity.this, SkillsTagActivity.class);
                bundle = new Bundle();
                bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel.getSkills().getLanguage());
                bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.LANGUAGE);
                bundle.putString(ConstantKey.TITLE, "Add your language");
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
                break;
            case R.id.rlt_btn_education:
                intent = new Intent(EditProfileActivity.this, SkillsTagActivity.class);
                bundle = new Bundle();
                bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel.getSkills().getEducation());
                bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.EDUCATION);
                bundle.putString(ConstantKey.TITLE, "Add your education");
                intent.putExtras(bundle);
                startActivityForResult(intent, 3);
                break;
            case R.id.rlt_btn_experience:
                intent = new Intent(EditProfileActivity.this, SkillsTagActivity.class);
                bundle = new Bundle();
                bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel.getSkills().getExperience());
                bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.EXPERIENCE);
                bundle.putString(ConstantKey.TITLE, "Add your occupation");
                intent.putExtras(bundle);
                startActivityForResult(intent, 4);
                break;
            case R.id.rlt_btn_specialities:
                intent = new Intent(EditProfileActivity.this, SkillsTagActivity.class);
                bundle = new Bundle();
                bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel.getSkills().getSpecialities());
                bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.SPECIALITIES);
                bundle.putString(ConstantKey.TITLE, "Add your certificate");
                intent.putExtras(bundle);
                startActivityForResult(intent, 5);
                break;
            case R.id.img_user_avatar:
                showBottomSheetDialog(false);

                break;
            case R.id.lytDeletePicture:

                new MaterialAlertDialogBuilder(EditProfileActivity.this)
                        .setTitle("Alert!")
                        .setMessage("Remove profile photo?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog1, id) -> {
                            dialog1.dismiss();
                            removeProfilePicture();
                        })
                        .setNegativeButton("No", (dialog12, id) -> {
                            //  Action for 'NO' Button
                            dialog12.cancel();

                        }).show();

                break;

        }
    }

    private void verifyPhone() {
        if(edtPhoneNumber.toString().equals("")){
            showToast("write number", EditProfileActivity.this);
            btnVerify.setClickable(false);
            btnVerify.setEnabled(false);
        }else {
            btnVerify.setClickable(true);
            btnVerify.setEnabled(true);
            Intent intent = new Intent(this, MobileVerificationActivity.class);
            intent.putExtra("phone_number", edtPhoneNumber.getText().toString());
            startActivity(intent);
        }


    }

    private void uploadDataInPortfolioMediaApi(File pictureFile) {
        showProgressDialog();
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getPortfolioMediaUpload("XMLHttpRequest",
                sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, retrofit2.@NotNull Response<String> response) {
                hideProgressDialog();
                Timber.e(response.toString());
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), EditProfileActivity.this);
                    return;
                }
                try {
                    String strResponse = response.body();
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        Toast.makeText(EditProfileActivity.this, "not found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser();
                        return;
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Timber.e(strResponse);
                        JSONObject jsonObject = new JSONObject(strResponse);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data")) {
                            AttachmentModel attachment = new AttachmentModel();
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            if (jsonObject_data.has("id") && !jsonObject_data.isNull("id"))
                                attachment.setId(jsonObject_data.getInt("id"));
                            if (jsonObject_data.has("name") && !jsonObject_data.isNull("name"))
                                attachment.setName(jsonObject_data.getString("name"));
                            if (jsonObject_data.has("file_name") && !jsonObject_data.isNull("file_name"))
                                attachment.setFileName(jsonObject_data.getString("file_name"));
                            if (jsonObject_data.has("mime") && !jsonObject_data.isNull("mime"))
                                attachment.setMime(jsonObject_data.getString("mime"));
                            if (jsonObject_data.has("url") && !jsonObject_data.isNull("url"))
                                attachment.setUrl(jsonObject_data.getString("url"));
                            if (jsonObject_data.has("thumb_url") && !jsonObject_data.isNull("thumb_url"))
                                attachment.setThumbUrl(jsonObject_data.getString("thumb_url"));
                            if (jsonObject_data.has("modal_url") && !jsonObject_data.isNull("modal_url"))
                                attachment.setModalUrl(jsonObject_data.getString("modal_url"));
                            if (jsonObject_data.has("created_at") && !jsonObject_data.isNull("created_at"))
                                attachment.setCreatedAt(jsonObject_data.getString("created_at"));
                            attachment.setType(AttachmentAdapter.VIEW_TYPE_IMAGE);

                            if (attachmentArrayList.size() != 0) {

                                attachmentArrayList.add(attachmentArrayList.size() - 1, attachment);

                            }
                        }


                        ArrayList<AttachmentModel> updateAttachment = new ArrayList<>(attachmentArrayList);

                        attachmentArrayList.clear();
                        attachmentArrayList.addAll(updateAttachment);
                        adapter.clearAll();

                        adapter.addItems(attachmentArrayList);

                        updateAttachment.clear();
                        //adapter.notifyDataSetChanged();


                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // showToast("attachment added", AttachmentActivity.this);
                    } else {
                        showToast("Something went wrong", EditProfileActivity.this);
                    }
                } catch (JSONException e) {
                    showToast("Something went wrong", EditProfileActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                hideProgressDialog();
                Timber.e(call.toString());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {

                // Retrieve the information from the selected location's CarmenFeature

                CarmenFeature carmenFeature = PlacePicker.getPlace(data);
                Helper.Logger("THIS", "CarmenFeature = " + carmenFeature.toJson());
                txtSuburb.seteContent(carmenFeature.placeName());
                str_latitude = String.valueOf(carmenFeature.center().latitude());
                str_longitude = String.valueOf(carmenFeature.center().longitude());
                LatLng locationObject = new LatLng(carmenFeature.center().latitude(), carmenFeature.center().longitude());
            }
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Uri uri = Uri.parse("file://" + imageStoragePath);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imgAvatar.setImageBitmap(bitmap);

                    if (isUploadPortfolio) {
                        uploadDataInPortfolioMediaApi(new File(uri.getPath()));
                    } else {
                        uploadProfileAvtar(new File(uri.getPath()));
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }
            } else if (requestCode == GALLERY_PICKUP_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    if (data.getData() != null) {
                        imageStoragePath = CameraUtils.getPath(EditProfileActivity.this, data.getData());
                        File file = new File(imageStoragePath);
                        Uri uri = Uri.parse("file://" + imageStoragePath);
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imgAvatar.setImageBitmap(bitmap);

                        if (isUploadPortfolio) {
                            uploadDataInPortfolioMediaApi(new File(uri.getPath()));

                        } else {
                            uploadProfileAvtar(new File(uri.getPath()));
                        }


                        //// uploadDataInPortfolioMediaApi(file);
                    }
                } else {
                    // failed to record video
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to Pickup Image", Toast.LENGTH_SHORT)
                            .show();
                }
            }


            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (requestCode == 1) {
                    userAccountModel.getSkills().setTransportation(bundle.getStringArrayList(ConstantKey.SKILLS));
                    transportationSetUp(userAccountModel);
                } else if (requestCode == 2) {
                    userAccountModel.getSkills().setLanguage(bundle.getStringArrayList(ConstantKey.SKILLS));
                    languagesSetUp(userAccountModel);
                } else if (requestCode == 3) {
                    userAccountModel.getSkills().setEducation(bundle.getStringArrayList(ConstantKey.SKILLS));
                    educationSetUp(userAccountModel);
                } else if (requestCode == 4) {
                    userAccountModel.getSkills().setExperience(bundle.getStringArrayList(ConstantKey.SKILLS));
                    experienceSetUp(userAccountModel);
                } else if (requestCode == 5) {
                    userAccountModel.getSkills().setSpecialities(bundle.getStringArrayList(ConstantKey.SKILLS));
                    specialitiesSetUp(userAccountModel);
                } else if (requestCode == 234) {
                    userAccountModel.setPortfolio(bundle.getParcelableArrayList(ConstantKey.ATTACHMENT));
                    adapter.clearAll();
                    attachmentArrayList.clear();
                    attachmentArrayList = userAccountModel.getPortfolio();
                    /*
                     * include add button */
                    adapter.addItems(attachmentArrayList);
                }
            }
        }
    }

    private void showBottomSheetDialog(boolean isUploadPortfolioOrPrfile) {
        isUploadPortfolio = isUploadPortfolioOrPrfile;

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.sheet_attachment, null);
        LinearLayout lytBtnCamera = view.findViewById(R.id.lyt_btn_camera);
        LinearLayout lytBtnImage = view.findViewById(R.id.lyt_btn_image);
        LinearLayout lytBtnVideo = view.findViewById(R.id.lyt_btn_video);
        LinearLayout lytBtnDoc = view.findViewById(R.id.lyt_btn_doc);
        LinearLayout lyrBtnVideoCamera = view.findViewById(R.id.lyt_btn_video_camera);

        if (isUploadPortfolioOrPrfile) {
            lytBtnVideo.setVisibility(View.VISIBLE);
            lytBtnDoc.setVisibility(View.VISIBLE);
            lyrBtnVideoCamera.setVisibility(View.VISIBLE);
        } else {
            lytBtnVideo.setVisibility(View.GONE);
            lytBtnDoc.setVisibility(View.INVISIBLE);
            lyrBtnVideoCamera.setVisibility(View.INVISIBLE);
        }


        lytBtnCamera.setOnClickListener(view1 -> {
            // Checking availability of the camera
            if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Your device doesn't support camera",
                        Toast.LENGTH_LONG).show();
                // will close the app if the device doesn't have camera
                return;
            }
            if (CameraUtils.checkPermissions(getApplicationContext())) {
                captureImage();
            } else {
                requestCameraPermission();
            }
            mBottomSheetDialog.hide();
        });


        lytBtnImage.setOnClickListener(v -> {
            Intent opengallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(opengallary, "Open Gallary"), GALLERY_PICKUP_IMAGE_REQUEST_CODE);
            mBottomSheetDialog.hide();
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);

    }

    /**
     * Requesting permissions using Dexter library
     */
    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            // capture picture
                            captureImage();

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(ConstantKey.MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void uploadProfileAvtar(File pictureFile) {
        showProgressDialog();
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().uploadProfilePicture("XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NotNull Call<String> call, retrofit2.@NotNull Response<String> response) {
                hideProgressDialog();
                Timber.e(response.toString());
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), EditProfileActivity.this);
                    return;
                }
                try {
                    String strResponse = response.body();
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        Toast.makeText(EditProfileActivity.this, "not found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser();
                        return;
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Timber.e(strResponse);
                        JSONObject jsonObject = new JSONObject(strResponse);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data")) {
                            AttachmentModel attachment = new AttachmentModel();
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            if (jsonObject_data.has("id") && !jsonObject_data.isNull("id"))
                                attachment.setId(jsonObject_data.getInt("id"));
                            if (jsonObject_data.has("name") && !jsonObject_data.isNull("name"))
                                attachment.setName(jsonObject_data.getString("name"));
                            if (jsonObject_data.has("file_name") && !jsonObject_data.isNull("file_name"))
                                attachment.setFileName(jsonObject_data.getString("file_name"));
                            if (jsonObject_data.has("mime") && !jsonObject_data.isNull("mime"))
                                attachment.setMime(jsonObject_data.getString("mime"));
                            if (jsonObject_data.has("url") && !jsonObject_data.isNull("url"))
                                attachment.setUrl(jsonObject_data.getString("url"));
                            if (jsonObject_data.has("thumb_url") && !jsonObject_data.isNull("thumb_url"))
                                attachment.setThumbUrl(jsonObject_data.getString("thumb_url"));
                            if (jsonObject_data.has("modal_url") && !jsonObject_data.isNull("modal_url"))
                                attachment.setModalUrl(jsonObject_data.getString("modal_url"));
                            if (jsonObject_data.has("created_at") && !jsonObject_data.isNull("created_at"))
                                attachment.setCreatedAt(jsonObject_data.getString("created_at"));
                            attachment.setType(AttachmentAdapter.VIEW_TYPE_IMAGE);

                            sessionManager.getUserAccount().setAvatar(attachment);
                            lytDeletePicture.setVisibility(View.VISIBLE);
                            if (onProfileupdatelistener != null) {
                                onProfileupdatelistener.updatedSuccesfully(attachment.getThumbUrl());
                            }
                            if (onProfileupdatelistenerSideMenu != null) {
                                onProfileupdatelistenerSideMenu.updatedSuccesfully(attachment.getThumbUrl());
                            }
                        }
                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // showToast("attachment added", AttachmentActivity.this);
                    } else {
                        showToast("Something went wrong", EditProfileActivity.this);
                    }
                } catch (JSONException e) {
                    showToast("Something went wrong", EditProfileActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                hideProgressDialog();
                Timber.e(call.toString());
            }
        });

    }

    private void removeProfilePicture() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_PROFILE + URL_REMOVE_AVTAR,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                                showToast("Profile Picture has been  Deleted", EditProfileActivity.this);
                                imgAvatar.setImageResource(R.drawable.ic_profile_image);
                                lytDeletePicture.setVisibility(View.GONE);
                                if (onProfileupdatelistener != null) {
                                    onProfileupdatelistener.updatedSuccesfully("");
                                }
                                if (onProfileupdatelistenerSideMenu != null) {
                                    onProfileupdatelistenerSideMenu.updatedSuccesfully("");
                                }
                            } else {
                                showToast("Something went Wrong", EditProfileActivity.this);
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser();
                            hideProgressDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                Toast.makeText(EditProfileActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", EditProfileActivity.this);
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(EditProfileActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }
}
