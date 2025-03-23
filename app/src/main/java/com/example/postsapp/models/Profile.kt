package com.example.postsapp.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Profile(
    var uid: String? = "",
    var name: String? = "",
    var say: String? = "",
    var image: String? = "",
    var followers: List<String>? = listOf(),
    var followed: List<String>? = listOf(),
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "say" to say,
            "image" to image,
            "followers" to followers,
            "followed" to followed,
        )
    }
}