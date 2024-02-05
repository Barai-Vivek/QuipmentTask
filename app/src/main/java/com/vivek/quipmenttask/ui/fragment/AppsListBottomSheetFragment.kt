package com.vivek.quipmenttask.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vivek.quipmenttask.databinding.FragmentMapAppsBottomSheetBinding
import com.vivek.quipmenttask.ui.activities.TripsActivity
import com.vivek.quipmenttask.ui.adapters.MapAppAdapter


class AppsListBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMapAppsBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val mapsApp = mutableListOf<ResolveInfo>()
    private var googleMapUrl: String? = null

    private val onItemClicked: (Int) -> Unit = {
        println("Item clicked position -> $it  ${mapsApp[it].activityInfo.packageName}")
        if(!googleMapUrl.isNullOrEmpty() && mapsApp[it].activityInfo.packageName == "com.google.android.apps.maps"){
            try {
                // Create the URI with the Google Maps URL
                val gmmIntentUri = Uri.parse(googleMapUrl)
                // Create an intent with the ACTION_VIEW action and the URI
                val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                // Start the activity with the chooser
                startActivity(intent)
            } catch (e : ActivityNotFoundException){
                e.printStackTrace()
            }
        } else {
            Toast.makeText(context, "This app does not support route navigation", Toast.LENGTH_SHORT).show()
        }
    }
    private var mapAppsAdapter = MapAppAdapter(mapsApp, onItemClicked)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapAppsBottomSheetBinding.inflate(inflater, container, false)

        // Retrieve data from arguments
        googleMapUrl = arguments?.getString("mapUrl", "")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeView()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Handle the back button press here
            backPressed()
        }
    }

    private fun initializeView() {
        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tripsRecyclerView.adapter = mapAppsAdapter

        // Get map applications
        val pm: PackageManager? = context?.packageManager
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=42.3,71.2"))
        val mapApps = pm?.queryIntentActivities(mapIntent, 0)

        if (mapApps.isNullOrEmpty()) {
            Toast.makeText(context, "No map applications found", Toast.LENGTH_SHORT).show()
        } else {
            mapsApp.addAll(mapApps)
        }

    }

    private fun backPressed() {
        (activity as TripsActivity).removeTopFragmentFromBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
