package com.example.postsapp.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Comment(
    var id: String? = "",
    var userName: String? = "",
    var userId: String? = "",
    var comment: String? = "",
    var dateTime:String? = "",
    var likes: MutableMap <String, Boolean>? = mutableMapOf()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userName" to userName,
            "userId" to userId,
            "comment" to comment,
            "dateTime" to dateTime,
            "likes" to likes
        )
    }
}
