package com.example.postsapp.viewModels

import androidx.lifecycle.ViewModel
import java.util.Date

class FragmentStateViewModel:ViewModel() {

    var lastTabPressed = ""
    var incomingPictures = Pair("null", Date().time)

}