package com.jobtick.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hootsuite.nachos.NachoTextView;
import com.jobtick.activities.UserProfileActivity;
import com.jobtick.activities.ZoomImageActivity;
import com.jobtick.adapers.BadgesAdapter;
import com.jobtick.interfaces.onProfileUpdateListener;
import com.jobtick.models.BadgesModel;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.activities.DashboardActivity;
import com.jobtick.activities.EditProfileActivity;
import com.jobtick.activities.ReviewsActivity;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;
import com.jobtick.widget.SpacingItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements onProfileUpdateListener, AttachmentAdapter.OnItemClickListener {
    private static final String TAG = ProfileFragment.class.getName();
    @BindView(R.id.txt_account_level)
    TextViewRegular txtAccountLevel;
    @BindView(R.id.lyt_btn_get_a_quote)
    LinearLayout lytBtnGetAQuote;
    @BindView(R.id.rb_as_a_ticker)
    RadioButton rbAsATicker;
    @BindView(R.id.rb_as_a_poster)
    RadioButton rbAsAPoster;
    @BindView(R.id.rg_ticker_poster)
    RadioGroup rgTickerPoster;
    @BindView(R.id.rb_about)
    RadioButton rbAbout;
    @BindView(R.id.rb_skills)
    RadioButton rbSkills;
    @BindView(R.id.rb_badges)
    RadioButton rbBadges;
    @BindView(R.id.rb_portfolio)
    RadioButton rbPortfolio;
    @BindView(R.id.rg_about_skills_badges_portfolio)
    RadioGroup rgAboutSkillsBadgesPortfolio;
    @BindView(R.id.lyt_about)
    LinearLayout lytAbout;
    @BindView(R.id.tag_education)
    NachoTextView tagEducation;
    @BindView(R.id.lyt_education)
    LinearLayout lytEducation;
    @BindView(R.id.tag_specialities)
    NachoTextView tagSpecialities;
    @BindView(R.id.lyt_specialities)
    LinearLayout lytSpecialities;
    @BindView(R.id.tag_language)
    NachoTextView tagLanguage;
    @BindView(R.id.lyt_language)
    LinearLayout lytLanguage;
    @BindView(R.id.tag_experience)
    NachoTextView tagExperience;
    @BindView(R.id.lyt_experience)
    LinearLayout lytExperience;
    @BindView(R.id.tag_transportation)
    NachoTextView tagTransportation;
    @BindView(R.id.lyt_transportation)
    LinearLayout lytTransportation;
    @BindView(R.id.lyt_skills)
    LinearLayout lytSkills;
    @BindView(R.id.recycler_view_portfolio)
    RecyclerView recyclerViewPortfolio;
    @BindView(R.id.recycler_view_badges)
    RecyclerView recyclerViewBadges;
    @BindView(R.id.relativeLayout)
    NestedScrollView relativeLayout;
    @BindView(R.id.txt_about)
    TextViewRegular txtAbout;
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @BindView(R.id.img_verified)
    ImageView imgVerified;
    @BindView(R.id.img_level)
    ImageView imgLevel;
    @BindView(R.id.txt_full_name)
    TextViewSemiBold txtFullName;
    @BindView(R.id.txt_suburb)
    TextViewRegular txtSuburb;
    @BindView(R.id.txt_last_seen)
    TextViewSemiBold txtLastSeen;
    @BindView(R.id.ratingbar)
    AppCompatRatingBar ratingbar;
    @BindView(R.id.txt_completion_rate)
    TextViewRegular txtCompletionRate;
    @BindView(R.id.txt_rating_value)
    TextViewRegular txtRatingValue;
    @BindView(R.id.txt_reviews_count)
    TextViewBold txtReviewsCount;
    @BindView(R.id.txt_btn_see_reviews)
    TextViewBold txtBtnSeeReviews;
    private DashboardActivity dashboardActivity;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private UserAccountModel userAccountModel;
    private ArrayList<AttachmentModel> attachmentArrayList;
    private ArrayList<BadgesModel> badgesModelArrayList;

    private AttachmentAdapter adapter;
    private BadgesAdapter badgesAdapter;
    public static onProfileUpdateListener onProfileupdatelistener;
    ImageView ivNotification;
    TextViewBold toolbar_title;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        dashboardActivity = (DashboardActivity) getActivity();
        onProfileupdatelistener = this;
        if (dashboardActivity != null) {
            toolbar = dashboardActivity.findViewById(R.id.toolbar);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_profile);
            ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
            ivNotification.setVisibility(View.GONE);
            toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
            toolbar_title.setVisibility(View.GONE);

        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(dashboardActivity);
        userAccountModel = new UserAccountModel();
        attachmentArrayList = new ArrayList<>();
        badgesModelArrayList = new ArrayList<>();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_flag:

                        break;
                    case R.id.action_edit:
                        startActivity(new Intent(dashboardActivity, EditProfileActivity.class));

                        break;
                }
                return false;
            }
        });

        init();
        getAllProfileData();

        initComponent();
    }

    private void initComponent() {
        rgTickerPoster.setOnCheckedChangeListener((group, checkedId) -> {

            onChangeTabUser();


        });


        rgAboutSkillsBadgesPortfolio.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_about:
                    onChangeTabBiography();
                    break;
                case R.id.rb_skills:
                    onChangeTabBiography();
                    break;
                case R.id.rb_badges:
                    onChangeTabBiography();
                    break;
                case R.id.rb_portfolio:
                    onChangeTabBiography();
                    break;
            }
        });


    }

    private void onChangeTabUser() {

        if (rbAsAPoster.isChecked()) {
            if (userAccountModel.getPosterRatings() != null && userAccountModel.getPosterRatings().getAvgRating() != null) {
                ratingbar.setProgress(userAccountModel.getPosterRatings().getAvgRating());
                txtRatingValue.setText("(" + userAccountModel.getPosterRatings().getAvgRating() + ")");
            }
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCompletionRate() != null) {
                txtCompletionRate.setText(userAccountModel.getPostTaskStatistics().getCompletionRate() + "% Completion Rate");
            }
            if (userAccountModel.getPosterRatings() != null &&
                    userAccountModel.getPosterRatings().getReceivedReviews() != null) {
                if (userAccountModel.getPosterRatings().getReceivedReviews() <= 1) {
                    txtReviewsCount.setText(userAccountModel.getPosterRatings().getReceivedReviews() + " review");
                } else {
                    txtReviewsCount.setText(userAccountModel.getPosterRatings().getReceivedReviews() + " reviews");
                }
            }
        } else {

            if (userAccountModel.getWorkerRatings() != null && userAccountModel.getWorkerRatings().getAvgRating() != null) {
                ratingbar.setProgress(userAccountModel.getWorkerRatings().getAvgRating());
                txtRatingValue.setText("(" + userAccountModel.getWorkerRatings().getAvgRating() + ")");
            }
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompletionRate() != null) {
                txtCompletionRate.setText(userAccountModel.getWorkTaskStatistics().getCompletionRate() + "% Completion Rate");
            }

            if (userAccountModel.getWorkerRatings() != null
                    && userAccountModel.getWorkerRatings().getReceivedReviews() != null) {

                if (userAccountModel.getWorkerRatings().getReceivedReviews() <= 1) {
                    txtReviewsCount.setText(userAccountModel.getWorkerRatings().getReceivedReviews() + " review");
                } else {
                    txtReviewsCount.setText(userAccountModel.getWorkerRatings().getReceivedReviews() + " reviews");
                }
            }

        }
    }

    private void onChangeTabBiography() {

        if (rbAbout.isChecked()) {
            lytAbout.setVisibility(View.VISIBLE);
            recyclerViewPortfolio.setVisibility(View.GONE);
            lytSkills.setVisibility(View.GONE);
            recyclerViewBadges.setVisibility(View.GONE);
        } else if (rbSkills.isChecked()) {
            lytAbout.setVisibility(View.GONE);
            recyclerViewPortfolio.setVisibility(View.GONE);
            lytSkills.setVisibility(View.VISIBLE);
            recyclerViewBadges.setVisibility(View.GONE);
        } else if (rbBadges.isChecked()) {
            lytAbout.setVisibility(View.GONE);
            recyclerViewPortfolio.setVisibility(View.GONE);
            lytSkills.setVisibility(View.GONE);
            recyclerViewBadges.setVisibility(View.VISIBLE);

        } else if (rbPortfolio.isChecked()) {
            lytAbout.setVisibility(View.GONE);
            recyclerViewPortfolio.setVisibility(View.VISIBLE);
            lytSkills.setVisibility(View.GONE);
            recyclerViewBadges.setVisibility(View.GONE);
        }
    }

    private void init() {
        lytBtnGetAQuote.setVisibility(View.GONE);
        recyclerViewPortfolio.setLayoutManager(new GridLayoutManager(dashboardActivity, 3));
        recyclerViewPortfolio.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(dashboardActivity, 5), true));
        recyclerViewPortfolio.setHasFixedSize(true);
        //set data and list adapter
        adapter = new AttachmentAdapter(dashboardActivity, attachmentArrayList, false);
        recyclerViewPortfolio.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        badgesAdapter = new BadgesAdapter(dashboardActivity, badgesModelArrayList, false);

        recyclerViewBadges.setLayoutManager(new LinearLayoutManager(dashboardActivity));
        recyclerViewBadges.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(dashboardActivity, 5), true));
        recyclerViewBadges.setHasFixedSize(true);
        recyclerViewBadges.setAdapter(badgesAdapter);

    }

    private void getAllProfileData() {
        dashboardActivity.showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PROFILE + "/" + sessionManager.getUserAccount().getId(),
                response -> {
                    Log.e("response", response);
                    dashboardActivity.hidepDialog();
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
                            badgesModelArrayList = userAccountModel.getBadges();
                            Log.e(TAG, attachmentArrayList.size() + "");
                            if (attachmentArrayList.size() != 0) {

                                // recyclerViewPortfolio.setVisibility(View.VISIBLE);
                                adapter.addItems(attachmentArrayList);
                            }
                            if (badgesModelArrayList.size() != 0) {
                                badgesAdapter.addItems(badgesModelArrayList);
                            }
                        } else {
                            dashboardActivity.showToast("Something went wrong", dashboardActivity);
                        }

                    } catch (JSONException e) {
                        dashboardActivity.showToast("JSONException", dashboardActivity);
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    dashboardActivity.errorHandle1(error.networkResponse);
                    dashboardActivity.hidepDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(dashboardActivity);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());

    }

    private void setUpAllEditFields(UserAccountModel userAccountModel) {

        txtAbout.setText(userAccountModel.getAbout());
        if (userAccountModel.getIsVerifiedAccount() == 1) {
            imgVerified.setVisibility(View.VISIBLE);
        } else {
            imgVerified.setVisibility(View.GONE);

        }
        if (userAccountModel.getSkills().getEducation() != null && userAccountModel.getSkills().getEducation().size() != 0) {
            lytEducation.setVisibility(View.VISIBLE);
            tagEducation.setText(userAccountModel.getSkills().getEducation());
        } else {
            lytEducation.setVisibility(View.GONE);
            tagEducation.setText(new ArrayList<>());
        }
        if (userAccountModel.getSkills().getExperience() != null && userAccountModel.getSkills().getExperience().size() != 0) {
            lytExperience.setVisibility(View.VISIBLE);
            tagExperience.setText(userAccountModel.getSkills().getExperience());
        } else {
            lytExperience.setVisibility(View.GONE);
            tagExperience.setText(new ArrayList<>());
        }
        if (userAccountModel.getSkills().getLanguage() != null && userAccountModel.getSkills().getLanguage().size() != 0) {
            lytLanguage.setVisibility(View.VISIBLE);
            tagLanguage.setText(userAccountModel.getSkills().getLanguage());
        } else {
            lytLanguage.setVisibility(View.GONE);
            tagLanguage.setText(new ArrayList<>());
        }
        if (userAccountModel.getSkills().getSpecialities() != null && userAccountModel.getSkills().getSpecialities().size() != 0) {
            lytSpecialities.setVisibility(View.VISIBLE);
            tagSpecialities.setText(userAccountModel.getSkills().getSpecialities());
        } else {
            lytSpecialities.setVisibility(View.GONE);
            tagSpecialities.setText(new ArrayList<>());
        }
        if (userAccountModel.getSkills().getTransportation() != null && userAccountModel.getSkills().getTransportation().size() != 0) {
            lytTransportation.setVisibility(View.VISIBLE);
            tagTransportation.setText(userAccountModel.getSkills().getTransportation());
        } else {
            lytTransportation.setVisibility(View.GONE);
            tagTransportation.setText(new ArrayList<>());
        }
        onChangeTabBiography();
        onChangeTabUser();
        if (userAccountModel.getAvatar() != null) {
            ImageUtil.displayImage(imgAvatar, userAccountModel.getAvatar().getThumbUrl(), null);
        }
        txtFullName.setText(userAccountModel.getName());
        txtSuburb.setText(userAccountModel.getLocation());
        txtAccountLevel.setText(userAccountModel.getWorkerTier().getName());
        txtLastSeen.setText("Last Seen  " + userAccountModel.getLastOnline());

    }

    @OnClick(R.id.txt_btn_see_reviews)
    public void onViewClicked() {
        if (rbAsAPoster.isChecked()) {


            Bundle bundle = new Bundle();
            bundle.putInt(Constant.userID, userAccountModel.getId());
            bundle.putString("WhoIs", Constant.AS_A_POSTER);
            //        bundle.putParcelable(Constant.userAccount, userAccountModel);

            startActivity(new Intent(dashboardActivity, ReviewsActivity.class)
                    .putExtras(bundle)
            );

        } else {

            Bundle bundle = new Bundle();
            bundle.putInt(Constant.userID, userAccountModel.getId());
            bundle.putString("WhoIs", Constant.AS_A_WORKER);
            //      bundle.putParcelable(Constant.userAccount, userAccountModel);

            startActivity(new Intent(dashboardActivity, ReviewsActivity.class)
                    .putExtras(bundle));


        }
    }

    @Override
    public void updatedSuccesfully(String path) {
        if (path != null) {
            ImageUtil.displayImage(imgAvatar, path, null);
        }

    }

    @Override
    public void updateProfile() {
        adapter.clear();
        getAllProfileData();

    }

    @Override
    public void onItemClick(View view, AttachmentModel obj, int position, String action) {
        Intent intent = new Intent(getContext(), ZoomImageActivity.class);
        intent.putExtra("url", attachmentArrayList);
        intent.putExtra("title", "");
        intent.putExtra("pos", position);
        startActivity(intent);
    }
}
