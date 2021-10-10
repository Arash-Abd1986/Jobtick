package com.jobtick.android.models.response.getbalance

import android.os.Parcel
import android.os.Parcelable

data class Data(
    val card: Card?,
    val wallet: Wallet?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Card::class.java.classLoader),
            parcel.readParcelable(Wallet::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(card, flags)
        parcel.writeParcelable(wallet, flags)
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