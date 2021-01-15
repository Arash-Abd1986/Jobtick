package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.android.utils.TimeAgo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

public class UserAccountModel implements Parcelable {
    String TAG = UserAccountModel.class.getName();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("lname")
    @Expose
    private String lname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("email_verified_at")
    @Expose
    private String emailVerifiedAt;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("mobile_verified_at")
    @Expose
    private String mobileVerifiedAt;
    @SerializedName("tagline")
    @Expose
    private String tagline;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("business_number")
    @Expose
    private String business_number;
    @SerializedName("is_online")
    @Expose
    private boolean isOnline = false;


    @SerializedName("is_verified_account")
    @Expose
    private Integer isVerifiedAccount = 0;


    @SerializedName("avatar")
    @Expose
    private AttachmentModel avatar;
    @SerializedName("poster_ratings")
    @Expose
    private RatingModel posterRatings;
    @SerializedName("worker_ratings")
    @Expose
    private RatingModel workerRatings;
    @SerializedName("posted_task_statistics")
    @Expose
    private PostedTaskStatistics postTaskStatistics;
    @SerializedName("work_task_statistics")
    @Expose
    private WorkTaskStatistics workTaskStatistics;


    @SerializedName("worker_tier")
    @Expose
    private TierModel workerTier;
    @SerializedName("poster_tier")
    @Expose
    private TierModel posterTier;

/*    @SerializedName("worker_rank")
    @Expose
    private Integer workerRank;

    @SerializedName("poster_rank")
    @Expose
    private Integer posterRank;*/

    @SerializedName("join_date")
    @Expose
    private String joinDate;
    @SerializedName("last_online")
    @Expose
    private String lastOnline;
    @SerializedName("skills")
    @Expose
    private SkillsModel skills;
    @SerializedName("portfolio")
    @Expose
    private ArrayList<AttachmentModel> portfolio = new ArrayList<>();
    @SerializedName("badges")
    @Expose
    private ArrayList<BadgesModel> badges = new ArrayList<>();

    /*  @SerializedName("worker_level")
      @Expose
      private int worker_level;

      @SerializedName("poster_level")
      @Expose
      private int poster_level;
  */
    @SerializedName("account_status")
    @Expose
    private AccountStatusModel account_status;

    @SerializedName("blocked")
    @Expose
    private Boolean blocked;

    @SerializedName("blocked_until")
    @Expose
    private String blockedUntil;

    /**
     * No args constructor for use in serialization
     */
    public UserAccountModel() {
    }

    public UserAccountModel(Integer id, String name, String fname, String location, Double latitude, Double longitude,
                            String lname, String email, String emailVerifiedAt, String mobile, String mobileVerifiedAt,
                            String tagline, String about, String dob, String business_number, boolean isOnline,
                            AttachmentModel avatar, RatingModel posterRatings, RatingModel workerRatings, PostedTaskStatistics postTaskStatistics,
                            WorkTaskStatistics workTaskStatistics, TierModel workerTier, TierModel posterTier, Integer workerRank,
                            Integer posterRank, String joinDate, String lastOnline, SkillsModel skills, ArrayList<AttachmentModel> portfolio,
                            ArrayList<BadgesModel> badges, AccountStatusModel account_status, Integer isVerifiedAccount, Boolean blocked, String blockedUntil) {
        this.id = id;
        this.name = name;
        this.fname = fname;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lname = lname;
        this.email = email;
        this.emailVerifiedAt = emailVerifiedAt;
        this.mobile = mobile;
        this.mobileVerifiedAt = mobileVerifiedAt;
        this.tagline = tagline;
        this.about = about;
        this.dob = dob;
        this.business_number = business_number;
        this.isOnline = isOnline;
        this.avatar = avatar;
        this.posterRatings = posterRatings;
        this.workerRatings = workerRatings;
        this.postTaskStatistics = postTaskStatistics;
        this.workTaskStatistics = workTaskStatistics;
        this.workerTier = workerTier;
        this.posterTier = posterTier;
        //      this.workerRank = workerRank;
        //      this.posterRank = posterRank;
        this.joinDate = joinDate;
        this.lastOnline = lastOnline;
        this.skills = skills;
        this.portfolio = portfolio;
        this.badges = badges;
        this.account_status = account_status;
        this.isVerifiedAccount = isVerifiedAccount;
        this.blocked = blocked;
        this.blockedUntil = blockedUntil;
    }


