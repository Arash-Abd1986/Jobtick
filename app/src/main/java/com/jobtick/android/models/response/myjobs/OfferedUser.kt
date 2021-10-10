package com.jobtick.android.models.response.myjobs

import android.os.Parcel
import android.os.Parcelable

data class OfferedUser(
    val avatar: String?,
    val task_id: Int?,
    val user_id: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(avatar)
        parcel.writeValue(task_id)
        parcel.writeValue(user_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OfferedUser> {
        override fun createFromParcel(parcel: Parcel): OfferedUser {
            return OfferedUser(parcel)
        }

        override fun newArray(size: Int): Array<OfferedUser?> {
            return arrayOfNulls(size)
        }
    }
}