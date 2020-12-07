package com.jobtick.utils;

import android.content.Context;

import com.jobtick.BuildConfig;


public class Constant {
    public static final String FILTER_SECTION = "section";
    public static final String FILTER_LOCATION = "location";
    public static final String FILTER_DISTANCE = "distance";
    public static final String FILTER_LATITUDE = "latitude";
    public static final String FILTER_LOGITUDE = "logitude";
    public static final String FILTER_PRICE = "price";
    public static final String FILTER_TASK_OPEN = "OPEN TASKS";
    public static final String FILTER_ALL = "REMOTE & IN PERSON";
    public static final String FILTER_ALL_QUERY = "all";
    public static final String FILTER_REMOTE = "REMOTE";
    public static final String FILTER_REMOTE_QUERY = "remote";
    public static final String FILTER_IN_PERSON = "IN PERSON";
    public static final String FILTER_IN_PERSON_QUERY = "physical";
    public static final String FILTER = "Filter";
    public static final String STATE_STRIPE_TICKER = "STATE_STRIPE_TICKER";
    public static final String STATE_STRIPE_POSTER = "STATE_STRIPE_POSTER";


    public static final String TASK_SEARCH_QUERY = "search_query";
    public static final String TASK_CANCELLED = "cancelled";
    public static final String TASK_DRAFT = "draft";
    public static final String TASK_OPEN = "open";
    public static final String TASK_PENDING = "pending";
    public static final String TASK_ASSIGNED = "assigned";
    public static final String TASK_COMPLETED = "completed";
    public static final String TASK_OVERDUE = "overdue";
    public static final String TASK_COMPLETE = "complete";
    public static final String TASK_CLOSED = "closed";

    public static final String TASK_CANCELLED_CASE_UPPER_FIRST = "Cancelled";
    public static final String TASK_DRAFT_CASE_ALL_JOB_KEY = "All Jobs";
    public static final String TASK_DRAFT_CASE_ALL_JOB_VALUE = "open,draft,assigned,offered,completed,close,cancelled";

    public static final String TASK_DRAFT_CASE_UPPER_FIRST = "Draft";
    public static final String TASK_OPEN_CASE_UPPER_FIRST = "Open";
    public static final String TASK_PENDING_CASE_UPPER_FIRST = "Pending";
    public static final String TASK_ASSIGNED_CASE_UPPER_FIRST = "Assigned";
    public static final String TASK_COMPLETED_CASE_UPPER_FIRST = "Completed";
    public static final String TASK_OFFERED_CASE_UPPER_FIRST = "Offered";
    public static final String TASK_CLOSED_CASE_UPPER_FIRST = "Closed";


    public static final String URL_privacy_policy = "https://dev.jobtick.com/privacy-policy";

    // base url
    public static final String BASE_URL = "https://dev.jobtick.com/api/v1/";
    public static final String MAIN_URL = "https://dev.jobtick.com/";
    public static final String URL_CHANGE_PASSWORD = BASE_URL + "account/change-password";
    public static final String URL_TASKS = BASE_URL + "tasks";
    public static final String URL_BOOKMARK = BASE_URL + "bookmarks";

    public static final String URL_NOTIFICATION_LIST = BASE_URL + "notifications";

    public static final String TASK_PAYMENT_TYPE_FIXED = "fixed";
    public static final String TASK_PAYMENT_TYPE_HOURLY = "hourly";
    public static final String TASK_MORNING = "morning";
    public static final String TASK_MIDDAY = "midday";
    public static final String TASK_AFTERNOON = "afternoon";
    public static final String TASK_EVENING = "evening";
    public static final String TASK_CATEGORY = "category";
    public static final String ADD_BILLING = "account/billingaddress";
    public static final String ADD_ACCOUNT_DETAILS = "account/bankaccount";

