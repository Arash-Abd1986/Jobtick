package com.jobtick.android.material.ui.jobdetails

import android.os.Parcel
import android.os.Parcelable

data class CardInfo(
        val accountHolder: String?,
        val cardNumber: String?,
        val expiryDate: String?,
        val CVC: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountHolder)
        parcel.writeString(cardNumber)
        parcel.writeString(expiryDate)
        parcel.writeString(CVC)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardInfo> {
        override fun createFromParcel(parcel: Parcel): CardInfo {
            return CardInfo(parcel)
        }

        override fun newArray(size: Int): Array<CardInfo?> {
            return arrayOfNulls(size)
        }
    }
}
