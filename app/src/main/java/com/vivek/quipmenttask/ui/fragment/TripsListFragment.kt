package com.vivek.quipmenttask.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vivek.quipmenttask.R
import com.vivek.quipmenttask.data.model.Trip
import com.vivek.quipmenttask.databinding.FragmentTripsListBinding
import com.vivek.quipmenttask.ui.activities.TripsActivity
import com.vivek.quipmenttask.ui.adapters.TripsAdapter
import com.vivek.quipmenttask.util.GeoCoderHelper
import com.vivek.quipmenttask.viewmodel.TripViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TripsListFragment : Fragment() {

    private var _binding: FragmentTripsListBinding? = null
    private val binding get() = _binding!!

    private val tripsList = mutableListOf<Trip>()
    private var selectedItems = HashSet<Int>()

    private val tripViewModel: TripViewModel by activityViewModels()

    private val changeItemSelection: (Int) -> Unit = {
        if (selectedItems.contains(it)) {
            selectedItems.remove(it)
        } else {
            selectedItems.add(it)
        }
        if (selectedItems.size > 0) {
            binding.btnNavigate.visibility = View.VISIBLE
        } else {
            binding.btnNavigate.visibility = View.GONE
        }
    }
    private var tripsAdapter = TripsAdapter(tripsList, selectedItems, changeItemSelection)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripsListBinding.inflate(inflater, container, false)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        readTrips()
    }

    private fun initializeView() {
        binding.toolbar.title = "Trips list"
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            backPressed()
        }

        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tripsRecyclerView.adapter = tripsAdapter

        binding.btnNavigate.setOnClickListener {
            openList()
        }
    }

    private fun backPressed() {
        (activity as TripsActivity).removeTopFragmentFromBackStack()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Read all trips
    private fun readTrips() {
        CoroutineScope(Dispatchers.Main).launch {
            tripViewModel.getAllTrips().observe(viewLifecycleOwner) { trips ->
                //do action here, and now you have data in list (notes)
                val oldCount = tripsList.size
                tripsList.addAll(trips)
                binding.progressBar.visibility = View.GONE
                tripsAdapter.notifyItemRangeInserted(oldCount, tripsList.size)
            }
        }
    }

    private fun openList() {
        binding.progressBar.visibility = View.VISIBLE
        // Start a coroutine to perform tasks in the background
        lifecycleScope.launch(Dispatchers.Default) {
            // Convert positionsHashSet to a list of positions
            val positionsList = selectedItems.toList()

            // Filter the tripsList based on the positions in positionsHashSet
            val filteredList = tripsList.filterIndexed { index, _ -> index in positionsList }

            // Sort the filtered list based on the positions in positionsHashSet
            val sortedList = filteredList.sortedBy { trip -> positionsList.indexOf(tripsList.indexOf(trip)) }

            // Generate a URL with address in the background
            val googleMapUrl =
                context?.let { GeoCoderHelper(it).generateGoogleMapsDirectionsAddressURL(sortedList) }

            // Start the activity in the foreground
            launch(Dispatchers.Main) {
                // Create the URI with the Google Maps URL
                val gmmIntentUri = Uri.parse(googleMapUrl)

                // Create an intent with the ACTION_VIEW action and the URI
                val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                // Set the intent category to BROWSABLE to allow apps to handle the intent
                intent.addCategory(Intent.CATEGORY_BROWSABLE)

                // Get a list of activities that can handle the intent
                val packageManager = requireContext().packageManager
                val activities =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

                binding.progressBar.visibility = View.GONE
                // Check if there are any activities that can handle the intent
                if (activities.isNotEmpty()) {
                    // Create a chooser dialog to let the user choose the map application
                    val chooser = Intent.createChooser(intent, "Select Map Application")

                    // Start the activity with the chooser
                    startActivity(chooser)
                } else {
                    // Handle case where no map application is available
                    Toast.makeText(context, "No map application found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun testList() {

        // Convert positionsHashSet to a list of positions
        val positionsList = selectedItems.toList()

        // Filter the tripsList based on the positions in positionsHashSet
        val filteredList = tripsList.filterIndexed { index, _ -> index in positionsList }

        // Sort the filtered list based on the positions in positionsHashSet
        val sortedList = filteredList.sortedBy { trip -> positionsList.indexOf(tripsList.indexOf(trip)) }

        //generate a url with lat lng
        /*val googleMapUrl =
            context?.let { GeoCoderHelper(it).generateGoogleMapsDirectionsLatLngURL(sortedList) }*/

        //generate a url with address
        //val example = "https://www.google.com/maps/dir/Jamnagar/Ahmedabad/Rajkot/Surat"
        val googleMapUrl =
            context?.let { GeoCoderHelper(it).generateGoogleMapsDirectionsAddressURL(sortedList) }

        //get lat lng for address
        //val latlng = context?.let { GeoCoderHelper(it).getLatLngFromAddress("Jamnagar, gujarat") }
        //val latlng1 = context?.let { GeoCoderHelper(it).getLatLngFromAddress("Ahmedabad, gujarat") }
        //println("lat lng ${latlng1?.first} & ${latlng1?.second}")

        // Create the URI with the starting point and destination address
        //val uri = Uri.parse("geo:0,0?q=$startAddress,$destinationAddress")  //Work only in google but shows all installed maps
        //val uri = Uri.parse("geo:${lat},${lng}")
        //val uri =
        //Uri.parse(
            //"geo:${latlng?.first},${latlng?.second}?q=" + Uri.encode("${latlng1?.first},${lng1} (${latlng1?.second})"))

        val gmmIntentUri =
            Uri.parse(googleMapUrl)

        // Create an intent with the ACTION_VIEW action and the URI
        val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

        // Set the intent category to BROWSABLE to allow apps to handle the intent
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        // Get a list of activities that can handle the intent
        val packageManager = requireContext().packageManager
        val activities =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        // Check if there are any activities that can handle the intent
        if (activities.isNotEmpty()) {
            // Create a chooser dialog to let the user choose the map application
            val chooser = Intent.createChooser(intent, "Select Map Application")

            // Start the activity with the chooser
            startActivity(chooser)
        } else {
            // Handle case where no map application is available
            Toast.makeText(context, "No map application found", Toast.LENGTH_SHORT).show()
        }
    }

}
