package com.lanhee.mapdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHost
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.lanhee.mapdiary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), IncludeFABActivity {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.findNavController()
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.fab.visibility = if(destination.id == R.id.activities) View.VISIBLE else View.GONE
        }
        binding.navigation.setupWithNavController(navController)

        binding.fab.setOnClickListener {
            val fragment = binding.container.getFragment<NavHostFragment>()
            val displayedFragment = fragment.childFragmentManager.fragments[0]
            (displayedFragment as ActivitiesFragment).onFABClick()
        }
    }

    override fun setFABVisibility(visibility: Int) {
        binding.fab.visibility = visibility
    }
}