package com.vivek.quipmenttask.ui.activities

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.vivek.quipmenttask.R
import com.vivek.quipmenttask.databinding.ActivityTripsBinding
import com.vivek.quipmenttask.ui.fragment.TripDetailFragment
import com.vivek.quipmenttask.ui.fragment.TripsListFragment
import com.vivek.quipmenttask.util.Constants
import com.vivek.quipmenttask.viewmodel.TripViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TripsActivity : AppCompatActivity() {

    private var _binding: ActivityTripsBinding? = null
    private val binding get() = _binding!!


    private val tripViewModel: TripViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if savedInstanceState is null to avoid adding fragments multiple times on configuration change
        if (savedInstanceState == null) {
            // Add Fragment TripDetailFragment
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, TripDetailFragment(), Constants.TRIPS_DETAIL_FRAG)
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Method to addFragmentToBackStack Fragment Trip detail with Fragment Trip List
    fun addFragmentToBackStack(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, tag)
            .addToBackStack(tag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    // Method to remove the top fragment from the back stack
    fun removeTopFragmentFromBackStack() {
        supportFragmentManager.popBackStack()
    }
}