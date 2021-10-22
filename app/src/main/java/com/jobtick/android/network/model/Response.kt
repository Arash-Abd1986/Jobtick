package com.jobtick.android.network.model

import com.jobtick.android.network.model.response.Links
import com.jobtick.android.network.model.response.Meta

data class Response(
        val `data`: Any,
        val links: Links,
        val message: String,
        val meta: Meta,
        val success: Boolean
)