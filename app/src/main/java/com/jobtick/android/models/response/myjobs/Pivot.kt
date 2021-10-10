package com.jobtick.android.models.response.myjobs

import android.os.Parcel
import android.os.Parcelable

data class Pivot(
    val created_at: String?,
    val leveltier_id: Int?,
    val send_reviewed_at: String?,
    val task_id: Int?,
    val user_id: Int?,
    val user_type: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(created_at)
        parcel.writeValue(leveltier_id)
        parcel.writeString(send_reviewed_at)
        parcel.writeValue(task_id)
        parcel.writeValue(user_id)
        parcel.writeString(user_type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pivot> {
        override fun createFromParcel(parcel: Parcel): Pivot {
            return Pivot(parcel)
        }

        override fun newArray(size: Int): Array<Pivot?> {
            return arrayOfNulls(size)
        }
    }
}