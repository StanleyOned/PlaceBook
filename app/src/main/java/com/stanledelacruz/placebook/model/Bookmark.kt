package com.stanledelacruz.placebook.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity

data class Bookmark(@PrimaryKey(autoGenerate = true) var id: Long? = null,
                    var placeId: String? = null,
                    var name: String = "",
                    var address: String = "",
                    var latitude: Double = 0.0,
                    var longitude: Double = 0.0,
                    var phone: String = "") {
}