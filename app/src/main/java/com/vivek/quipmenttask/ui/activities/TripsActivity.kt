package com.vivek.quipmenttask.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.vivek.quipmenttask.R
import com.vivek.quipmenttask.databinding.ActivityTripsBinding
import com.vivek.quipmenttask.ui.fragment.TripDetailFragment
import com.vivek.quipmenttask.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripsActivity : AppCompatActivity() {

    private var _binding: ActivityTripsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addFragmentToBackStack(TripDetailFragment(), Constants.TRIPS_DETAIL_FRAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Method to addFragmentToBackStack Fragment Trip detail with Fragment Trip List
    fun addFragmentToBackStack(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, fragment, tag)
            .addToBackStack(tag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    // Method to remove the top fragment from the back stack
    fun removeTopFragmentFromBackStack() {
        supportFragmentManager.popBackStack()
    }
}