    protected UserAccountModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        fname = in.readString();
        location = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        lname = in.readString();
        email = in.readString();
        emailVerifiedAt = in.readString();
        mobile = in.readString();
        mobileVerifiedAt = in.readString();
        tagline = in.readString();
        about = in.readString();
        dob = in.readString();
        business_number = in.readString();
        isOnline = in.readByte() != 0;
        isVerifiedAccount = in.readInt();
        avatar = in.readParcelable(AttachmentModel.class.getClassLoader());
        posterRatings = in.readParcelable(RatingModel.class.getClassLoader());
        workerRatings = in.readParcelable(RatingModel.class.getClassLoader());
        postTaskStatistics = in.readParcelable(StatisticsModel.class.getClassLoader());
        workTaskStatistics = in.readParcelable(StatisticsModel.class.getClassLoader());
        workerTier = in.readParcelable(TierModel.class.getClassLoader());
        posterTier = in.readParcelable(TierModel.class.getClassLoader());
/*        if (in.readByte() == 0) {
            workerRank = null;
        } else {
            workerRank = in.readInt();
        }
        if (in.readByte() == 0) {
            posterRank = null;
        } else {
            posterRank = in.readInt();
        }*/
        joinDate = in.readString();
        lastOnline = in.readString();
        skills = in.readParcelable(SkillsModel.class.getClassLoader());
        portfolio = in.createTypedArrayList(AttachmentModel.CREATOR);
        badges = in.createTypedArrayList(BadgesModel.CREATOR);
/*        poster_level = in.readInt();
        worker_level = in.readInt();*/
        account_status = in.readParcelable(AccountStatusModel.class.getClassLoader());
        blocked = in.readByte() != 0;     //myBoolean == true if byte != 0
        blockedUntil = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(fname);
        dest.writeString(location);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        dest.writeString(lname);
        dest.writeString(email);
        dest.writeString(emailVerifiedAt);
        dest.writeString(mobile);
        dest.writeString(mobileVerifiedAt);
        dest.writeString(tagline);
        dest.writeString(about);
        dest.writeString(dob);
        dest.writeString(business_number);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeInt(isVerifiedAccount);
        dest.writeParcelable(avatar, flags);
        dest.writeParcelable(posterRatings, flags);
        dest.writeParcelable(workerRatings, flags);
        dest.writeParcelable(postTaskStatistics, flags);
        dest.writeParcelable(workTaskStatistics, flags);
        dest.writeParcelable(workerTier, flags);
        dest.writeParcelable(posterTier, flags);
        /*if (workerRank == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(workerRank);
        }
        if (posterRank == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(posterRank);
        }*/
        dest.writeString(joinDate);
        dest.writeString(lastOnline);
        dest.writeParcelable(skills, flags);
        dest.writeTypedList(portfolio);
        dest.writeTypedList(badges);

/*        dest.writeInt(poster_level);
        dest.writeInt(worker_level);*/
        dest.writeParcelable(account_status, flags);
        if (blocked != null) {
            dest.writeByte((byte) (blocked ? 1 : 0));     //if myBoolean == true, byte == 1

        } else {

            dest.writeByte((byte) (0));     //if myBoolean == true, byte == 1

        }


