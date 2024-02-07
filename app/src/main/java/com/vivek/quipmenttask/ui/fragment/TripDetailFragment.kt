package com.vivek.quipmenttask.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vivek.quipmenttask.data.model.Trip
import com.vivek.quipmenttask.databinding.FragmentTripdetailBinding
import com.vivek.quipmenttask.ui.activities.TripsActivity
import com.vivek.quipmenttask.util.Constants
import com.vivek.quipmenttask.util.formatToString
import com.vivek.quipmenttask.viewmodel.TripViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class TripDetailFragment : Fragment() {

    private var _binding: FragmentTripdetailBinding? = null
    private val binding get() = _binding!!

    private val tripViewModel: TripViewModel by activityViewModels()

    private var calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripdetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //access view model once this method is called.
        readTrips()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {
        binding.apply {
            //Add Fragment Trips List when a button is clicked
            btnNext.setOnClickListener {
                (activity as TripsActivity).addFragmentToBackStack(
                    TripsListFragment(),
                    Constants.TRIPS_LIST_FRAG
                )
            }
            btnSave.setOnClickListener {
                save()
            }

            btnClear.setOnClickListener {
                clear()
            }

            btnRemoveAll.setOnClickListener {
                removeAll()
            }

            edtTime.setOnClickListener { showDateTimePicker() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clear() {
        clearForm()
    }

    private fun save() {
        val result = generateTripObj()
        if (result.first == "Error") {
            Toast.makeText(context, result.second as String, Toast.LENGTH_SHORT).show()
        } else {
            //insert data
            insertTrip(result.second as Trip, result.first)
        }
    }

    private fun clearForm() {
        binding.apply {
            calendar = Calendar.getInstance()
            edtCustomerName.setText("")
            edtPickupAddress.setText("")
            edtDropOffAddress.setText("")
            edtPrice.setText("")
            edtTime.setText("")
            edtCustomerName.requestFocus()
        }

    }

    private fun generateTripObj(): Pair<String, Any> {
        binding.apply {
            if (edtCustomerName.text.toString().isEmpty()) {
                return Pair("Error", "Please enter customer name")
            } else if (edtPickupAddress.text.toString().isEmpty()) {
                return Pair("Error", "Please enter pickup address")
            } else if (edtDropOffAddress.text.toString().isEmpty()) {
                return Pair("Error", "Please enter dropoff address")
            } else if (edtPrice.text.toString().isEmpty()) {
                return Pair("Error", "Please enter price")
            } else if (edtTime.text.toString().isEmpty()) {
                return Pair("Error", "Please enter pickup time")
            } else if (edtTime.text.toString()
                    .isNotEmpty() && calendar.timeInMillis < System.currentTimeMillis()
            ) {
                return Pair("Error", "Please select future pickup time")
            } else {
                val customerName: String = edtCustomerName.text.toString()
                val pickUpAddress: String = edtPickupAddress.text.toString()
                val dropOffAddress: String = edtDropOffAddress.text.toString()
                val price: String = edtPrice.text.toString()
                val pickTime = Date(calendar.timeInMillis)

                val trip = Trip(
                    id = null,
                    customerName,
                    pickUpAddress,
                    dropOffAddress,
                    price = price.toDouble(),
                    pickTime
                )

                return Pair("Trip added", trip)
            }
        }
    }

    private fun insertTrip(trip: Trip, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            tripViewModel.insertTrip(trip)
            withContext(Dispatchers.Main) {
                //do action here
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                clearForm()
            }
        }
    }

    //Read all trips
    private fun readTrips() {
        CoroutineScope(Dispatchers.Main).launch {
            tripViewModel.tripCount.observe(viewLifecycleOwner) { trips ->
                //do action here, and now you have data in list (notes)
                binding.toolbar.title = "Enter Trip ${trips + 1}"
                handleNextAndRemoveBtnVisibility(trips)
            }
        }
    }

    private fun handleNextAndRemoveBtnVisibility(trips: Int) {
        if (trips > 0) {
            if (trips > 4) {
                showNextButton()
            }
            handleRemoveAllButton(true)
        } else {
            handleRemoveAllButton(false)
        }
    }

    private fun handleRemoveAllButton(show: Boolean) {
        binding.apply {
            if (show) {
                btnRemoveAll.visibility = View.VISIBLE
            } else {
                btnRemoveAll.visibility = View.GONE
            }

            val params = guideline.layoutParams as ConstraintLayout.LayoutParams
            params.guidePercent = if (btnNext.visibility == View.VISIBLE) 0.5f else 1f
            guideline.layoutParams = params
        }
    }

    private fun showNextButton() {
        binding.apply {
            btnNext.visibility = View.VISIBLE
            val params = guideline.layoutParams as ConstraintLayout.LayoutParams
            params.guidePercent = 0.5f
            guideline.layoutParams = params
        }
    }

    //Remove all trips
    private fun removeAll() {
        CoroutineScope(Dispatchers.IO).launch {
            tripViewModel.removeAllTrips()
            withContext(Dispatchers.Main) {
                // Update UI on the main thread
                binding.btnNext.visibility = View.GONE
                Toast.makeText(context, "All trips removed", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun showDateTimePicker() {
        // Date Picker Dialog
        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                { _, year, month, dayOfMonth ->
                    // Set selected date to calendar
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // Time Picker Dialog
                    val timePickerDialog = TimePickerDialog(
                        it,
                        { _: TimePicker?, hourOfDay: Int, minute: Int ->
                            // Set selected time to calendar
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)

                            // Update EditText with selected date and time
                            updateDateTimeEditText()
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    )

                    // Show Time Picker Dialog
                    timePickerDialog.show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        // Set minimum date to current date
        datePickerDialog?.datePicker?.minDate = System.currentTimeMillis()

        // Show Date Picker Dialog
        datePickerDialog?.show()
    }

    private fun updateDateTimeEditText() {
        // Update EditText with selected date and time
        binding.edtTime.setText(calendar.time.formatToString(Constants.yyyyMMddHHmm))
    }
}
