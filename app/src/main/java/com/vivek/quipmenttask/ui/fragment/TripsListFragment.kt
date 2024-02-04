package com.vivek.quipmenttask.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
    private val selectedItems = HashSet<Int>()

    private val tripViewModel: TripViewModel by activityViewModels()

    private var tripsAdapter: TripsAdapter = TripsAdapter(tripsList, selectedItems)

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

        readTrips()
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Log.i("CHeck ", "click")
                // Handle back button click from the support action bar
                backPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeView() {
        setHasOptionsMenu(true)
        val actionBar = (activity as TripsActivity).supportActionBar
        actionBar?.apply {
            title = "Trips list"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.tripsRecyclerView.layoutManager= LinearLayoutManager(context)
        binding.tripsRecyclerView.adapter = tripsAdapter
    }

    private fun backPressed(){
        (activity as TripsActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
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
}
