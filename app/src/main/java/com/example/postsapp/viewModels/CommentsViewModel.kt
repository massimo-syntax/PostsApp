package com.example.postsapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postsapp.models.Comment
import com.example.postsapp.models.Profile
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.runBlocking

class CommentsViewModel : ViewModel() {

    // FIREBASE RTDB INSTANCE
    private val db = Firebase.database.reference
    private val commentsRef = Firebase.database.getReference("comments")

    // instead of a livedata the list can be updated in parallel with the event
    // thats made from the firebase event listener: commentsChildsEventListener = object : ChildEventListener {
    var allComments = mutableListOf<Comment>()

    private val _event = MutableLiveData<Pair<String,String>?>(null)
    val event : LiveData<Pair<String,String>?>
        get() = _event


    //  WRITE
    fun writeComment(c:Comment, postId:String, uid: String){
        val key = commentsRef.push().key
        if (key == null) return
        c.id = key

        val updates: MutableMap<String, Any> = hashMapOf(
            "comments/$postId/$key" to c,
            "profiles/$uid/myComments/$key" to postId,
        )
        Firebase.database.reference.updateChildren(updates).addOnCompleteListener {
            // done by commentsEventListener when added
        }

    }
    //  DELETE
    fun deleteComment(c:Comment, postId:String, uid: String){
        // remove comment
        commentsRef
            .child(postId)
            .child(c.id!!)
            .removeValue().addOnSuccessListener { /* added, wait event listener */ }.addOnFailureListener {/* i ask the senior first.. */}
        // remove comment from my profile
        Firebase.database.getReference("profiles")
            .child(uid)
            .child("myComments")
            .child(c.id!!).removeValue()
    }


     fun likeComment(uid: String, commentId: String , postId: String) {
        val updates: MutableMap<String, Any> = hashMapOf(
            "profiles/$uid/likedComments/$commentId" to postId,
            "comments/$postId/$commentId/likesCount" to ServerValue.increment(1)
        )
         db.updateChildren(updates).addOnCompleteListener {
             val index = allComments.indexOfFirst{it.id == commentId}
             allComments[index].likesCount = allComments[index].likesCount!! + 1
             _event.value= Pair("liked", commentId )
         }
    }

    fun unlikeComment(uid: String , commentId: String, postId: String){
        Firebase.database.getReference("profiles").child(uid).child("likedComments").child(commentId).removeValue()
        commentsRef.child(postId).child(commentId).child("likesCount").setValue(ServerValue.increment(-1)).addOnCompleteListener{
            val index = allComments.indexOfFirst{it.id == commentId}
            allComments[index].likesCount = allComments[index].likesCount!! - 1
            _event.value = Pair("unliked" , commentId)
        }
    }




    val commentsUsers = mutableSetOf<Profile>()

    fun getCommentInfo(c:Comment){
        Firebase.database.getReference("profiles").child(c.userId!!).get().addOnSuccessListener {
            val profile = it.getValue<Profile>()
            commentsUsers.add(profile!!)
            _event.value = Pair("info",c.id.toString())
        }
    }


    val commentsEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // A new comment has been added, add it to the displayed list
                val c = dataSnapshot.getValue<Comment>()
                // this can replace downloading the whole list
                // firebase sends all comments 1 by 1 also at starting the listener
                allComments.add(0,c!!)
                // get profile of this user
                _event.value = Pair("added", c.id.toString() )
                getCommentInfo(c)
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
                //allComments.removeAt(index)
                allComments.remove(c)

                // notify observer
                _event.value = Pair("removed", index.toString() )

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
    fun registerCommentsEventListenerForThisPost(postId:String){
        commentsRef.child(postId).addChildEventListener(commentsEventListener)
    }




}