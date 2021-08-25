package com.jobtick.android.fragments

import com.mikhaellopez.circularimageview.CircularImageView
import com.jobtick.android.models.UserAccountModel
import com.google.android.material.button.MaterialButton
import com.jobtick.android.activities.UploadableImage
import android.os.Bundle
import com.jobtick.android.R
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.activities.AbstractUploadableImageImpl
import android.view.LayoutInflater
import android.view.ViewGroup
import butterknife.ButterKnife
import timber.log.Timber
import com.jobtick.android.utils.ConstantKey
import butterknife.OnClick
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import okhttp3.RequestBody
import okhttp3.MultipartBody
import com.jobtick.android.retrofit.ApiClient
import com.jobtick.android.utils.HttpStatus
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.activities.DashboardActivity
import org.json.JSONException
import com.jobtick.android.utils.ImageUtil
import com.jobtick.android.utils.SessionManager
import okhttp3.MediaType
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception

class ProfileReqFragment : Fragment() {
    private var imgAvatar: CircularImageView? = null
    var sessionManager: SessionManager? = null
    private var userAccountModel: UserAccountModel? = null
    var btnNext: MaterialButton? = null
    private var uploadableImage: UploadableImage? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(context)
        imgAvatar = view.findViewById(R.id.img_user_avatar)
        imgAvatar!!.setOnClickListener {
            if (view.id == R.id.img_user_avatar) {
                uploadableImage!!.showAttachmentImageBottomSheet(true)
            }
        }
        btnNext = view.findViewById(R.id.btn_next)
        btnNext!!.setOnClickListener(View.OnClickListener { v: View? -> (parentFragment as TickerRequirementsBottomSheet?)!!.changeFragment(1) })
        userAccountModel = (activity as TaskDetailsActivity?)!!.userAccountModel
        setUpAvatar(userAccountModel)
        uploadableImage = object : AbstractUploadableImageImpl(requireActivity()) {
            override fun onImageReady(imageFile: File) {
                uploadProfileAvatar(imageFile)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_req, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    private fun setUpAvatar(userAccountModel: UserAccountModel?) {
        try {
            ImageUtil.displayImage(imgAvatar, userAccountModel!!.avatar.url, null)
        } catch (ex: Exception) {
            Timber.tag(ConstantKey.TAG).e("EXCEPTION CAUGHT WHILE EXECUTING DATABASE TRANSACTION")
            ex.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        uploadableImage!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadProfileAvatar(pictureFile: File) {
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().uploadProfilePicture("XMLHttpRequest", sessionManager!!.tokenType + " " + sessionManager!!.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                val res = response.body()
                println(res)
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    if (activity != null) {
                        (activity as ActivityBase?)!!.showToast(response.message(), activity)
                    }
                    return
                }
                try {
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        (requireActivity() as ActivityBase).showToast("not found", requireContext())
                        return
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        (activity as ActivityBase?)!!.unauthorizedUser()
                        return
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        val jsonObject = JSONObject(res)
                        if (jsonObject.has("data")) {
                            val attachment = AttachmentModel()
                            val jsonObjectData = jsonObject.getJSONObject("data")
                            if (jsonObjectData.has("id") && !jsonObjectData.isNull("id")) attachment.id = jsonObjectData.getInt("id")
                            if (jsonObjectData.has("name") && !jsonObjectData.isNull("name")) attachment.name = jsonObjectData.getString("name")
                            if (jsonObjectData.has("file_name") && !jsonObjectData.isNull("file_name")) attachment.fileName = jsonObjectData.getString("file_name")
                            if (jsonObjectData.has("mime") && !jsonObjectData.isNull("mime")) attachment.mime = jsonObjectData.getString("mime")
                            if (jsonObjectData.has("url") && !jsonObjectData.isNull("url")) attachment.url = jsonObjectData.getString("url")
                            if (jsonObjectData.has("thumb_url") && !jsonObjectData.isNull("thumb_url")) attachment.thumbUrl = jsonObjectData.getString("thumb_url")
                            if (jsonObjectData.has("modal_url") && !jsonObjectData.isNull("modal_url")) attachment.modalUrl = jsonObjectData.getString("modal_url")
                            if (jsonObjectData.has("created_at") && !jsonObjectData.isNull("created_at")) attachment.createdAt = jsonObjectData.getString("created_at")
                            attachment.type = AttachmentAdapter.VIEW_TYPE_IMAGE
                            sessionManager!!.userAccount.avatar = attachment
                            ImageUtil.displayImage(imgAvatar, attachment.url, null)
                            if (ProfileFragment.onProfileupdatelistener != null) {
                                ProfileFragment.onProfileupdatelistener!!.updatedSuccesfully(attachment.url)
                            }
                            if (DashboardActivity.onProfileupdatelistenerSideMenu != null) {
                                DashboardActivity.onProfileupdatelistenerSideMenu!!.updatedSuccesfully(attachment.url)
                            }
                        }
                        //   adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // ((ActivityBase) getActivity()).showToast("attachment added", getActivity());
                        btnNext!!.isEnabled = true
                    } else {
                        (activity as ActivityBase?)!!.showToast("Something went wrong", activity)
                    }
                } catch (e: JSONException) {
                    (activity as ActivityBase?)!!.showToast("Something went wrong", activity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                if (activity != null) {
                    (activity as ActivityBase?)!!.hideProgressDialog()
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(): ProfileReqFragment {
            return ProfileReqFragment()
        }
    }
}