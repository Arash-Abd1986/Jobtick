package com.jobtick.fragments;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;

import com.jobtick.activities.AbstractUploadableImageImpl;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.activities.UploadableImage;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

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
import static com.jobtick.utils.ConstantKey.TAG;

public class ProfileReqFragment extends Fragment {

    private CircularImageView imgAvatar;
    SessionManager sessionManager;
    private UserAccountModel userAccountModel;
    MaterialButton btnNext;

    private UploadableImage uploadableImage;

    public ProfileReqFragment() {
    }

    public static ProfileReqFragment newInstance() {
        return new ProfileReqFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        imgAvatar = view.findViewById(R.id.img_user_avatar);
        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            ((TickerRequirementsBottomSheet) getParentFragment()).changeFragment(1);
        });

        userAccountModel = ((TaskDetailsActivity) getActivity()).userAccountModel;
        setUpAvatar(userAccountModel);

        uploadableImage = new AbstractUploadableImageImpl(requireActivity()) {
            @Override
            public void onImageReady(File imageFile) {
                uploadProfileAvatar(imageFile);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_req, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setUpAvatar(UserAccountModel userAccountModel) {
        try {
            ImageUtil.displayImage(imgAvatar, userAccountModel.getAvatar().getUrl(), null);
        } catch (Exception ex) {
            Timber.tag(TAG).e("EXCEPTION CAUGHT WHILE EXECUTING DATABASE TRANSACTION");
            ex.printStackTrace();
        }
    }

    @OnClick({R.id.img_user_avatar})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_user_avatar) {
            uploadableImage.showAttachmentBottomSheet(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uploadableImage.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileAvatar(File pictureFile) {
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().uploadProfilePicture("XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String res = response.body();
                System.out.println(res);
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    if (getActivity() != null) {
                        ((ActivityBase) getActivity()).showToast(response.message(), getActivity());
                    }
                    return;
                }
                try {
                    String strResponse = res;
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        ((ActivityBase)requireActivity()).showToast("not found", requireContext());
                        return;
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        ((ActivityBase) getActivity()).unauthorizedUser();
                        return;
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        JSONObject jsonObject = new JSONObject(strResponse);
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
                            ImageUtil.displayImage(imgAvatar, attachment.getUrl(), null);

                            if (onProfileupdatelistener != null) {
                                onProfileupdatelistener.updatedSuccesfully(attachment.getUrl());
                            }
                            if (onProfileupdatelistenerSideMenu != null) {
                                onProfileupdatelistenerSideMenu.updatedSuccesfully(attachment.getUrl());
                            }
                        }
                        //   adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // ((ActivityBase) getActivity()).showToast("attachment added", getActivity());
                        btnNext.setEnabled(true);
                    } else {
                        ((ActivityBase) getActivity()).showToast("Something went wrong", getActivity());
                    }
                } catch (JSONException e) {
                    ((ActivityBase) getActivity()).showToast("Something went wrong", getActivity());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (getActivity() != null) {
                    ((ActivityBase) getActivity()).hideProgressDialog();
                }
            }
        });
    }
}