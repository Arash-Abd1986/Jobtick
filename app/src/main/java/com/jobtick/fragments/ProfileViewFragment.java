package com.jobtick.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.EditProfileActivity;
import com.jobtick.activities.ProfileActivity;
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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileViewFragment extends Fragment implements onProfileUpdateListener, AttachmentAdapter.OnItemClickListener {
    public Integer userId=-1;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_portfolio)
    RecyclerView recyclerViewPortfolio;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_about)
    TextView txtAbout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvAboutHeading)
    TextView tvAboutHeading;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_verified)
    ImageView imgVerified;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tag_education)
    TagContainerLayout tagEducation;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_education)
    LinearLayout lytEducation;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tag_specialities)
    TagContainerLayout tagSpecialities;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_specialities)
    LinearLayout lytSpecialities;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tag_language)
    TagContainerLayout tagLanguage;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_language)
    LinearLayout lytLanguage;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tag_experience)
    TagContainerLayout tagExperience;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_experience)
    LinearLayout lytExperience;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tag_transportation)
    TagContainerLayout tagTransportation;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_transportation)
    LinearLayout lytTransportation;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_full_name)
    TextView txtFullName;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_suburb)
    TextView txtSuburb;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_last_seen)
    TextView txtLastSeen;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvViewAllReviews)
    TextView tvViewAllReviews;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvSkills)
    TextView tvSkills;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_account_level)
    TextView txtAccountLevel;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ratingbarAsTicker)
    RatingBar ratingbarAsTicker;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ratingbarAsPoster)
    RatingBar ratingbarAsPoster;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvTickerReview)
    TextView tvTickerReview;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.content)
    LinearLayout content;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvPosterReview)
    TextView tvPosterReview;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvTickerCompletionRate)
    TextView tvTickerCompletionRate;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvPosterCompletionRate)
    TextView tvPosterCompletionRate;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rbPortfollio)
    RadioButton rbPortfollio;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rbSkills)
    RadioButton rbSkills;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_get_quote)
    CardView btnQuote;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llEnlarge)
    LinearLayout llEnlarge;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivMedalBoronz)
    ImageView ivMedalBoronz;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivMedalSilver)
    ImageView ivMedalSilver;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivMedalGOld)
    ImageView ivMedalGOld;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivMedalMax)
    ImageView ivMedalMax;

    private ProfileActivity profileActivity;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private UserAccountModel userAccountModel;
    private ArrayList<AttachmentModel> attachmentArrayList;
    private ArrayList<BadgesModel> badgesModelArrayList;
    private AttachmentAdapter adapter;
    private BadgesAdapter badgesAdapter;
    public static onProfileUpdateListener onProfileupdatelistener;
    private Typeface poppins_medium;
    private LinearLayout lPort, lSkill, NoPortfolio,NoAbout;
    private  ImageView ivLevelInfo,ivProfileInfo;
    private LinearLayout noReview,tickerReview,posterReview,noSkill;
    private TextView txtNoReview,addSkill,addPortFilo;
    public ProfileViewFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllProfileData();
    }

    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);
        ButterKnife.bind(this, view);


        onProfileupdatelistener = this;
        NoPortfolio = view.findViewById(R.id.no_port_folio);
        lPort = view.findViewById(R.id.lyt_Port);
        lSkill = view.findViewById(R.id.lyt_skills);
        NoAbout=view.findViewById(R.id.no_about);
        noSkill=view.findViewById(R.id.no_port_skill);
        addPortFilo=view.findViewById(R.id.txt_add_portfolio);

        addSkill=view.findViewById(R.id.txt_add_skill);
        txtNoReview=view.findViewById(R.id.tv_no_review);
        noReview=view.findViewById(R.id.no_review);
        posterReview=view.findViewById(R.id.poster_review);
        tickerReview=view.findViewById(R.id.ticker_review);
        ivLevelInfo=view.findViewById(R.id.ivLevelInfo);
        ivProfileInfo=view.findViewById(R.id.ivProfileInfo);
        ivLevelInfo.setOnClickListener(view1 -> {
            LevelsInfoBottomSheet levelsInfoBottomSheet = new LevelsInfoBottomSheet();
            levelsInfoBottomSheet.show(getParentFragmentManager(), "");
        });
        ivProfileInfo.setOnClickListener(view1 -> {
            LevelInfoBottomSheet levelInfoBottomSheet = new LevelInfoBottomSheet();
            levelInfoBottomSheet.show(getParentFragmentManager(), "");
        });
        initToolbar();
        Bundle b = getArguments();
        userId = Objects.requireNonNull(b).getInt("userId",-1);
        return view;
    }

    private void initToolbar() {
        profileActivity = (ProfileActivity) requireActivity();
        poppins_medium = ResourcesCompat.getFont(getContext(), R.font.roboto_medium);
        onProfileupdatelistener = this;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(profileActivity);
        userAccountModel = new UserAccountModel();
        attachmentArrayList = new ArrayList<>();
        badgesModelArrayList = new ArrayList<>();

        init();

        getAllProfileData();
        initComponent();
        //   initComponentScroll(view);

    }

    private void initComponentScroll(View view) {
        NestedScrollView nested_content = view.findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY < oldScrollY) { // up
                animateFab(true);
            }
            if (scrollY > oldScrollY) { // down
                animateFab(true);
            }
        });
    }

    boolean isFabHide = false;

    private void animateFab(final boolean hide) {
        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;
        int moveY = hide ? (2 * btnQuote.getHeight()) : 0;
        btnQuote.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void initComponent() {
        rbPortfollio.setOnCheckedChangeListener((group, checkedId) -> onChangeTabBiography());
        rbSkills.setOnCheckedChangeListener((group, checkedId) -> onChangeTabBiography());
    }

    @SuppressLint("SetTextI18n")
    private void onChangeTabUser() {

        if (userAccountModel.getPosterRatings() != null && userAccountModel.getPosterRatings().getAvgRating() != null) {
            ratingbarAsPoster.setProgress(userAccountModel.getPosterRatings().getAvgRating());
            tvTickerReview.setText("(" + userAccountModel.getPosterRatings().getAvgRating() + ")");
        }
        if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCompletionRate() != null) {
            tvTickerCompletionRate.setText(userAccountModel.getPostTaskStatistics().getCompletionRate() + "%");
        }
    }

    private void onChangeTabBiography() {
        if (rbPortfollio.isChecked()) {
            //lytAbout.setVisibility(View.VISIBLE);
            if (attachmentArrayList.size() <= 0) {
                NoPortfolio.setVisibility(View.VISIBLE);
                noSkill.setVisibility(View.GONE);
                recyclerViewPortfolio.setVisibility(View.GONE);
                addPortFilo.setOnClickListener(view13 -> {
                    Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
                    startActivity(intent);
                });
                lPort.setVisibility(View.GONE);
            } else {
                if(attachmentArrayList.size() > 10){
                    ((ActivityBase)requireActivity()).showToast( "MAX 10 picture", requireContext());
                }
                recyclerViewPortfolio.setVisibility(View.VISIBLE);
                noSkill.setVisibility(View.GONE);
                NoPortfolio.setVisibility(View.GONE);
                lPort.setVisibility(View.VISIBLE);
            }
            lSkill.setVisibility(View.GONE);
            rbPortfollio.setTextColor(getResources().getColor(R.color.blue));
            rbSkills.setTextColor(getResources().getColor(R.color.textColor));
        } else
        if (rbSkills.isChecked()) {
            if (tagEducation.size()<=0 && tagExperience.size()<=0 && tagLanguage.size() <= 0
                    && tagSpecialities.size()<=0&& tagTransportation.size()<=0) {
                NoPortfolio.setVisibility(View.GONE);
                noSkill.setVisibility(View.VISIBLE);
                addSkill.setOnClickListener(view13 -> {
                    Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
                    startActivity(intent);
                });
                lSkill.setVisibility(View.GONE);
                tvSkills.setVisibility(View.GONE);
            } else {
                NoPortfolio.setVisibility(View.GONE);
                noSkill.setVisibility(View.GONE);
                lSkill.setVisibility(View.VISIBLE);
                tvSkills.setVisibility(View.VISIBLE);
            }
            recyclerViewPortfolio.setVisibility(View.GONE);
            lPort.setVisibility(View.GONE);
            rbPortfollio.setTextColor(getResources().getColor(R.color.textColor));
            rbSkills.setTextColor(getResources().getColor(R.color.blue));

        }
    }

    private void init() {

        recyclerViewPortfolio.setLayoutManager(new GridLayoutManager(profileActivity, 3));
        recyclerViewPortfolio.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(profileActivity, 3), true));
        recyclerViewPortfolio.setHasFixedSize(true);

        adapter = new AttachmentAdapter(attachmentArrayList, false);
        recyclerViewPortfolio.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        badgesAdapter = new BadgesAdapter(badgesModelArrayList);
    }

    private void getAllProfileData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PROFILE + "/" + userId,
                response -> {
                    Timber.e(response);
                    content.setVisibility(View.VISIBLE);
                    pbLoading.setVisibility(View.GONE);
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            userAccountModel = new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"));
                            setUpAllEditFields(userAccountModel);
                            attachmentArrayList = userAccountModel.getPortfolio();
                            adapter.clear();
                            badgesModelArrayList = userAccountModel.getBadges();


                            if (attachmentArrayList.size() <= 0) {
                                NoPortfolio.setVisibility(View.VISIBLE);
                                lPort.setVisibility(View.GONE);
                            } else {
                                recyclerViewPortfolio.setVisibility(View.VISIBLE);
                                NoPortfolio.setVisibility(View.GONE);
                                lPort.setVisibility(View.VISIBLE);
                            }
                            if(attachmentArrayList.size() > 10){
                                ((ActivityBase)requireActivity()).showToast("MAX 10 picture", requireContext());
                                adapter.addItems(attachmentArrayList);
                            }


                            if (badgesModelArrayList.size() <= 0) {
                                NoPortfolio.setVisibility(View.VISIBLE);
                                lSkill.setVisibility(View.GONE);
                            } else {
                                NoPortfolio.setVisibility(View.GONE);
                                lSkill.setVisibility(View.VISIBLE);
                            }
                            badgesAdapter.addItems(badgesModelArrayList);

                            if(userAccountModel.getPortfolio().size()==0)
                            {
                                NoPortfolio.setVisibility(View.VISIBLE);
                            }else{
                                NoPortfolio.setVisibility(View.GONE);
                            }
                            if(rbPortfollio.isChecked())
                            {
                                lSkill.setVisibility(View.GONE);
                                noSkill.setVisibility(View.GONE);
                            }
                        } else {
                            profileActivity.showToast("Something went wrong", profileActivity);
                        }

                    } catch (JSONException e) {
                        profileActivity.showToast("JSONException", profileActivity);
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    profileActivity.errorHandle1(error.networkResponse);
                    profileActivity.hideProgressDialog();
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
        RequestQueue requestQueue = Volley.newRequestQueue(profileActivity);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());

    }

    @SuppressLint("SetTextI18n")
    private void setUpAllEditFields(UserAccountModel userAccountModel) {
        tvAboutHeading.setTypeface(txtAbout.getTypeface(), Typeface.BOLD_ITALIC);

        if (userAccountModel.getAbout() == null || userAccountModel.getAbout().equals("")) {
            txtAbout.setText("Nothing to show");
            txtAbout.setVisibility(View.GONE);
        } else {
            txtAbout.setVisibility(View.VISIBLE);
            txtAbout.setText("" + userAccountModel.getAbout());

        }
        if (userAccountModel.getTagline() == null ||userAccountModel.getTagline().equals("")) {
            tvAboutHeading.setText("\"Nothing to show\"");
            tvAboutHeading.setVisibility(View.GONE);
        } else {
            tvAboutHeading.setVisibility(View.VISIBLE);
            tvAboutHeading.setText("\"" + userAccountModel.getTagline()+"\"");
        }

        if(userAccountModel.getWorkerRatings()==null){
            tickerReview.setVisibility(View.GONE);
            noReview.setVisibility(View.VISIBLE);
            txtNoReview.setVisibility(View.VISIBLE);

        }else{
            noReview.setVisibility(View.GONE);
            tickerReview.setVisibility(View.VISIBLE);
            ratingbarAsTicker.setRating(userAccountModel.getWorkerRatings().getAvgRating());
            tvTickerReview.setText("("+userAccountModel.getWorkerRatings().getTotalRatings().toString()+")");
            if(userAccountModel.getWorkTaskStatistics()!=null)
                tvTickerCompletionRate.setText(userAccountModel.getWorkTaskStatistics().getCompletionRate().toString()+"%");
        }
        if(userAccountModel.getPosterRatings()==null){
            posterReview.setVisibility(View.GONE);
            noReview.setVisibility(View.VISIBLE);
            txtNoReview.setVisibility(View.VISIBLE);
        }else{
            posterReview.setVisibility(View.VISIBLE);
            noReview.setVisibility(View.GONE);
            ratingbarAsPoster.setRating(userAccountModel.getPosterRatings().getAvgRating());
            tvPosterReview.setText("("+userAccountModel.getPosterRatings().getTotalRatings().toString()+")");
            if(userAccountModel.getPostTaskStatistics()!=null)
                tvPosterCompletionRate.setText(userAccountModel.getPostTaskStatistics().getCompletionRate().toString()+"%");
        }

        switch (userAccountModel.getWorkerTier().getId()){
            case 1:
                ivMedalBoronz.setImageResource(R.drawable.ic_boronz_selected);
                break;
            case 2:
                ivMedalBoronz.setImageResource(R.drawable.ic_silver_selected);
                break;
            case 3:
                ivMedalBoronz.setImageResource(R.drawable.ic_gold_selected);
                break;
            case 4:
                ivMedalBoronz.setImageResource(R.drawable.ic_max_selected);
                break;
        }

        if (userAccountModel.getIsVerifiedAccount() == 1) {
            imgVerified.setVisibility(View.VISIBLE);
        } else {
            imgVerified.setVisibility(View.GONE);
        }
        if(userAccountModel.getPortfolio().size()==0)
        {
            NoPortfolio.setVisibility(View.VISIBLE);
        }else{
            NoPortfolio.setVisibility(View.GONE);
        }
        if(userAccountModel.getSkills().getExperience() == null && userAccountModel.getSkills().getExperience().size() == 0 &&
                userAccountModel.getSkills().getLanguage() == null && userAccountModel.getSkills().getLanguage().size() == 0 &&
                userAccountModel.getSkills().getSpecialities() == null && userAccountModel.getSkills().getSpecialities().size() == 0 &&
                userAccountModel.getSkills().getTransportation() == null && userAccountModel.getSkills().getTransportation().size() == 0 &&
                userAccountModel.getSkills().getEducation() == null && userAccountModel.getSkills().getEducation().size() == 0){
            NoPortfolio.setVisibility(View.GONE);
            lSkill.setVisibility(View.GONE);
            lytEducation.setVisibility(View.GONE);
            lytExperience.setVisibility(View.GONE);
            lytLanguage.setVisibility(View.GONE);
            lytSpecialities.setVisibility(View.GONE);
            lytTransportation.setVisibility(View.GONE);
            recyclerViewPortfolio.setVisibility(View.GONE);
            lPort.setVisibility(View.GONE);
        }
        else{
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
        }
        tagEducation.setTagTypeface(poppins_medium);
        tagSpecialities.setTagTypeface(poppins_medium);
        tagLanguage.setTagTypeface(poppins_medium);
        tagExperience.setTagTypeface(poppins_medium);
        tagTransportation.setTagTypeface(poppins_medium);
        if (userAccountModel.getAvatar() != null) {
            ImageUtil.displayImage(imgAvatar, userAccountModel.getAvatar().getThumbUrl(), null);
        }
        txtFullName.setText(userAccountModel.getName());
        txtSuburb.setText(userAccountModel.getLocation());
        //txtAccountLevel.setText(""+userAccountModel.getWorkerTier().getName());
        txtLastSeen.setText("Last Seen  " + userAccountModel.getLastOnline());
        tvViewAllReviews.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putInt(Constant.userID, userAccountModel.getId());
            bundle.putString("WhoIs", Constant.AS_A_WORKER);
            //      bundle.putParcelable(Constant.userAccount, userAccountModel);

            startActivity(new Intent(profileActivity, ReviewsActivity.class)
                    .putExtras(bundle));
        });
        onChangeTabBiography();
        onChangeTabUser();
    }

    public void onViewClicked() {
        /*if (rbAsAPoster.isChecked()) {


            Bundle bundle = new Bundle();
            bundle.putInt(Constant.userID, userAccountModel.getId());
            bundle.putString("WhoIs", Constant.AS_A_POSTER);
            //        bundle.putParcelable(Constant.userAccount, userAccountModel);

            startActivity(new Intent(profileActiviy, ReviewsActivity.class)
                    .putExtras(bundle)
            );

        } else {

            Bundle bundle = new Bundle();
            bundle.putInt(Constant.userID, userAccountModel.getId());
            bundle.putString("WhoIs", Constant.AS_A_WORKER);
            //      bundle.putParcelable(Constant.userAccount, userAccountModel);

            startActivity(new Intent(profileActiviy, ReviewsActivity.class)
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
