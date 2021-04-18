package com.jobtick.android.models.response.myjobs

import android.os.Parcel
import android.os.Parcelable

data class AssignedWorker(
    val avatar: String?,
    val pivot: Pivot?,
    val user_id: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(Pivot::class.java.classLoader),
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(avatar)
        parcel.writeParcelable(pivot, flags)
        parcel.writeValue(user_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssignedWorker> {
        override fun createFromParcel(parcel: Parcel): AssignedWorker {
            return AssignedWorker(parcel)
        }

        override fun newArray(size: Int): Array<AssignedWorker?> {
            return arrayOfNulls(size)
        }
    }
}