package com.example.postsapp.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Comment(
    var id: String? = "",
    var user: String? = "",
    var comment: String? = "",
    var likes: Int? = 0
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "user" to user,
            "comment" to comment,
            "likes" to likes
        )
    }
}