    public static final String URL_USER_PROFILE_INFO = BASE_URL + "profile/info";
    public static final String PROFILE_INFO = "profile/info";
    public static final String URL_OFFERS = BASE_URL + "offers";
    public static final String URL_PROFILE = BASE_URL + "profile";
    public static final int MAX_LINE_TEXTVIEW_MORE_4 = 4;
    public static final int MAX_LINE_TEXTVIEW_MORE_2 = 2;
    public static final String URL_PAYMENTS_METHOD = BASE_URL + "payments/method";
    public static final String URL_DELETE_BANK_ACCOUNT = BASE_URL + "account/bankaccount";
    public static final String URL_DELETE_BILLING_ADDRESS = BASE_URL + "account/billingaddress";
    public static final String URL_CHAT = BASE_URL + "chat";
    public static final String URL_QUESTIONS = BASE_URL + "questions";
    public static final String URL_CANCELLATION = BASE_URL + "cancellation";
    public static final String URL_COUPONS = BASE_URL + "coupons";
    public static final String URL_TASK_ALERT = BASE_URL + "taskalerts";

    public static int PAGE_SIZE = 10;


    // resister
    public static String URL_SIGNUP = BASE_URL + "signup";
    public static final String MEDIA_TEMP_ATTACHMENT = BASE_URL + "media/temp-attachment";
    public static final String URL_JOB_DELETE = BASE_URL + "tasks";
    public static final String URL_TASKS_CREATE = BASE_URL + "tasks/create";
    // resend-otp
    public static String URL_RESEND_OTP = BASE_URL + "resend-otp";
    public static String URL_NEW_RESEND_OTP = BASE_URL + "email-verification/resent-otp";
    public static final String URL_DELETE_TASK_MEDIA = BASE_URL + "tasks";
    public static final String URL_UPDATE_TASK = BASE_URL + "tasks";
    public static final String URL_LIST_USER_TASK = BASE_URL + "tasks";
    public static final String URL_GET_SINGLE_TASK = BASE_URL + "tasks";
    public static final String URL_GET_ACCOUNT = BASE_URL + "account";
    public static final String URL_GET_PAYMENT_HISTORY_POSTER = BASE_URL + "payments/list?poster_payment_filter[]";
    public static final String URL_GET_PAYMENT_HISTORY_WORKER = BASE_URL + "payments/list?worker_payment_filter[]";

    public static final String URL_CREATE_RESCHEDULE = "reschedule";

    //Budget Increment
    public static String URL_BUDGET_Increment = "/additionalfund/request";
    public static String AS_A_POSTER = "poster";
    public static String AS_A_WORKER = "worker";

    public static String URL_REMOVE_AVTAR = "/avatar";

    // reset password
    public static String URL_RESET_PASSWORD = BASE_URL + "reset-password";
    public static final String URL_VERIFY_OTP = BASE_URL + "verify-otp";
    public static final String URL_RESET_PASSWORD_VERIFY_OTP = BASE_URL + "reset-password/otp-verify";
    private Context context;

    // login
    public static String URL_SIGNIN = BASE_URL + "signin";

    // login
    public static String URL_SIGNIN_GOOGLE = BASE_URL + "google/signin";

    // login
    public static String URL_SIGNIN_FACEBOOK = BASE_URL + "facebook/signin";

    //email verification
    public static String URL_EMAIL_VERIFICATION = BASE_URL + "email-verification";


    // Forgot Password
    public static String URL_FORGOT_PASSWORD = BASE_URL + "reset-password/verify";
    public static String URL_ADDITIONAL_FUND = "additionalfund";


    //Mobile verification
    public static String URL_SEND_OTP = BASE_URL + "account/phone_send_otp";
    public static String URL_OTP_VERIFICATION = BASE_URL + "account/phone_verification";


    public static String URL_OFFERS_EARNING_CALCULATION = BASE_URL + "offers/earning_calculation";


    public static String CASH_OUT = "cash out";

    Constant(Context context) {
        this.context = context;
    }

    /*
     *  END ALL URLS
     */

    /*
     * ALL KEYS
     *
     */
    //headers
    public static String MAPBOX_API_KEY = BuildConfig.Mapbox_Access_key;
    public static final String KEY_ACCEPT = "Accept";
    public static final String KEY_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String KEY_AUTHORIZATION = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhbGciOiJIUzI" +
            "1NiIsImlkIjoxMzYsInN1YiI6MTM2LCJpYXQiOjE1MzIzNTA5MzIsImV4cCI6MTUzMjQzNzMzMn0." +
            "fJfR-0PNi-nSb_szulqE_zu0k7BHdgY16MIVFG9f6P8";

    public static final String userID = "userID";
    public static final String userAccount = "userAccount";
}
