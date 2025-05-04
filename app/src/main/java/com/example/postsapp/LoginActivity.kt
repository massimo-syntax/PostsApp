package com.example.postsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.example.postsapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    //fun toast(s:String){ Toast.makeText(this,s, Toast.LENGTH_SHORT).show() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser == null){
            //toast("no user logged in")
        }else{
            //toast("current user from login: "+firebaseAuth.currentUser.toString())
            startActivity(Intent(this, MainActivity::class.java))
        }


        binding.btnLogin.setOnClickListener {
            val email:String = binding.etEmailAddress.text.toString()
            val password:String = binding.etPassword.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                //toast("both fields are required")
                return@setOnClickListener
            }

            binding.progressCircular.visibility = View.VISIBLE

            lifecycleScope.launch(Dispatchers.IO){
                login(firebaseAuth,email,password)
            }
        }

        binding.tvNoAccount.setOnClickListener {
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
            finish()
        }



    }// onCreate() END


    private suspend fun login(firebaseAuth: FirebaseAuth, email: String, password: String) : AuthResult? {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email,password)
                .await()
            loginReceivedResult(result.user)
            return result
        } catch (e :Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(application, "login failed, probably you have to register first" ,Toast.LENGTH_SHORT).show()
                binding.progressCircular.visibility = View.GONE
            }
            return null
        }
    }


    private suspend fun loginReceivedResult(user : FirebaseUser? ) {
        if(user == null){
            // something went wrong
            // login just throws exception on try catch when no user
            // in the other function here up
            return
        }
        withContext(Dispatchers.Main){
            binding.progressCircular.visibility = View.GONE
            //toast("login success $user")
            startActivity(Intent( this@LoginActivity , LoginActivity::class.java))
            finish()
        }
    }



    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


}