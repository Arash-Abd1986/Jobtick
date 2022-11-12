package com.jobtick.android.material.ui.jobdetails

import android.os.Parcel
import android.os.Parcelable

data class PaymentData(
        val amount: Int,
        val offerId: Int,
        val slug: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(amount)
        parcel.writeInt(offerId)
        parcel.writeString(slug)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentData> {
        override fun createFromParcel(parcel: Parcel): PaymentData {
            return PaymentData(parcel)
        }

        override fun newArray(size: Int): Array<PaymentData?> {
            return arrayOfNulls(size)
        }
    }
}
