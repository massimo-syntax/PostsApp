package com.example.postsapp.repositories

import androidx.lifecycle.LiveData
import com.example.postsapp.models.Draft
import com.example.postsapp.room.DraftsDao

class DraftsRepository(private val dao: DraftsDao) {

    val allDrafts: LiveData<List<Draft>> = dao.getAll()

    // on below line we are creating an insert method
    // for adding the note to our database.
    suspend fun insert(d: Draft) {
        dao.insert(d)
    }

    // on below line we are creating a delete method
    // for deleting our note from database.
    suspend fun delete(d: Draft){
        dao.delete(d)
    }

    // on below line we are creating a update method for
    // updating our note from database.
    suspend fun update(d: Draft){
        dao.update(d)
    }




}


