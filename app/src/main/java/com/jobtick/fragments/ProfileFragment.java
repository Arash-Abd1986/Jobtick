package com.jobtick.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.activities.DashboardActivity;
import com.jobtick.activities.EditProfileActivity;
import com.jobtick.activities.ReviewsActivity;
import com.jobtick.activities.ZoomImageActivity;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.adapers.BadgesAdapter;
import com.jobtick.interfaces.onProfileUpdateListener;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.BadgesModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;
import com.jobtick.widget.SpacingItemDecoration;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements onProfileUpdateListener, AttachmentAdapter.OnItemClickListener {
    private static final String TAG = ProfileFragment.class.getName();

    @BindView(R.id.recycler_view_portfolio)
    RecyclerView recyclerViewPortfolio;

    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;

    @BindView(R.id.txt_about)
    TextView txtAbout;

    @BindView(R.id.tvAboutHeading)
    TextView tvAboutHeading;

    @BindView(R.id.img_verified)
    ImageView imgVerified;

    @BindView(R.id.tag_education)
    TagContainerLayout tagEducation;


    @BindView(R.id.lyt_education)
    LinearLayout lytEducation;
    @BindView(R.id.tag_specialities)
    TagContainerLayout tagSpecialities;

    @BindView(R.id.lyt_specialities)
    LinearLayout lytSpecialities;
    @BindView(R.id.tag_language)
    TagContainerLayout tagLanguage;

    @BindView(R.id.lyt_language)
    LinearLayout lytLanguage;
    @BindView(R.id.tag_experience)
    TagContainerLayout tagExperience;

    @BindView(R.id.lyt_experience)
    LinearLayout lytExperience;
    @BindView(R.id.tag_transportation)
    TagContainerLayout tagTransportation;
    @BindView(R.id.lyt_transportation)
    LinearLayout lytTransportation;
    @BindView(R.id.lyt_skills)
    LinearLayout lytSkills;

    @BindView(R.id.img_level)
    ImageView imgLevel;
    @BindView(R.id.txt_full_name)
    TextView txtFullName;
    @BindView(R.id.txt_suburb)
    TextView txtSuburb;
    @BindView(R.id.txt_last_seen)
    TextView txtLastSeen;

    @BindView(R.id.tvViewAllReviews)
    TextView tvViewAllReviews;

    @BindView(R.id.tvSkills)
    TextView tvSkills;

    @BindView(R.id.txt_account_level)
    TextView txtAccountLevel;

    @BindView(R.id.ratingbarAsTicker)
    RatingBar ratingbarAsTicker;

    @BindView(R.id.ratingbarAsPoster)
    RatingBar ratingbarAsPoster;

    @BindView(R.id.tvTickerReview)
    TextView tvTickerReview;

    @BindView(R.id.tvPosterReview)
    TextView tvPosterReview;

    @BindView(R.id.tvTickerCompletionRate)
    TextView tvTickerCompletionRate;


    @BindView(R.id.tvPosterCompletionRate)
    TextView tvPosterCompletionRate;

    @BindView(R.id.rbPortfollio)
    RadioButton rbPortfollio;

    @BindView(R.id.rbSkills)
    RadioButton rbSkills;

    @BindView(R.id.card_get_quote)
    CardView card_get_quote;

    @BindView(R.id.llEnlarge)
    LinearLayout llEnlarge;


    /*

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


    @BindView(R.id.recycler_view_badges)
    RecyclerView recyclerViewBadges;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;


    @BindView(R.id.ratingbar)
    AppCompatRatingBar ratingbar;
    @BindView(R.id.txt_completion_rate)
    TextView txtCompletionRate;
    @BindView(R.id.txt_rating_value)
    TextView txtRatingValue;
    @BindView(R.id.txt_reviews_count)
    TextView txtReviewsCount;
    @BindView(R.id.txt_btn_see_reviews)
    TextView txtBtnSeeReviews;*/

    private DashboardActivity dashboardActivity;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private UserAccountModel userAccountModel;
    private ArrayList<AttachmentModel> attachmentArrayList;
    private ArrayList<BadgesModel> badgesModelArrayList;

    private AttachmentAdapter adapter;
    private BadgesAdapter badgesAdapter;
    public static onProfileUpdateListener onProfileupdatelistener;
    Typeface poppins_medium;
    ImageView ivNotification;
    TextView toolbar_title;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        onProfileupdatelistener = this;

        dashboardActivity = (DashboardActivity) getActivity();
        poppins_medium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/poppins_Medium.otf");
        onProfileupdatelistener = this;
        if (dashboardActivity != null) {
            toolbar = dashboardActivity.findViewById(R.id.toolbar);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_profile);
            ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
            ivNotification.setVisibility(View.GONE);
            toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
            toolbar_title.setVisibility(View.VISIBLE);

            toolbar_title.setText("Profile");

            toolbar_title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_medium));
            toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_100));
            androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            toolbar_title.setLayoutParams(params);

            toolbar.post(() -> {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
                toolbar.setNavigationIcon(d);
            });

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

        /*init();
        getAllProfileData();
        initComponent();*/

        init();

        getAllProfileData();
        initComponent();
        initComponentScroll(view);

      /*  initComponent();
        init(view);*/
        //init(view);

    }

    private void initComponentScroll(View view) {
        NestedScrollView nested_content = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateFab(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateFab(true);
                }
            }
        });
    }

    boolean isFabHide = false;

    private void animateFab(final boolean hide) {
        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;
        int moveY = hide ? (2 * card_get_quote.getHeight()) : 0;
        card_get_quote.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }
   /* public void init(View view)
    {

        Toolbar toolbar_title=(Toolbar)getActivity().findViewById(R.id.toolbar);

        TextView title=(TextView) toolbar_title.findViewById(R.id.toolbar_title);
        title.setVisibility(View.GONE);

        ImageView ivNotification=(ImageView)toolbar_title.findViewById(R.id.ivNotification);
        ivNotification.setImageResource(R.drawable.ic_edit_profile);

        RadioButton rbPortfollio=(RadioButton)view.findViewById(R.id.rbPortfollio);
        RadioButton rbSkills=(RadioButton)view.findViewById(R.id.rbSkills);

        //LinearLayout llSkill=(LinearLayout)view.findViewById(R.id.llSkill);
        //LinearLayout llPortfolloi=(LinearLayout)view.findViewById(R.id.llPortfolloi);

        //TextView tvViewAll=(TextView)view.findViewById(R.id.tvViewAll);
       // TextView tvSkills=(TextView)view.findViewById(R.id.tvSkills);
        //tvSkills.setText(getResources().getString(R.string.skills));

        //llSkill.setVisibility(View.VISIBLE);
        //llPortfolloi.setVisibility(View.GONE);

        rbPortfollio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    rbPortfollio.setTextColor(getResources().getColor(R.color.blue));
                    rbSkills.setTextColor(getResources().getColor(R.color.textColor));

                   // llSkill.setVisibility(View.GONE);
                    //llPortfolloi.setVisibility(View.VISIBLE);
                    //tvViewAll.setVisibility(View.GONE);
                    //tvSkills.setText(getResources().getString(R.string.portfolio));
                }
            }
        });

        rbSkills.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    rbPortfollio.setTextColor(getResources().getColor(R.color.textColor));
                    rbSkills.setTextColor(getResources().getColor(R.color.blue));

                   // llSkill.setVisibility(View.VISIBLE);
                    //llPortfolloi.setVisibility(View.GONE);
                    //tvViewAll.setVisibility(View.VISIBLE);

                    //tvSkills.setText(getResources().getString(R.string.skills));
                }

            }
        });
    }*/


    private void initComponent() {
        rbPortfollio.setOnCheckedChangeListener((group, checkedId) -> {

            onChangeTabBiography();


        });

        rbSkills.setOnCheckedChangeListener((group, checkedId) -> {

            onChangeTabBiography();


        });
        /*rgTickerPoster.setOnCheckedChangeListener((group, checkedId) -> {

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
        });*/


    }

    private void onChangeTabUser() {

        if (userAccountModel.getPosterRatings() != null && userAccountModel.getPosterRatings().getAvgRating() != null) {
            ratingbarAsPoster.setProgress(userAccountModel.getPosterRatings().getAvgRating());
            tvTickerReview.setText("(" + userAccountModel.getPosterRatings().getAvgRating() + ")");
        }
        if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCompletionRate() != null) {
            tvTickerCompletionRate.setText(userAccountModel.getPostTaskStatistics().getCompletionRate() + "%");
        }

        /*if (userAccountModel.getPosterRatings() != null &&
                userAccountModel.getPosterRatings().getReceivedReviews() != null)
        {
            if (userAccountModel.getPosterRatings().getReceivedReviews() <= 1)
            {
                txtReviewsCount.setText(userAccountModel.getPosterRatings().getReceivedReviews() + " review");
            } else {
                txtReviewsCount.setText(userAccountModel.getPosterRatings().getReceivedReviews() + " reviews");
            }
        }*/




        /*if (userAccountModel.getWorkerRatings() != null && userAccountModel.getWorkerRatings().getAvgRating() != null) {
            ratingbarAsPoster.setProgress(userAccountModel.getWorkerRatings().getAvgRating());
            tvPosterReview.setText("(" + userAccountModel.getWorkerRatings().getAvgRating() + ")");
        }
        if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompletionRate() != null) {
            tvPosterCompletionRate.setText(userAccountModel.getWorkTaskStatistics().getCompletionRate() + "%");
        }*/

        /*if (userAccountModel.getWorkerRatings() != null
                && userAccountModel.getWorkerRatings().getReceivedReviews() != null) {

            if (userAccountModel.getWorkerRatings().getReceivedReviews() <= 1) {
                txtReviewsCount.setText(userAccountModel.getWorkerRatings().getReceivedReviews() + " review");
            } else {
                txtReviewsCount.setText(userAccountModel.getWorkerRatings().getReceivedReviews() + " reviews");
            }
        }*/

        /*if (rbAsAPoster.isChecked())
        {
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

        }*/
    }

    private void onChangeTabBiography() {

        if (rbPortfollio.isChecked()) {
            //lytAbout.setVisibility(View.VISIBLE);
            recyclerViewPortfolio.setVisibility(View.VISIBLE);
            lytSkills.setVisibility(View.GONE);
            rbPortfollio.setTextColor(getResources().getColor(R.color.blue));
            rbSkills.setTextColor(getResources().getColor(R.color.textColor));

            tvSkills.setVisibility(View.GONE);

            llEnlarge.setVisibility(View.VISIBLE);

            //recyclerViewBadges.setVisibility(View.GONE);
        } else if (rbSkills.isChecked()) {
            // lytAbout.setVisibility(View.GONE);
            recyclerViewPortfolio.setVisibility(View.GONE);
            lytSkills.setVisibility(View.VISIBLE);
            // recyclerViewBadges.setVisibility(View.GONE);

            rbPortfollio.setTextColor(getResources().getColor(R.color.textColor));
            rbSkills.setTextColor(getResources().getColor(R.color.blue));

            tvSkills.setVisibility(View.VISIBLE);
            llEnlarge.setVisibility(View.GONE);
        }

        /*if (rbAbout.isChecked()) {
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
        }*/
    }

    private void init() {
        //lytBtnGetAQuote.setVisibility(View.GONE);
        recyclerViewPortfolio.setLayoutManager(new GridLayoutManager(dashboardActivity, 3));
        //recyclerViewPortfolio.setLayoutManager(new GridLayoutManager(dashboardActivity, 3));

        recyclerViewPortfolio.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(dashboardActivity, 8), true));
        recyclerViewPortfolio.setHasFixedSize(true);
        //set data and list adapter
        adapter = new AttachmentAdapter(dashboardActivity, attachmentArrayList, false);
        recyclerViewPortfolio.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        badgesAdapter = new BadgesAdapter(dashboardActivity, badgesModelArrayList, false);

        /*recyclerViewBadges.setLayoutManager(new LinearLayoutManager(dashboardActivity));
        recyclerViewBadges.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(dashboardActivity, 5), true));
        recyclerViewBadges.setHasFixedSize(true);
        recyclerViewBadges.setAdapter(badgesAdapter);*/

    }

    private void getAllProfileData() {

        dashboardActivity.showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PROFILE + "/" + sessionManager.getUserAccount().getId(),
                response -> {
                    Log.e("response", response);
                    dashboardActivity.hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {

                            userAccountModel = new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"));


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
                    dashboardActivity.hideProgressDialog();
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


        txtAbout.setText("" + userAccountModel.getAbout());
        tvAboutHeading.setText("“" + userAccountModel.getTagline() + "”");

        tagEducation.setTagTypeface(poppins_medium);
        tagSpecialities.setTagTypeface(poppins_medium);
        tagLanguage.setTagTypeface(poppins_medium);
        tagExperience.setTagTypeface(poppins_medium);
        tagTransportation.setTagTypeface(poppins_medium);

        if (userAccountModel.getIsVerifiedAccount() == 1) {
            imgVerified.setVisibility(View.VISIBLE);
        } else {
            imgVerified.setVisibility(View.GONE);

            if (userAccountModel.getSkills().getEducation() != null && userAccountModel.getSkills().getEducation().size() != 0) {
                lytEducation.setVisibility(View.VISIBLE);
                //tagEducation.setText(userAccountModel.getSkills().getEducation());

                tagEducation.setTags(userAccountModel.getSkills().getEducation());

            } else {
                lytEducation.setVisibility(View.GONE);
                tagEducation.setTags(new ArrayList<>());


            }
            if (userAccountModel.getSkills().getExperience() != null && userAccountModel.getSkills().getExperience().size() != 0) {
                lytExperience.setVisibility(View.VISIBLE);
                tagExperience.setTags(userAccountModel.getSkills().getExperience());
            } else {
                lytExperience.setVisibility(View.GONE);
                tagExperience.setTags(new ArrayList<>());
            }
            if (userAccountModel.getSkills().getLanguage() != null && userAccountModel.getSkills().getLanguage().size() != 0) {
                lytLanguage.setVisibility(View.VISIBLE);
                tagLanguage.setTags(userAccountModel.getSkills().getLanguage());
            } else {
                lytLanguage.setVisibility(View.GONE);
                tagLanguage.setTags(new ArrayList<>());
            }
            if (userAccountModel.getSkills().getSpecialities() != null && userAccountModel.getSkills().getSpecialities().size() != 0) {
                lytSpecialities.setVisibility(View.VISIBLE);
                tagSpecialities.setTags(userAccountModel.getSkills().getSpecialities());

            } else {
                lytSpecialities.setVisibility(View.GONE);
                tagSpecialities.setTags(new ArrayList<>());
            }
            if (userAccountModel.getSkills().getTransportation() != null && userAccountModel.getSkills().getTransportation().size() != 0) {
                lytTransportation.setVisibility(View.VISIBLE);
                tagTransportation.setTags(userAccountModel.getSkills().getTransportation());
            } else {
                lytTransportation.setVisibility(View.GONE);
                tagTransportation.setTags(new ArrayList<>());
            }

            if (userAccountModel.getAvatar() != null) {
                ImageUtil.displayImage(imgAvatar, userAccountModel.getAvatar().getThumbUrl(), null);
            }
            txtFullName.setText(userAccountModel.getName());
            txtSuburb.setText(userAccountModel.getLocation());
            //txtAccountLevel.setText(""+userAccountModel.getWorkerTier().getName());
            txtLastSeen.setText("Last Seen  " + userAccountModel.getLastOnline());


            tvViewAllReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.userID, userAccountModel.getId());
                    bundle.putString("WhoIs", Constant.AS_A_WORKER);
                    //      bundle.putParcelable(Constant.userAccount, userAccountModel);

                    startActivity(new Intent(dashboardActivity, ReviewsActivity.class)
                            .putExtras(bundle));
                }
            });

            onChangeTabBiography();
            onChangeTabUser();
       /*
        onChangeTabBiography();
        onChangeTabUser();
       */
        }
    }


    public void onViewClicked() {
        /*if (rbAsAPoster.isChecked()) {


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


        }*/
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
