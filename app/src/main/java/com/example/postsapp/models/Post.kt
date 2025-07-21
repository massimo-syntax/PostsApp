package com.example.postsapp.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Post(
    var id: String? = "",
    var user: String? = "",
    var userId: String? = "",
    var title: String? = "",
    var body: String? = "",
    var image: String? = "",
    var datetime : String? = "",
    var tags: MutableMap <String, Boolean>? = mutableMapOf(),
    var likes: MutableMap <String, Boolean>? = mutableMapOf(),
    var likesCount: Int? = 0
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "user" to user,
            "userId" to userId,
            "title" to title,
            "body" to body,
            "image" to image,
            "likes" to likes,
            "likesCount" to likesCount
        )
    }
}
