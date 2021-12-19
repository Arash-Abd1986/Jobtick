package com.jobtick.android.utils;

import com.jobtick.android.BuildConfig;

public class ConstantKey {
    // public static final String PUBLISHABLE_KEY = "pk_test_51DMC17EdCPjjZ7tOKFdshvLgwrNkjWFc4Q3tJT4QONjr8yy9BQt4xhoE5nxIOF1PUJm8W3MjU8kn9yNCsUCYbTa400kXtgexZK";
    public static final String PUBLISHABLE_KEY = (BuildConfig.DEBUG) ?
            "pk_test_51DMC17EdCPjjZ7tOKFdshvLgwrNkjWFc4Q3tJT4QONjr8yy9BQt4xhoE5nxIOF1PUJm8W3MjU8kn9yNCsUCYbTa400kXtgexZK"
            : "pk_live_TOO68dRFoZh4b8Y5aBNHSWiu";

    public static final String ATTACHMENT = "ATTACHMENT";
    public static final String SLUG = "SLUG";
    public static final String TAB = "TAB";
    public static final String PORTFO_SKILLS = "portfolio_skills";
    public static final String TASK_STATUS = "task_status";
    public static final String TASK = "TASK";
    public static final String POSITION = "POSITION ";
    public static final String CATEGORY = "CATEGORY";
    public static final String CATEGORY_ID = "CATEGORY_ID";
    public static final String TITLE = "TITLE";
    public static final String KEY_TASK_ID = "task_id";
    public static final String KEY_TASK_CANCELLATION_ID = "cancellation_id";

    public static final String CREATE_A_JOB = "Post a job";
    public static final String UPDATE_TASK = "Edit the job";
    public static final String COPY_TASK = "Copy the job";
    public static final String TASK_NAME = "task_name";
    public static final String TASK_BUDGET = "task_budget";
    public static final String RATING = "rating";
    public static final String COMMENT = "comment";
    public static final String AMOUNT = "Amount";
    public static final String DISCOUNT_COUPON = "Discount_coupon";
    public static final String DONE = "Done";
    public static final String DECLINE = "Decline";
    public static final String INCREASE_BUDGET = "Increase_budget";
    public static final String WRITE_REVIEW = "Write_review";
    public static final String KEY_USER_REPORT = "user_report";
    public static final String KEY_TASK_REPORT = "task_report";
    public static final String KEY_COMMENT_REPORT = "comment_report";
    public static final String KEY_QUESTION_REPORT = "question_report";
    public static final String KEY_OFFER_REPORT = "offer_report";

    public static final String USER_ID = "userid";
    public static final String GALLERY_DIRECTORY_NAME = "Jobtick";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";
    public static final String COMPLETES_MESSAGE_TITLE = "TITLE";
    public static final String COMPLETES_SLUG = "SLUG";
    public static final String COMPLETES_MESSAGE_FROM = "FROM";

    public static final String VIDEO_PATH="video_path";
    public static final String COMPLETES_MESSAGE_SUBTITLE = "SUBTITLE";
    public static final String MAKE_AN_OFFER = "Make An Offer";
    public static final String PAYMENT_OVERVIEW = "payment_overview";
    public static final String CANCELLATION = "Cancellation itself";
    public static final String CANCELLATION_SUBMITTED = "Cancellation submitted";
    public static final String CANCELLATION_TITLE = "Cancellation title";
    public static final String PRIVATE_CHAT = "Private_chat";
    public static final String ACCEPT = "Accept";
    public static final String ACCEPTED = "Accepted";

    public static final String OFFER_LIST_MODEL = "OFFER LIST MODEL";
    public static final String SKILLS = "SKILLS";

    public static final String SPECIALITIES = "Specialities";
    public static final String TRANSPORTATION = "Transportation";
    public static final String LANGUAGE = "Language";
    public static final String EXPERIENCE = "Skills";
    public static final String EDUCATION = "Education";
    public static final String CREATE_TASK = "Create Job";
    public static final String DRAFT_JOB = "isDraftJob";


    public static final String TOOLBAR_TITLE = "Toolbar";

    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_IMAGE = "IMAGE";

    public static final String ACTIONBAR_TITLE = "ACTIONBAR_TITLE";
    public static final String TAG_SIZE = "TAG_SIZE";
    public static final String TAG = "TAG";
    public static final String NO_PAYMENT_METHOD = "400";
    public static final String INVALID_CARD_EXCEPTION = "INVALID_CARD_EXCEPTION";
    public static final String PHYSICAL = "physical";

    public static final String MAKE_AN_OFFER_MODEL = "Make_an_offer_model";


