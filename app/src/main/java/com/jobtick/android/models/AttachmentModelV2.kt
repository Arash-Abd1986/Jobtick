package com.jobtick.android.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jobtick.android.adapers.AttachmentAdapter
import java.io.File

data class AttachmentModelV2(

        @SerializedName("id")
        @Expose
        var id: Int = -3,

        @SerializedName("name")
        @Expose
        var name: String? = null,

        @SerializedName("file_name")
        @Expose
        var fileName: String? = null,

        @SerializedName("mime")
        @Expose
        var mime: String? = null,

        @SerializedName("url")
        @Expose
        var url: String? = null,

        @SerializedName("thumb_url")
        @Expose
        var thumbUrl: String? = null,

        @SerializedName("modal_url")
        @Expose
        var modalUrl: String? = null,

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null,
        var drawable: Int = 0,
        var type: Int? = AttachmentAdapter.VIEW_TYPE_IMAGE,
        var file: File? = null,
        var isCheckedEnable: Boolean = false,
        var isChecked: Boolean = false,

        )