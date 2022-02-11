package com.jobtick.android.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

enum class EventTitles(val key: String) {
    N_CLICK_PJ_DETAILS_NEXT("n_click_pj_details_next"),
    N_PAGE_VIEW_PJ_DETAILS("n_page_view_pj_details"),
    N_PAGE_VIEW_PJ_COMPLETE("n_page_view_pj_complete"),
    N_PAGE_VIEW_PJ_BUDGET("n_page_view_pj_budget"),
    N_PAGE_VIEW_PJ_DATE("n_page_view_pj_date"),
    N_API_TEMP_ATTACHMENT("n_api_temp_attachment"),
    N_API_CREATE_TASK("n_api_create_tasks"),
    N_CLICK_PJ_DATE_NEXT("n_click_pj_date_next"),
    N_CLICK_PJ_BUDGET_NEXT("n_click_pj_budget_next"),
    N_CLICK_PJ_VIEW_YOUR_JOB("n_click_pj_complete_view_your_job"),
    N_CLICK_PJ_POST_NEW_JOB("n_click_pj_complete_new_job")

}

private lateinit var firebaseAnalytics: FirebaseAnalytics
fun pushEvent(eventName: String, bundle: Bundle) {
    firebaseAnalytics = Firebase.analytics
    firebaseAnalytics.logEvent(eventName, bundle)
}

fun eventCleaner(string: String): String {
    return if (string.length > 99) {
        string.substring(0, 98)
    } else
        string
}