package com.example.pet_pawtrol

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.pet_pawtrol.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    lateinit var navController: NavController
    var pref : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        MAIN = this

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    fun saveData(data: String){
        val editor = pref?.edit()
        editor?.putString("user",data)
        editor?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val editor = pref?.edit()
        editor?.clear()
        editor?.apply()
    }
}