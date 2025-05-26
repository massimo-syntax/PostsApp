package com.example.postsapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postsapp.models.Comment
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class CommentsViewModel : ViewModel() {

    // FIREBASE RTDB INSTANCE
    private val db = Firebase.database
    private val commentsRef = db.getReference("comments")

    // instead of a livedata the list can be updated in parallel with the event
    // thats made from the firebase event listener: commentsChildsEventListener = object : ChildEventListener {
    val allComments = mutableListOf<Comment>()


    private val _event = MutableLiveData<Pair<String,String>?>(null)
    val event : LiveData<Pair<String,String>?>
        get() = _event



    //  WRITE
    fun writeComment(c:Comment, postId:String){
        commentsRef.child(postId).child(c.id!!).setValue(c).addOnSuccessListener { /* added, wait event listener */ }.addOnFailureListener {/* i ask the senior first.. */}
    }
    //  DELETE
    fun deleteComment(c:Comment, postId:String){
        commentsRef.child(postId).child(c.id!!).removeValue().addOnSuccessListener { /* added, wait event listener */ }.addOnFailureListener {/* i ask the senior first.. */}
    }


    val commentsChildsEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // A new comment has been added, add it to the displayed list
                val c = dataSnapshot.getValue<Comment>()
                // this can replace downloading the whole list
                // firebase sends all comments 1 by 1 also at starting the listener
                allComments.add(c!!)
                // event callback changing value
                // that directly calls the observer (i hope.. didnt read the java code of the observer, jet..)
                _event.value = Pair("added", c.id.toString() )

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("comment updated", "onChildChanged: ${dataSnapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                val c = dataSnapshot.getValue<Comment>()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d("comment removed", "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val c = dataSnapshot.getValue<Comment>()
                val cKey = dataSnapshot.key

                val index = allComments.indexOf(c)
                allComments.removeAt(index)

                _event.value = Pair("removed", index.toString() )

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("comment moved", "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedComment = dataSnapshot.getValue<Comment>()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("something went wrong with comments from firebase", "postComments:onCancelled", databaseError.toException())
                Log.w("something went wrong with comments from firebase", "postComments:onCancelled", databaseError.toException())
                Log.w("something went wrong with comments from firebase", "postComments:onCancelled", databaseError.toException())
                Log.w("something went wrong with comments from firebase", "postComments:onCancelled", databaseError.toException())
                Log.w("something went wrong with comments from firebase", "postComments:onCancelled", databaseError.toException())

            }
    }

    // to use in the fragment
    fun registerPostCommentsEventListener(id:String){
        commentsRef.child(id).addChildEventListener(commentsChildsEventListener)
    }




}