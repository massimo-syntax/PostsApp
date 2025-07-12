package com.example.postsapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var currentSection : String? = null

    // single post
    private val _actionBarTitle = MutableLiveData<String?>(null)
    val actionBarTitle : LiveData<String?>
        get() = _actionBarTitle

    fun setActionBarTitle(s:String){
        _actionBarTitle.value = s
    }


}