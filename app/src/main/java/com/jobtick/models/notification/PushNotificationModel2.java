
package com.jobtick.models.notification;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushNotificationModel2 implements Parcelable
{

    @SerializedName("data")
    @Expose
    private List<NotifDatum> data = null;
    @SerializedName("links")
    @Expose
    private Links links;
    @SerializedName("meta")
    @Expose
    private Meta meta;
    public final static Creator<PushNotificationModel2> CREATOR = new Creator<PushNotificationModel2>() {


        @SuppressWarnings({
            "unchecked"
        })
        public PushNotificationModel2 createFromParcel(Parcel in) {
            return new PushNotificationModel2(in);
        }

        public PushNotificationModel2 [] newArray(int size) {
            return (new PushNotificationModel2[size]);
        }

    }
    ;

    protected PushNotificationModel2(Parcel in) {
        in.readList(this.data, (NotifDatum.class.getClassLoader()));
        this.links = ((Links) in.readValue((Links.class.getClassLoader())));
        this.meta = ((Meta) in.readValue((Meta.class.getClassLoader())));
    }

    public PushNotificationModel2() {
    }

    public List<NotifDatum> getData() {
        return data;
    }

    public void setData(List<NotifDatum> data) {
        this.data = data;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(data);
        dest.writeValue(links);
        dest.writeValue(meta);
    }

    public int describeContents() {
        return  0;
    }

}
