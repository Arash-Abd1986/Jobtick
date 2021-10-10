package com.jobtick.android.models.response.myjobs

import android.os.Parcel
import android.os.Parcelable

data class MyJobsResponse(
    val current_page: Int?,
    val `data`: List<Data>?,
    val first_page_url: String?,
    val from: Int?,
    val last_page: Int?,
    val last_page_url: String?,
    val links: List<Link>?,
    val next_page_url: String?,
    val path: String?,
    val per_page: Int?,
    val prev_page_url: String?,
    val to: Int?,
    val total: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.createTypedArrayList(Data),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.createTypedArrayList(Link),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(current_page)
        parcel.writeTypedList(data)
        parcel.writeString(first_page_url)
        parcel.writeValue(from)
        parcel.writeValue(last_page)
        parcel.writeString(last_page_url)
        parcel.writeTypedList(links)
        parcel.writeString(next_page_url)
        parcel.writeString(path)
        parcel.writeValue(per_page)
        parcel.writeString(prev_page_url)
        parcel.writeValue(to)
        parcel.writeValue(total)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyJobsResponse> {
        override fun createFromParcel(parcel: Parcel): MyJobsResponse {
            return MyJobsResponse(parcel)
        }

        override fun newArray(size: Int): Array<MyJobsResponse?> {
            return arrayOfNulls(size)
        }
    }
}