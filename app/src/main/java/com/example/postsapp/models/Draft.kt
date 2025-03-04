package com.example.postsapp.models



import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// on below line we are specifying our table name
@Entity(tableName = "notesTable")

// on below line we are specifying our column info
// and inside that we are passing our column name
data class Draft (

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "title")
    val title :String,

    @ColumnInfo(name = "description")
    val description :String,

    @ColumnInfo(name = "timestamp")
    val timeStamp :String,

    @ColumnInfo(name = "images")
    val images: String,

    @ColumnInfo(name = "tags")
    val tags : String

)

