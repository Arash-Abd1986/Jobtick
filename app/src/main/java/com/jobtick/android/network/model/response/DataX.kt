package com.jobtick.android.network.model.response

import android.os.Parcel
import android.os.Parcelable

data class DataX(
        val created_at: String?,
        val id: Int,
        val max_budget: Int,
        val min_budget: Int,
        val title: String?,
        val updated_at: String?,
        var isChecked: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(created_at)
        parcel.writeInt(id)
        parcel.writeInt(max_budget)
        parcel.writeInt(min_budget)
        parcel.writeString(title)
        parcel.writeString(updated_at)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataX> {
        override fun createFromParcel(parcel: Parcel): DataX {
            return DataX(parcel)
        }

        override fun newArray(size: Int): Array<DataX?> {
            return arrayOfNulls(size)
        }
    }
}