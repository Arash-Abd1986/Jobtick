package com.jobtick.android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;
import com.jobtick.android.adapers.AttachmentAdapter;
import com.jobtick.android.adapers.AttachmentAdapterEditProfile;
import com.jobtick.android.adapers.SuburbSearchAdapter;
import com.jobtick.android.fragments.DatePickerBottomSheet;
import com.jobtick.android.fragments.SearchSuburbBottomSheet;
import com.jobtick.android.models.AttachmentModel;
import com.jobtick.android.models.UserAccountModel;
import com.jobtick.android.models.response.searchsuburb.Feature;
import com.jobtick.android.retrofit.ApiClient;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.Helper;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.utils.SuburbAutoComplete;
import com.jobtick.android.utils.TimeHelper;
import com.jobtick.android.utils.Tools;
import com.jobtick.android.widget.ExtendedCommentText;
import com.jobtick.android.widget.ExtendedEntryText;
import com.jobtick.android.widget.SpacingItemDecoration;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

import static com.jobtick.android.activities.DashboardActivity.onProfileupdatelistenerSideMenu;
import static com.jobtick.android.fragments.ProfileFragment.onProfileupdatelistener;
import static com.jobtick.android.utils.Constant.MIN_AGE_FOR_USE_APP;
import static com.jobtick.android.utils.Constant.URL_REMOVE_AVTAR;

