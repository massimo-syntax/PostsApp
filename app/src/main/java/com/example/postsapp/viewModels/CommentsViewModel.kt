package com.example.postsapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postsapp.models.Comment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class CommentsViewModel : ViewModel() {

    // FIREBASE AUTH                                                        FIREBASE AUTH
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    public val currentUID = firebaseAuth.currentUser?.uid!!

    // FIREBASE RTDB INSTANCE
    private val db = Firebase.database
    private val commentsRef = db.getReference("comments")

    private val _allPostComments = MutableLiveData<List<Comment>?> (null)
    val allPostsComments : LiveData<List<Comment>?>
        get() = _allPostComments


    private val _event = MutableLiveData<Pair<String,String>?>(null)
    val event : LiveData<Pair<String,String>?>
        get() = _event

    var addedComment : Comment? = null


    fun getAllComments(postId:String){
        val postComments =  commentsRef.child(postId)
        postComments.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val snapshot = it.result
                val comments = mutableListOf<Comment>()
                for (commentsSnapshot in snapshot.children) {
                    val comment = commentsSnapshot.getValue(Comment::class.java)
                    comments.add(comment!!)
                }
                Log.d("comments list succesful", "${comments.size}")
                _allPostComments.value = comments
            } else {
                Log.d("error retreiving comments list, or empty", "${it.exception?.message}") //Never ignore potential errors!
            }
        }
    }

    fun writeComment(c:Comment, postId:String){
        commentsRef.child(postId).child(c.id!!).setValue(c).addOnSuccessListener {
            // added
        }.addOnFailureListener {/**/}
    }


        val commentsChildsEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("comment added", "onChildAdded:" + dataSnapshot.key!!)
                // A new comment has been added, add it to the displayed list
                val c = dataSnapshot.getValue<Comment>()

                // this can replace downloading the whole list
                addedComment = c
                _event.value = Pair("added", "addedComment = ${addedComment.toString()}" ) // when null probably is ""



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
                val commentKey = dataSnapshot.key

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

    fun registerPostCommentsEventListener(id:String){
        commentsRef.child(id).addChildEventListener(commentsChildsEventListener)
    }




}