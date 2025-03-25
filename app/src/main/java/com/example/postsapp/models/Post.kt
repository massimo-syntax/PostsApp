package com.example.postsapp.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Post(
    var uid: String? = "",
    var title: String? = "",
    var body: String? = "",
    var image: String? = "",
    var tags: List<String>? = listOf(),
    var likes: Int? = 0,
    var comments: List<String>? = listOf()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "title" to title,
            "body" to body,
            "image" to image,
            "likes" to likes,
            "comments" to comments,
        )
    }
}
