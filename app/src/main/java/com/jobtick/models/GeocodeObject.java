package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GeocodeObject implements Parcelable {
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String knownName;

    public GeocodeObject() {
    }

    protected GeocodeObject(Parcel in) {
        address = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        postalCode = in.readString();
        knownName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(postalCode);
        dest.writeString(knownName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GeocodeObject> CREATOR = new Creator<GeocodeObject>() {
        @Override
        public GeocodeObject createFromParcel(Parcel in) {
            return new GeocodeObject(in);
        }

        @Override
        public GeocodeObject[] newArray(int size) {
            return new GeocodeObject[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public GeocodeObject setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public GeocodeObject setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public GeocodeObject setState(String state) {
        this.state = state;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public GeocodeObject setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public GeocodeObject setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getKnownName() {
        return knownName;
    }

    public GeocodeObject setKnownName(String knownName) {
        this.knownName = knownName;
        return this;
    }

}
