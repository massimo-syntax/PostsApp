package com.example.postsapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.postsapp.models.Draft

@Database(entities = arrayOf(Draft::class), version = 1, exportSchema = false)
abstract class DraftsDB : RoomDatabase() {

    abstract fun getDraftsDao(): DraftsDao

    companion object {
        // Singleton prevents multiple
        // instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: DraftsDB? = null

        fun getDatabase(context: Context): DraftsDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DraftsDB::class.java,
                    "drafts_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
