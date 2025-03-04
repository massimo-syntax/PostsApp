package com.example.postsapp.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.postsapp.models.Draft

// annotation for dao class.

@Dao
interface DraftsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note :Draft)

    @Delete
    suspend fun delete(note: Draft)

    @Query("Select * from notesTable order by id ASC")
    fun getAll(): LiveData<List<Draft>>

    @Update
    suspend fun update(note: Draft)

}
