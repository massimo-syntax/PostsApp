package com.example.postsapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.postsapp.databinding.ActivityMainBinding
import com.example.postsapp.viewModels.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel

    fun toast(s:String){ Toast.makeText(this,s, Toast.LENGTH_SHORT).show() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true);
            supportActionBar!!.setDisplayShowHomeEnabled(true);
            supportActionBar!!.title = "this is the title"
        }

        // this viewmodel is used in fragments by activityViewModels()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.actionBarTitle.observe(this){
            title ->
            if(supportActionBar == null || title == null) return@observe
            supportActionBar!!.title = title
        }

        // Find the NavHostFragment and get its NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_activity_nav_host) as NavHostFragment
        navController = navHostFragment.navController

        // Get reference to the BottomNavigationView from the layout
        val navView: BottomNavigationView = binding.bottomNavView

        // Set item icon tint list to null (optional, to disable default icon tinting)
        navView.itemIconTintList = null

        // Set up the BottomNavigationView with the NavController for navigation
        navView.setupWithNavController(navController)

        firebaseAuth = FirebaseAuth.getInstance()
        toast("firebase user = ${firebaseAuth.currentUser?.uid} Main activity")

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.like -> {
                toast("App liked")
                true
            }
            R.id.logout -> {
                firebaseAuth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}