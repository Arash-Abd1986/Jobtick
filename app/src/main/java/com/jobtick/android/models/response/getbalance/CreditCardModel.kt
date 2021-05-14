package com.jobtick.android.models.response.getbalance

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

data class CreditCardModel(
        var isSuccess: Boolean,
        var message: String?,
        var data: List<Data>? = null


) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.createTypedArrayList(Data)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isSuccess) 1 else 0)
        parcel.writeString(message)
        parcel.writeTypedList(data)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<CreditCardModel> {
        override fun createFromParcel(parcel: Parcel): CreditCardModel {
            return CreditCardModel(parcel)
        }

        override fun newArray(size: Int): Array<CreditCardModel?> {
            return arrayOfNulls(size)
        }
    }
}