public class
EditProfileActivity extends ActivityBase implements AttachmentAdapterEditProfile.OnItemClickListener,
        SuburbSearchAdapter.SubClickListener, DatePickerBottomSheet.DateChange {

    private final int PLACE_SELECTION_REQUEST_CODE = 1;
    public static final int PHONE_VERIFICATION_REQUEST_CODE = 500;

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
    ExtendedEntryText edtPhoneNumber;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_about_me)
    ExtendedCommentText edtAboutMe;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_email_address)
    ExtendedEntryText edtEmailAddress;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_birth_date)
    ExtendedEntryText txtBirthDate;

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
    @BindView(R.id.txt_general_info)
    TextView txtGeneralInfo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_private_info)
    TextView txtPrivateInfo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_portfolio_skills)
    TextView txtPortfolioSkills;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llGeneral)
    LinearLayout llGeneral;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llPinfo)
    LinearLayout llPinfo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llPS)
    LinearLayout llPS;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.under_tab3)
    View underTab3;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.under_tab2)
    View underTab2;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.under_tab1)
    View underTab1;

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
    @BindView(R.id.small_plus)
    FrameLayout smallPlus;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_user_avatar)
    CircularImageView imgAvatar;

    private ArrayList<AttachmentModel> attachmentArrayList;
    private UserAccountModel userAccountModel;
    private String str_latitude = null;
    private String str_longitude = null;
    private BottomSheetBehavior mBehavior;
    private String str_DOB_MODEL = "";
    private AttachmentAdapterEditProfile adapter;
    private UploadableImage uploadableImage;
    private boolean isImageProfile;
    private int year, month, day;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

        attachmentArrayList = new ArrayList<>();
        mBehavior = BottomSheetBehavior.from(bottomSheet);
        edtPhoneNumber.setExtendedViewOnClickListener(this::verifyPhone);
        ivBack.setOnClickListener(v -> onBackPressed());

        init();
        getAllUserProfileDetails();

        uploadableImage = new AbstractUploadableImageImpl(this) {
            @Override
            public void onImageReady(File imageFile) {
                uploadMedia(imageFile);
            }
        };
    }


    private boolean validation() {
        if (TextUtils.isEmpty(edtFirstName.getText().trim())) {
            edtFirstName.setError("Enter first name");
            showToast("Please fill inputs", this);
            return false;
        } else if (TextUtils.isEmpty(edtLastName.getText().trim())) {
            edtLastName.setError("Enter last name");
            showToast("Please fill inputs", this);
            return false;
        } else if (TextUtils.isEmpty(txtSuburb.getText().trim())) {
            showToast("Please fill inputs", this);
            txtSuburb.setError("Select your location");
            return false;
        }
        return true;
    }

    private void submitProfile() {
        showProgressDialog();
        Helper.closeKeyboard(this);
        String str_fname = edtFirstName.getText().trim();
        String str_lname = edtLastName.getText().trim();
        String str_suburb = txtSuburb.getText().trim();
        String str_tag = edtTagline.getText().trim();
        String str_about_me = edtAboutMe.getText().trim();
        String str_business_number = edtBusinessNumber.getText().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_USER_PROFILE_INFO,
                response -> {
                    hideProgressDialog();
                    try {
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject_user = jsonObject.getJSONObject("data");

                            UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                            sessionManager.setUserAccount(userAccountModel);
                            sessionManager.setLatitude(str_latitude);
                            sessionManager.setLongitude(str_longitude);
                            initDatePicker();
                            showSuccessToast(jsonObject.getString("message"), EditProfileActivity.this);
                            if (onProfileupdatelistener != null) {
                                onProfileupdatelistener.updateProfile();
                            }
                            onBackPressed();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Something Went Wrong", EditProfileActivity.this);
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));

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
        initTabs();
        recyclerView.setLayoutManager(new GridLayoutManager(EditProfileActivity.this, 4));
        recyclerView.addItemDecoration(new SpacingItemDecoration(4, Tools.dpToPx(EditProfileActivity.this, 8), true));
        recyclerView.setHasFixedSize(true);
        //set data and list adapter
        adapter = new AttachmentAdapterEditProfile(EditProfileActivity.this, attachmentArrayList, true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        txtBirthDate.setExtendedViewOnClickListener(() -> {

            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date old_date = new Date();
            try {
                old_date = utcFormat.parse(userAccountModel.getDob());

            } catch (Exception e) {
                e.printStackTrace();
            }
            DatePickerBottomSheet datePickerBottomSheet = new DatePickerBottomSheet(new Date(String.valueOf(old_date)).getTime());
            datePickerBottomSheet.setDchange(this);
            datePickerBottomSheet.show(this.getSupportFragmentManager(),"");
        });
        initDatePicker();

        txtSuburb.setExtendedViewOnClickListener(() -> {
            SearchSuburbBottomSheet infoBottomSheet = new SearchSuburbBottomSheet(this);
            infoBottomSheet.show(getSupportFragmentManager(), null);
        });
        smallPlus.setOnClickListener(v->{
            isImageProfile = false;
            uploadableImage.showAttachmentImageBottomSheet(false);
        });
    }

    private void initTabs() {
        llGeneral.setVisibility(View.VISIBLE);
        llPinfo.setVisibility(View.GONE);
        llPS.setVisibility(View.GONE);
        txtGeneralInfo.setOnClickListener(it -> {
            llGeneral.setVisibility(View.VISIBLE);
            llPinfo.setVisibility(View.GONE);
            llPS.setVisibility(View.GONE);
            txtGeneralInfo.setTextColor(getColor(R.color.colorAccent));
            txtPrivateInfo.setTextColor(getColor(R.color.N100));
            txtPortfolioSkills.setTextColor(getColor(R.color.N100));
            underTab1.setBackground(ContextCompat.getDrawable(this,R.drawable.tab_line_selected));
            underTab2.setBackground(ContextCompat.getDrawable(this,R.drawable.tab_line));
            underTab3.setBackground(ContextCompat.getDrawable(this,R.drawable.tab_line));
        });
        txtPrivateInfo.setOnClickListener(it -> {
            llGeneral.setVisibility(View.GONE);
            llPinfo.setVisibility(View.VISIBLE);
            llPS.setVisibility(View.GONE);
            txtGeneralInfo.setTextColor(getColor(R.color.N100));
            txtPrivateInfo.setTextColor(getColor(R.color.colorAccent));
            txtPortfolioSkills.setTextColor(getColor(R.color.N100));
            underTab1.setBackground(ContextCompat.getDrawable(this,R.drawable.tab_line));
            underTab2.setBackground(ContextCompat.getDrawable(this,R.drawable.tab_line_selected));
            underTab3.setBackground(ContextCompat.getDrawable(this,R.drawable.tab_line));
        });
        txtPortfolioSkills.setOnClickListener(it -> {
            llGeneral.setVisibility(View.GONE);
            llPinfo.setVisibility(View.GONE);
            llPS.setVisibility(View.VISIBLE);
            txtGeneralInfo.setTextColor(getColor(R.color.N100));
            txtPrivateInfo.setTextColor(getColor(R.color.N100));
            txtPortfolioSkills.setTextColor(getColor(R.color.colorAccent));
            underTab1.setBackground(ContextCompat.getDrawable(this,R.drawable.tab_line));
            underTab2.setBackground(ContextCompat.getDrawable(this,R.drawable.tab_line));
            underTab3.setBackground(ContextCompat.getDrawable(this,R.drawable.tab_line_selected));
        });
    }

    private void initDatePicker() {
        userAccountModel = sessionManager.getUserAccount();
        Calendar calendar = Calendar.getInstance();
        if (userAccountModel.getDob() != null) {
            calendar.set(TimeHelper.getYear(userAccountModel.getDob()),
                    TimeHelper.getMonth(userAccountModel.getDob()),
                    TimeHelper.getDay(userAccountModel.getDob()));

            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

        } else {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -(MIN_AGE_FOR_USE_APP));
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        //TODO
//        datePickerDialog = new DatePickerDialog(this,
//                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
//                mDateSetListener,
//                year, month, day);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -(MIN_AGE_FOR_USE_APP));
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //TODO
//        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
//        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                           // attachmentArrayList.add(new AttachmentModel());
                            Timber.e("%s", attachmentArrayList.size());
                            if (attachmentArrayList.size() >= 0) {
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
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
        edtLastName.seteContent(userAccountModel.getLname());
        if (userAccountModel.getMobile() != null) {
            if (userAccountModel.getMobile().length() > 3)
                edtPhoneNumber.setText("0" + userAccountModel.getMobile().substring(3));
        } else {
            edtPhoneNumber.seteVerifyVisible(true);
        }

        if (userAccountModel.getMobileVerifiedAt() == null) {
            edtPhoneNumber.seteVerifyVisible(true);
        }
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edtPhoneNumber.seteVerifyVisible(true);
            }
        });
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
        txtBirthDate.setText(Tools.getDayMonthDateTimeFormat(userAccountModel.getDob()));

        if (userAccountModel.getAvatar() != null) {
            Glide.with(imgAvatar).load(userAccountModel.getAvatar().getThumbUrl()).into(imgAvatar);
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

            isImageProfile = false;
            uploadableImage.showAttachmentImageBottomSheet(false);

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
                                showToast(jsonObject_error.getString("message"), this);
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
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
    @OnClick({R.id.rlt_btn_transportation, R.id.rlt_btn_languages,
            R.id.rlt_btn_education, R.id.rlt_btn_experience, R.id.rlt_btn_specialities, R.id.img_user_avatar, R.id.lytDeletePicture,
            R.id.card_save_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.card_save_profile:
                if (validation())
                    submitProfile();

                break;
            case R.id.rlt_btn_transportation:
                Intent intent = new Intent(EditProfileActivity.this, SkillsTagActivity.class);
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
                isImageProfile = true;
                uploadableImage.showAttachmentImageBottomSheet(true);
                break;
            case R.id.lytDeletePicture:

                new MaterialAlertDialogBuilder(EditProfileActivity.this)
                        .setTitle("")
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
        //it should work with Australian Numbers, format: +0* **** ****
        if (edtPhoneNumber.getText().length() != 10) {
            showToast("Please enter correct mobile number", EditProfileActivity.this);
            return;
        }
        if (edtPhoneNumber.getText().toString().equals(userAccountModel.getMobile())) {
            showToast("This mobile number is already registered.", EditProfileActivity.this);
            return;
        }

        Intent intent = new Intent(this, MobileVerificationActivity.class);
        intent.putExtra("phone_number", edtPhoneNumber.getText().toString());
        startActivityForResult(intent, PHONE_VERIFICATION_REQUEST_CODE);
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
                        showToast("not found", EditProfileActivity.this);
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
        uploadableImage.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {

                txtSuburb.setText(SuburbAutoComplete.getSuburbName(data));
                str_latitude = SuburbAutoComplete.getLatitude(data);
                str_longitude = SuburbAutoComplete.getLongitude(data);
            }

            if (requestCode == PHONE_VERIFICATION_REQUEST_CODE && resultCode == RESULT_OK) {
                getAllUserProfileDetails();
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

    private void uploadMedia(File imageFile) {
        Uri uri = Uri.fromFile(imageFile);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imgAvatar.setImageBitmap(bitmap);

        if (!isImageProfile) {
            uploadDataInPortfolioMediaApi(imageFile);

        } else {
            uploadProfileAvtar(imageFile);
        }
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
                        showToast("not found", EditProfileActivity.this);
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
                                showSuccessToast("Profile Picture has been  Deleted", EditProfileActivity.this);
                                imgAvatar.setImageResource(R.drawable.ic_circle_logo);
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
                                showToast(jsonObject_error.getString("message"), this);
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
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


    @Override
    public void clickOnSearchedLoc(@NotNull Feature location) {
        txtSuburb.setText(location.getPlace_name_en());
        str_latitude = String.valueOf(location.getGeometry().getCoordinates().get(1));
        str_longitude = String.valueOf(location.getGeometry().getCoordinates().get(0));
    }

    @Override
    public void onDateChange(int year, int monthOfYear, int dayOfMonth) {
        Date temp = new Date(System.currentTimeMillis());
        Calendar today = Calendar.getInstance();
        today.setTime(temp);
        int currentYear = today.get(Calendar.YEAR);
        if (currentYear - 13 < year) {
            showToast("Your age must be over 13", this);
            return;
        }
        Calendar dob = Calendar.getInstance();

        dob.set(year, monthOfYear, dayOfMonth);
        str_DOB_MODEL = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
        txtBirthDate.setText(dob.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + dayOfMonth + ", " + year);
    }
}