        dest.writeString(blockedUntil);
    }

    public AccountStatusModel getAccount_status() {
        return account_status;
    }

    public void setAccount_status(AccountStatusModel account_status) {
        this.account_status = account_status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserAccountModel> CREATOR = new Creator<UserAccountModel>() {
        @Override
        public UserAccountModel createFromParcel(Parcel in) {
            return new UserAccountModel(in);
        }

        @Override
        public UserAccountModel[] newArray(int size) {
            return new UserAccountModel[size];
        }
    };

    public int getIsVerifiedAccount() {
        return isVerifiedAccount;
    }

    public void setIsVerifiedAccount(int isVerifiedAccount) {
        this.isVerifiedAccount = isVerifiedAccount;
    }

    public TierModel getWorkerTier() {
        return workerTier;
    }

    public void setWorkerTier(TierModel workerTier) {
        this.workerTier = workerTier;
    }

    public TierModel getPosterTier() {
        return posterTier;
    }

    public void setPosterTier(TierModel posterTier) {
        this.posterTier = posterTier;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobileVerifiedAt() {
        return mobileVerifiedAt;
    }

    public void setMobileVerifiedAt(String mobileVerifiedAt) {
        this.mobileVerifiedAt = mobileVerifiedAt;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBusiness_number() {
        return business_number;
    }

    public void setBusiness_number(String business_number) {
        this.business_number = business_number;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public AttachmentModel getAvatar() {
        return avatar;
    }

    public void setAvatar(AttachmentModel avatar) {
        this.avatar = avatar;
    }

    public RatingModel getPosterRatings() {
        return posterRatings;
    }

    public void setPosterRatings(RatingModel posterRatings) {
        this.posterRatings = posterRatings;
    }

    public RatingModel getWorkerRatings() {
        return workerRatings;
    }

    public void setWorkerRatings(RatingModel workerRatings) {
        this.workerRatings = workerRatings;
    }

    public PostedTaskStatistics getPostTaskStatistics() {
        return postTaskStatistics;
    }

    public void setPostTaskStatistics(PostedTaskStatistics postTaskStatistics) {
        this.postTaskStatistics = postTaskStatistics;
    }

    public WorkTaskStatistics getWorkTaskStatistics() {
        return workTaskStatistics;
    }

    public void setWorkTaskStatistics(WorkTaskStatistics workTaskStatistics) {
        this.workTaskStatistics = workTaskStatistics;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void setIsVerifiedAccount(Integer isVerifiedAccount) {
        this.isVerifiedAccount = isVerifiedAccount;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public String getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(String blockedUntil) {
        this.blockedUntil = blockedUntil;
    }


/*    public Integer getWorkerRank() {
        return workerRank;
    }

    public void setWorkerRank(Integer workerRank) {
        this.workerRank = workerRank;
    }

    public Integer getPosterRank() {
        return posterRank;
    }

    public void setPosterRank(Integer posterRank) {
        this.posterRank = posterRank;
    }*/

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public SkillsModel getSkills() {
        return skills;
    }

    public void setSkills(SkillsModel skills) {
        this.skills = skills;
    }

    public ArrayList<AttachmentModel> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(ArrayList<AttachmentModel> portfolio) {
        this.portfolio = portfolio;
    }


    public ArrayList<BadgesModel> getBadges() {
        return badges;
    }

    public void setBadges(ArrayList<BadgesModel> badges) {
        this.badges = badges;
    }

/*    public int getWorker_level() {
        return worker_level;
    }

    public void setWorker_level(int worker_level) {
        this.worker_level = worker_level;
    }

    public int getPoster_level() {
        return poster_level;
    }

    public void setPoster_level(int poster_level) {
        this.poster_level = poster_level;
    }*/

    public UserAccountModel getJsonToModel(JSONObject jsonObject) {
        UserAccountModel userAccountModel = new UserAccountModel();
        try {
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                userAccountModel.setId(jsonObject.getInt("id"));
            if (jsonObject.has("name") && !jsonObject.isNull("name"))
                userAccountModel.setName(jsonObject.getString("name"));
            if (jsonObject.has("fname") && !jsonObject.isNull("fname"))
                userAccountModel.setFname(jsonObject.getString("fname"));
            if (jsonObject.has("location") && !jsonObject.isNull("location"))
                userAccountModel.setLocation(jsonObject.getString("location"));
            if (jsonObject.has("position") && !jsonObject.isNull("position")) {
                JSONObject jsonObject_position = jsonObject.getJSONObject("position");
                if (jsonObject_position.has("latitude") && !jsonObject_position.isNull("latitude"))
                    userAccountModel.setLatitude(jsonObject_position.getDouble("latitude"));
                if (jsonObject_position.has("longitude") && !jsonObject_position.isNull("longitude"))
                    userAccountModel.setLongitude(jsonObject_position.getDouble("longitude"));
            }
            if (jsonObject.has("lname") && !jsonObject.isNull("lname"))
                userAccountModel.setLname(jsonObject.getString("lname"));
            if (jsonObject.has("email") && !jsonObject.isNull("email"))
                userAccountModel.setEmail(jsonObject.getString("email"));
            if (jsonObject.has("email_verified_at") && !jsonObject.isNull("email_verified_at"))
                userAccountModel.setEmailVerifiedAt(jsonObject.getString("email_verified_at"));
            if (jsonObject.has("mobile") && !jsonObject.isNull("mobile"))
                userAccountModel.setMobile(jsonObject.getString("mobile"));
            if (jsonObject.has("mobile_verified_at") && !jsonObject.isNull("mobile_verified_at"))
                userAccountModel.setMobileVerifiedAt(jsonObject.getString("mobile_verified_at"));
            if (jsonObject.has("tagline") && !jsonObject.isNull("tagline"))
                userAccountModel.setTagline(jsonObject.getString("tagline"));
            if (jsonObject.has("about") && !jsonObject.isNull("about"))
                userAccountModel.setAbout(jsonObject.getString("about"));
            if (jsonObject.has("dob") && !jsonObject.isNull("dob"))
                userAccountModel.setDob(jsonObject.getString("dob"));
            if (jsonObject.has("business_number") && !jsonObject.isNull("business_number"))
                userAccountModel.setBusiness_number(jsonObject.getString("business_number"));
            /*{
                for(Integer integer : ConstantKey.integerArrayList){
                    if(integer == userAccountModel.getId()){
                        userAccountModel.setIsOnline(true);
                    }
                }
            }*/
            if (jsonObject.has("is_online") && !jsonObject.isNull("is_online"))
                userAccountModel.setIsOnline(jsonObject.getBoolean("is_online"));


            if (jsonObject.has("is_verified_account") && !jsonObject.isNull("is_verified_account"))
                userAccountModel.setIsVerifiedAccount(jsonObject.getInt("is_verified_account"));

            if (jsonObject.has("avatar") && !jsonObject.isNull("avatar")) {
                userAccountModel.setAvatar(new AttachmentModel().getJsonToModel(jsonObject.getJSONObject("avatar")));
            }
            if (jsonObject.has("poster_ratings") && !jsonObject.isNull("poster_ratings")) {
                JSONObject jsonObject_poster_ratings = jsonObject.getJSONObject("poster_ratings");
                userAccountModel.setPosterRatings(new RatingModel().getJsonToModel(jsonObject_poster_ratings));
            }
            if (jsonObject.has("worker_ratings") && !jsonObject.isNull("worker_ratings")) {
                JSONObject jsonObject_worker_ratings = jsonObject.getJSONObject("worker_ratings");
                userAccountModel.setWorkerRatings(new RatingModel().getJsonToModel(jsonObject_worker_ratings));
            }
            if (jsonObject.has("posted_task_statistics") && !jsonObject.isNull("posted_task_statistics")) {
                JSONObject jsonObject_post_task_statistics = jsonObject.getJSONObject("posted_task_statistics");
                userAccountModel.setPostTaskStatistics(new PostedTaskStatistics().getJsonToModel(jsonObject_post_task_statistics));
            }
            if (jsonObject.has("work_task_statistics") && !jsonObject.isNull("work_task_statistics")) {
                JSONObject jsonObject_work_task_statistics = jsonObject.getJSONObject("work_task_statistics");
                userAccountModel.setWorkTaskStatistics(new WorkTaskStatistics().getJsonToModel(jsonObject_work_task_statistics));
            }
            if (jsonObject.has("worker_tier") && !jsonObject.isNull("worker_tier")) {
                JSONObject jsonObject_worker_tier = jsonObject.getJSONObject("worker_tier");
                userAccountModel.setWorkerTier(new TierModel().getJsonToModel(jsonObject_worker_tier));
            }
            if (jsonObject.has("poster_tier") && !jsonObject.isNull("poster_tier")) {
                JSONObject jsonObject_poster_tier = jsonObject.getJSONObject("poster_tier");
                userAccountModel.setPosterTier(new TierModel().getJsonToModel(jsonObject_poster_tier));
            }
            if (jsonObject.has("skills") && !jsonObject.isNull("skills")) {
                JSONObject jsonObject_skills = jsonObject.getJSONObject("skills");
                userAccountModel.setSkills(new SkillsModel().getJsonToModel(jsonObject_skills));
            }
            if (jsonObject.has("portfolio") && !jsonObject.isNull("portfolio")) {
                JSONArray jsonArray_portfolio = jsonObject.getJSONArray("portfolio");
                ArrayList<AttachmentModel> attachmentArrayList = new ArrayList<>();
                for (int i = 0; jsonArray_portfolio.length() > i; i++) {
                    AttachmentModel attachment = new AttachmentModel().getJsonToModel(jsonArray_portfolio.getJSONObject(i));
                    attachmentArrayList.add(attachment);
                }
                userAccountModel.setPortfolio(attachmentArrayList);
            }


            if (jsonObject.has("badges") && !jsonObject.isNull("badges")) {
                JSONArray jsonArray_badges = jsonObject.getJSONArray("badges");
                ArrayList<BadgesModel> badgesModelArrayList = new ArrayList<>();
                for (int i = 0; jsonArray_badges.length() > i; i++) {

                    BadgesModel data = new BadgesModel().getJsonToModel(jsonArray_badges.getJSONObject(i));
                    badgesModelArrayList.add(data);
                }
                userAccountModel.setBadges(badgesModelArrayList);
            }


            if (jsonObject.has("account_status") && !jsonObject.isNull("account_status")) {
                JSONObject jsonArray_accountStatus = jsonObject.getJSONObject("account_status");
                userAccountModel.setAccount_status(new AccountStatusModel().getJsonToModel(jsonArray_accountStatus));
            }


/*            if (jsonObject.has("worker_rank") && !jsonObject.isNull("worker_rank"))
                userAccountModel.setWorkerRank(jsonObject.getInt("worker_rank"));
            if (jsonObject.has("poster_rank") && !jsonObject.isNull("poster_rank"))
                userAccountModel.setPosterRank(jsonObject.getInt("poster_rank"));*/
            if (jsonObject.has("join_date") && !jsonObject.isNull("join_date"))
                userAccountModel.setJoinDate(jsonObject.getString("join_date"));
            if (jsonObject.has("last_online") && !jsonObject.isNull("last_online"))
                userAccountModel.setLastOnline(TimeAgo.getTimeAgo(jsonObject.getString("last_online")));


            if (jsonObject.has("blocked") && !jsonObject.isNull("blocked")) {
                userAccountModel.setBlocked(jsonObject.getBoolean("blocked"));
            }
            if (jsonObject.has("blocked_until") && !jsonObject.isNull("blocked_until")) {
                userAccountModel.setBlockedUntil(jsonObject.getString("blocked_until"));
            }

          /*  if (jsonObject.has("poster_level") && !jsonObject.isNull("poster_level")) {
                userAccountModel.setPoster_level(jsonObject.getInt("poster_level"));
            }

            if (jsonObject.has("worker_level") && !jsonObject.isNull("worker_level")) {
                userAccountModel.setWorker_level(jsonObject.getInt("worker_level"));
            }*/

        } catch (JSONException e) {
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return userAccountModel;
    }
}
