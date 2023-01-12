package com.nairobi.tugasakhir

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import androidx.fragment.app.Fragment
import com.nairobi.tugasakhir.admin.HomepageAdmin
import com.nairobi.tugasakhir.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

     private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomepageAdmin())

        binding.bottomNavigation.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> replaceFragment(HomepageAdmin())
                R.id.absen-> replaceFragment(SplashScreen())
                R.id.profil -> replaceFragment(HomepageAdmin())

                else ->{



                }

            }

            true

        }



    }
        private fun replaceFragment(fragment :Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,fragment)
        fragmentTransaction.commit()

    }
}