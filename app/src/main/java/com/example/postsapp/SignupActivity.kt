package com.example.postsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.lifecycleScope
import com.example.postsapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignupBinding
    private lateinit var firebaseAuth:FirebaseAuth

    fun toast(s:String){ Toast.makeText(this,s,Toast.LENGTH_SHORT).show() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            val email:String = binding.etEmailAddress.text.toString()
            val password:String = binding.etPassword.text.toString()
            val password2:String = binding.etRepeatPassword.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                toast("There are empty fields")
                return@setOnClickListener
            }
            if(password != password2){
                toast("Password does not match")
                return@setOnClickListener
            }

            binding.progressCircular.visibility = View.VISIBLE

            lifecycleScope.launch(Dispatchers.IO){
                signup(firebaseAuth,email,password)
            }
        }

        binding.tvAlreadyHaveAnAccount.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    } // onCreate() END

    private suspend fun signup(firebaseAuth: FirebaseAuth, emailId: String, password: String) : AuthResult? {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(emailId,password)
                .await()
            signupGotResult(result.user)
            return result

        } catch (e :Exception){
            withContext(Dispatchers.Main){
                //toast("login failed: $e")
                binding.progressCircular.visibility = View.GONE
            }
            return null
        }
    }


    private suspend fun signupGotResult(user : FirebaseUser? ) {
        if(user == null){
          //toast("no auth user")
            return
        }
        withContext(Dispatchers.Main){
            binding.progressCircular.visibility = View.GONE
            //toast("login success $user")
            startActivity(Intent( this@SignupActivity , LoginActivity::class.java))
            finish()
        }
    }



}