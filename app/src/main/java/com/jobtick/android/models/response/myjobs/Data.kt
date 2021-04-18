package com.jobtick.android.models.response.myjobs

import android.os.Parcel
import android.os.Parcelable
import org.bouncycastle.its.asn1.Latitude
import org.bouncycastle.its.asn1.Longitude

data class Data(
    val assigned_worker: List<AssignedWorker>?,
    val budget: Int?,
    val created_at: String?,
    val due_date: String?,
    val id: Int?,
    val location: String?,
    val latitude: String?,
    val longitude: String?,
    val offered_users: List<OfferedUser>?,
    val offered_users_count: Int?,
    val poster_avatar: String?,
    val slug: String?,
    val status: String?,
    val title: String?,
    val updated_at: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(AssignedWorker),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(OfferedUser),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(assigned_worker)
        parcel.writeValue(budget)
        parcel.writeString(created_at)
        parcel.writeString(due_date)
        parcel.writeValue(id)
        parcel.writeString(location)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeTypedList(offered_users)
        parcel.writeValue(offered_users_count)
        parcel.writeString(poster_avatar)
        parcel.writeString(slug)
        parcel.writeString(status)
        parcel.writeString(title)
        parcel.writeString(updated_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}