    public static final String BTN_OFFER_PENDING = "Offer pending";
    public static final String BTN_MAKE_AN_OFFER = "Make an offer";
    public static final String BTN_COMPLETED = "Completed";
    public static final String BTN_ASK_TO_RELEASE_MONEY = "Ask for Payment";
    public static final String BTN_WAIT_TO_RELEASE_MONEY = "Waiting for poster to release money";

    public static final String BTN_POST_NEW_JOB = "Post a new job";
    public static final String BTN_LEAVE_A_REVIEW = "Leave a review";
    public static final String BTN_INCREASE_BUDGET_REQUEST_SENT = "View increase price request";
    public static final String BTN_CONFIRM_RELEASE_MONEY= "View release money request";
    public static final String BTN_RESCHEDULE_REQUEST_SENT = "View reschedule time request";
    public static final String BTN_CANCELLATION_REQUEST_SENT = "CANCELLATION REQUEST SENT";
    public static final String BTN_VIEW_CANCELLATION_REQUEST = "View cancellation request";
    public static final String BTN_CANCELLATION_REQUEST_RECEIVED = "CANCELLATION REQUEST RECEIVED";
    public static final String BTN_RELEASE_MONEY = "RELEASE MONEY";
    public static final String BTN_WRITE_A_REVIEW = "WRITE A REVIEW";
    public static final String BTN_ASSIGNED = "ASSIGNED";
    public static final String CONVERSATION = "CONVERSATION";
    public static final String QUESTION_LIST_MODEL = "QUESTION LIST MODEL";


    public static final String CANCELLATION_PENDING = "pending";
    public static final String CANCELLATION_DECLINED = "declined";
    public static final String CANCELLATION_ACCEPTED = "accepted";
    public static final String IS_MY_TASK = "is_my_task";
    public static final String DISCOUNT_AMOUNT = "Discount_amount";


    public static final int RESULTCODE_COUPON = 1;
    public static final int RESULTCODE_MAKEANOFFER = 2;
    public static final int RESULTCODE_PAYMENTOVERVIEW = 3;
    public static final int RESULTCODE_CANCELLATION = 4;
    public static final int RESULTCODE_PRIVATE_CHAT = 5;
    public static final int RESULTCODE_CANCELLATION_ACCEPT = 6;
    public static final int RESULTCODE_CANCELLATION_DECLINE = 7;
    public static final int RESULTCODE_CANCELLATION_DONE = 8;
    public static final int RESULTCODE_INCREASE_BUDGET = 9;
    public static final int RESULTCODE_RESCHEDULE = 15;
    public static final int RESULTCODE_WRITE_REVIEW = 10;
    public static final int RESULTCODE_CATEGORY = 11;
    public static final int RESULTCODE_ATTACHMENT = 12;
    public static final int RESULTCODE_UPDATE_TASK = 13;
    public static final int RESULTCODE_CREATE_TASK=14;
    public static final int RESULTCODE_NOTIFICATION_READ = 101;

    public static final String offerId = "offerid";
    public static final String questionId = "questionId";
    public static final String commentId = "commentId";
    public static final String PUSH_NOTIFICATION_MODEL = "pushnotificaiton_model";

    public static final String PUSH_TRIGGER = "trigger";
    public static final String PUSH_USER_ID = "user_id";
    public static final String PUSH_TASK_ID = "task_id";
    public static final String PUSH_TASK_SLUG = "task_slug";
    public static final String TASK_SLUG = "task_slug";
    public static final String PUSH_TASK_STATUS = "task_status";

    public static final String PUSH_OFFER = "offer";
    public static final String PUSH_QUESTION = "question";
    public static final String PUSH_CONVERSATION = "conversation";
    public static final String ID = "id";
    public static final String PUSH_STATUS = "task_status";
    public static final String PUSH_TITLE = "title";

    public static final String PUSH_OFFER_ID = "offer_id";
    public static final String PUSH_QUESTION_ID = "question_id";
    public static final String PUSH_CONVERSATION_ID = "conversation_id";


    public static final String PUSH_TASK = "task";
    public static final String PUSH_COMMENT = "comment";
    public static final String FROM_MY_JOBS_WITH_LOVE = "from_my_jobs";
    public static final String ALL_MY_JOBS_URL_FILTER = "?mytask=draft,open,assigned,offered,completed,closed,cancelled";
    public static final String COPY = "copy";
    public static final String EDIT = "edit";
    public static final int RESULTCODE_MY_JOBS = 2020;
    public static boolean IS_CHAT_SCREEN = false;

    public static final String GO_TO_MY_JOBS = "go to my jobs";
    public static final String GO_TO_HOME = "go to home";
    public static final String GO_TO_EXPLORE = "go to explore";
    public static final String GO_TO_CHAT = "go to chat";
    public static final String GO_TO_PROFILE = "go to profile";


    //  public static ArrayList<Integer> integerArrayList = new ArrayList<>();
}
