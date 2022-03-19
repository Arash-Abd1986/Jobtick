package com.jobtick.android.utils;

import android.content.Context;

import com.jobtick.android.BuildConfig;


public class Constant {
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


    public static final String TASK_CANCELLED = "cancelled";
    public static final String TASK_DRAFT = "draft";
    public static final String TASK_OPEN = "open";
    public static final String TASK_PENDING = "pending";
    public static final String TASK_ASSIGNED = "assigned";
    public static final String TASK_COMPLETED = "completed";
    public static final String TASK_OVERDUE = "overdue";
    public static final String TASK_COMPLETE = "complete";
    public static final String TASK_CLOSED = "closed";

    public static final String TASK_DRAFT_CASE_ALL_JOB_KEY = "All Jobs";
    public static final String TASK_DRAFT_CASE_ALL_JOB_VALUE = "open,draft,assigned,offered,completed,close,cancelled";
    public static final String TASK_ASSIGNED_CASE_RELATED_JOB_VALUE = "assigned";

    public static final String TASK_ASSIGNED_CASE_UPPER_FIRST = "Assigned";



    public static final String URL_privacy_policy = "https://www.jobtick.com/privacy";
    public static final String URL_support = "https://www.jobtick.com/support";
    public static final String URL_terms = "https://www.jobtick.com/terms";
    public static final String URL_how_it_works_poster = "https://www.jobtick.com/how-it-works/poster";
    public static final String URL_how_it_works_ticker = "https://www.jobtick.com/how-it-works/ticker";
    public static final String URL_google_play = "https://play.google.com/store/apps/details?id=com.jobtick.android";

    // base url
    public static final String BASE_URL_PRODUCT = "https://api.jobtick.com/api/v1/";
    public static final String BASE_URL_DEBUG = "https://api-dev.jobtick.com/api/v1/";
    public static final String BASE_URL_PRODUCT_V2 = "https://api.jobtick.com/api/v2/";
    public static final String BASE_URL_DEBUG_V2 = "https://api-dev.jobtick.com/api/v2/";
    public static final String SOCKET_DEV = "https://api-dev.jobtick.com:2096";
    public static final String SOCKET_PROD = "https://api.jobtick.com:2096";
    public static final String BASE_URL_SERVER = (BuildConfig.DEBUG) ? SOCKET_DEV : SOCKET_PROD;
    public static final String BASE_URL = (BuildConfig.DEBUG) ? BASE_URL_DEBUG : BASE_URL_PRODUCT;
    public static final String BASE_URL_v2 = (BuildConfig.DEBUG) ? BASE_URL_DEBUG_V2 : BASE_URL_PRODUCT_V2;
    public static final String MAP_BOX_BASE_URL = "https://api.mapbox.com/geocoding/v5/mapbox.places/";
    public static final String PROD = "";
    public static final String DEV = "dev.";
    public static final String SHARE_APPEND_TXT = (BuildConfig.DEBUG) ? DEV : PROD;

    public static final String URL_CHANGE_PASSWORD = BASE_URL + "account/change-password";
    public static final String URL_TASKS = BASE_URL + "tasks";
    public static final String URL_TASKS_v2 = BASE_URL_v2 + "jobs";
    public static final String URL_MY_JOBS = BASE_URL_v2 + "my-jobs";
    public static final String URL_BLOCK_CHAT = BASE_URL + "chat/blocking";
    public static final String URL_BOOKMARK = BASE_URL + "bookmarks";

    public static final String URL_NOTIFICATION_LIST = BASE_URL + "notifications";
    public static final String URL_NOTIFICATION_UNREAD = BASE_URL + "notifications/unread-count";
    public static final String HOME = BASE_URL_v2 + "home";
    public static final String URL_NOTIFICATION_MARK_ALL_READ = BASE_URL + "notifications/mark-all-read";

    public static final String TASK_PAYMENT_TYPE_HOURLY = "hourly";
    public static final String TASK_CATEGORY = "category";
    public static final String TASK_CATEGORY_V2 =  BASE_URL_v2 + "category";
    public static final String CHECK_UPDATE = "init-info";
    public static final String ADD_BILLING = "account/billingaddress";
    public static final String ADD_ACCOUNT_DETAILS = "account/bankaccount";

    public static final String URL_USER_PROFILE_INFO = BASE_URL + "profile/info";
    public static final String PROFILE_INFO = "profile/info";
    public static final String URL_OFFERS = BASE_URL + "offers";
    public static final String URL_PROFILE = BASE_URL + "profile";
    public static final int MAX_LINE_TEXTVIEW_MORE_4 = 4;
    public static final String URL_PAYMENTS_METHOD = BASE_URL + "payments/method";
    public static final String URL_DELETE_BANK_ACCOUNT = BASE_URL + "account/bankaccount";
    public static final String URL_DELETE_BILLING_ADDRESS = BASE_URL + "account/billingaddress";
    public static final String URL_CHAT = BASE_URL + "chat";
    public static final String URL_QUESTIONS = BASE_URL + "questions";
    public static final String URL_CANCELLATION = BASE_URL + "cancellation";
    public static final String URL_TASK_ALERT_V2 = BASE_URL_v2 + "jobalerts";
    public static final String URI = "URI";
    public static final String EDIT_MODE = "edit_mode";

    public static int PAGE_SIZE = 10;


    // resister
    public static String URL_SIGNUP = BASE_URL + "signup";

    // resend-otp
    public static String URL_RESEND_OTP = BASE_URL + "resend-otp";
    public static String URL_NEW_RESEND_OTP = BASE_URL + "email-verification/resent-otp";
    public static final String URL_GET_ACCOUNT = BASE_URL + "account";
    public static final String URL_GET_PAYMENT_HISTORY_FILTER = BASE_URL + "payments/list";

    public static final String URL_CREATE_RESCHEDULE = "reschedule";

    //Budget Increment
    public static String URL_BUDGET_Increment = "/additionalfund/request";
    public static String AS_A_WORKER = "worker";

    public static String URL_REMOVE_AVTAR = "/avatar";

    // reset password
    public static String URL_RESET_PASSWORD = BASE_URL + "reset-password";
    public static final String URL_RESET_PASSWORD_VERIFY_OTP = BASE_URL + "reset-password/otp-verify";
    private final Context context;

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
    public static String URL_OFFERS_PAYING_CALCULATION = BASE_URL + "offers/paying_calculation";
    public static String URL_CHECK_COUPON = BASE_URL + "coupons/check-coupon";


    public static String CASH_OUT = "cash out";

    Constant(Context context) {
        this.context = context;
    }

    public static String MAPBOX_API_KEY = BuildConfig.Mapbox_Access_key;
    public static final String URL_VIDEO_GUIDELINE = "https://jobtick-ap.s3-ap-southeast-2.amazonaws.com/videos/video-offer-guidlines.mp4";

    public static final String userID = "userID";
    public static final String Level = "Level";
    public static final String userAccount = "userAccount";

    public static final int MAX_RESCHEDULE_DAY = 14;
    public static final int MIN_AGE_FOR_USE_APP = 13;
    public static final int MAX_FILTER_DISTANCE_IN_KILOMETERS = 4000;

}
