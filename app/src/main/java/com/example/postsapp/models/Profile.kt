package com.example.postsapp.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Profile(
    // id of profile is uid
    var uid: String? = "",
    var name: String? = "",
    var say: String? = "",
    var image: String? = "",
    var postsCount: Int? = 0,
    var myPosts: MutableMap <String,Boolean>? = mutableMapOf(),
    var followersCount: Int? = 0,
    var followers: MutableMap <String, Boolean>? = mutableMapOf(),
    var followed: MutableMap <String, Boolean>? = mutableMapOf(),
    var likedComments: MutableMap<String,String>? = mutableMapOf(),
    var myComments: MutableMap<String,String>? = mutableMapOf()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "say" to say,
            "image" to image,
            "postsCount" to postsCount,
            "myPosts" to myPosts,
            "followersCount" to followersCount,
            "followers" to followers,
            "followed" to followed,
            "likedComments" to likedComments,
            "myComments" to myComments
        )
    }
}