package com.example.postsapp

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.ofInt
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.animation.addPauseListener
import androidx.core.animation.doOnEnd
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertTimestampToReadableFormat(timestamp: Long): String {
//    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val sdf = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())

    val date = Date(timestamp)
    return sdf.format(date)
}

data class QueryString (   // Pointer to a String
    var s:String=""
)

fun slideView(view: View, currentHeight: Int, newHeight:Int) {

     val slideAnimator = ofInt(currentHeight, newHeight)
         .setDuration(500)
    /* We use an update listener which listens to each tick
     * and manually updates the height of the view  */

    slideAnimator.addUpdateListener { anim ->
        val value  = anim.animatedValue.toString()
        view.layoutParams.height = value.toInt()
        view.requestLayout()
    }


    /*  We use an animationSet to play the animation  */
    val animSet = AnimatorSet()
    animSet.interpolator = AccelerateDecelerateInterpolator()
    animSet.play(slideAnimator)
    animSet.start()


}



