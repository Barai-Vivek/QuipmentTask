package com.vivek.quipmenttask.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vivek.quipmenttask.R
import com.vivek.quipmenttask.data.model.Trip
import com.vivek.quipmenttask.databinding.FragmentTripsListBinding
import com.vivek.quipmenttask.ui.activities.TripsActivity
import com.vivek.quipmenttask.ui.adapters.TripsAdapter
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
        val selectedTrips = selectedItems.map { position ->
            if (position in tripsList.indices) {
                tripsList[position] // Return the element at the specified position if it exists in the list
            } else {
                null
            }
        }

        val startAddress = selectedTrips.firstOrNull()?.pickUpAddress
            ?: "" // Get the starting address entered by the user
        val destinationAddress =
            selectedTrips.lastOrNull()?.dropOffAddress
                ?: "" // Get the destination address entered by the user

        // Create the URI with the starting point and destination address
        val uri = Uri.parse("geo:0,0?q=$startAddress,$destinationAddress")

        // Create an intent with the ACTION_VIEW action and the URI
        val intent = Intent(Intent.ACTION_VIEW, uri)

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
