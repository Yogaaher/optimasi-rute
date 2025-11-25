package com.example.optimasirute.ui.input

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.optimasirute.R
import com.example.optimasirute.data.dummy.WisataDummy
import com.example.optimasirute.databinding.FragmentInputBinding
import com.example.optimasirute.ui.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InputFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var wisataAdapter: WisataAdapter

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var budgetWorkRunnable = Runnable {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupStartTimePicker()
        setupDatePicker()
        setupBudgetFeature()
        binding.btnProcess.setOnClickListener { processRoute() }
        observeViewModel()
    }

    private fun observeViewModel() {
        sharedViewModel.startTimeInMinutes.observe(viewLifecycleOwner) { minutes ->
            val hours = minutes / 60
            val mins = minutes % 60
            binding.tvStartTimeValue.text = String.format("%02d:%02d", hours, mins)
            wisataAdapter.updateCurrentTime(minutes)
        }

        sharedViewModel.selectedDate.observe(viewLifecycleOwner) { calendar ->
            val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("in", "ID"))
            binding.tvDateValue.text = sdf.format(calendar.time)
            wisataAdapter.updateDayType(sharedViewModel.isWeekend())
            updateTotalPriceAndBudgetStatus()
        }

        sharedViewModel.isBudgetEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.layoutInputBudget.visibility = if (isEnabled) View.VISIBLE else View.GONE
            if (!isEnabled) {
                binding.inputBudget.text = null
                sharedViewModel.setBudget(0L)
            }
            updateAdapterBasedOnBudget()
        }

        sharedViewModel.budget.observe(viewLifecycleOwner) { updateAdapterBasedOnBudget() }

        sharedViewModel.currentTotalPrice.observe(viewLifecycleOwner) { price ->
            binding.tvTotalPrice.text = "Total Harga: ${sharedViewModel.formatRupiah(price)}"
        }
    }

    private fun setupStartTimePicker() {
        binding.cardStartTime.setOnClickListener {
            val currentTime = sharedViewModel.startTimeInMinutes.value ?: 480
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute -> validateAndSetStartTime(hourOfDay, minute) },
                currentTime / 60,
                currentTime % 60,
                true
            ).show()
        }
    }

    private fun setupDatePicker() {
        binding.cardDate.setOnClickListener {
            val calendar = sharedViewModel.selectedDate.value ?: Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth -> sharedViewModel.setSelectedDate(year, month, dayOfMonth) },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()

            datePickerDialog.show()
        }
    }

    private fun validateAndSetStartTime(hour: Int, minute: Int) {
        val newStartTimeInMinutes = hour * 60 + minute
        if (WisataDummy.daftarWisata.all { newStartTimeInMinutes >= it.tutup }) {
            AlertDialog.Builder(requireContext())
                .setTitle("Waktu Mulai Tidak Valid")
                .setMessage("Waktu mulai yang Anda pilih sudah melewati jam operasional semua tempat wisata.")
                .setPositiveButton("OK", null)
                .show()
        } else {
            sharedViewModel.setStartTime(hour, minute)
        }
    }

    private fun setupBudgetFeature() {
        binding.switchBudget.setOnCheckedChangeListener { _, isChecked -> sharedViewModel.setBudgetEnabled(isChecked) }

        binding.inputBudget.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                debounceHandler.removeCallbacks(budgetWorkRunnable)
                budgetWorkRunnable = Runnable {
                    sharedViewModel.setBudget(s.toString().toLongOrNull() ?: 0L)
                }
                debounceHandler.postDelayed(budgetWorkRunnable, 1000)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupRecyclerView() {
        wisataAdapter = WisataAdapter(WisataDummy.daftarWisata, emptySet()) {
            updateTotalPriceAndBudgetStatus()
        }
        binding.rvWisata.apply {
            adapter = wisataAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
        updateTotalPriceAndBudgetStatus()
    }

    private fun updateTotalPriceAndBudgetStatus() {
        val selected = wisataAdapter.getSelectedWisata()
        val totalPrice = selected.sumOf { wisataAdapter.getCurrentPrice(it) }
        sharedViewModel.updateTotalPrice(totalPrice)
        updateAdapterBasedOnBudget()
    }

    private fun updateAdapterBasedOnBudget() {
        val isEnabled = sharedViewModel.isBudgetEnabled.value ?: false
        val budget = sharedViewModel.budget.value ?: 0L

        if (isEnabled && budget > 0) {
            var currentTotal = wisataAdapter.getSelectedWisata().sumOf {
                wisataAdapter.getCurrentPrice(it).toLong()
            }
            while (currentTotal > budget && wisataAdapter.getSelectedWisata().isNotEmpty()) {
                wisataAdapter.removeLastSelectedItem()
                (binding.rvWisata.adapter as? WisataAdapter)?.notifyDataSetChanged()
                currentTotal = wisataAdapter.getSelectedWisata().sumOf {
                    wisataAdapter.getCurrentPrice(it).toLong()
                }
                sharedViewModel.updateTotalPrice(currentTotal.toInt())
                Toast.makeText(requireContext(), "Pilihan terakhir dihapus karena melebihi budget", Toast.LENGTH_SHORT).show()
            }
        }
        val isBudgetFeatureActive = isEnabled && budget > 0
        wisataAdapter.updateBudgetAndAvailability(budget, isBudgetFeatureActive)
    }

    private fun processRoute() {
        val selectedWisata = wisataAdapter.getSelectedWisata()
        if (selectedWisata.size < 2) {
            Toast.makeText(requireContext(), "Pilih minimal 2 tempat wisata", Toast.LENGTH_SHORT).show()
            return
        }
        val startTime = sharedViewModel.startTimeInMinutes.value ?: 480
        sharedViewModel.runOptimization(selectedWisata, startTime)
        findNavController().navigate(R.id.action_navigation_input_to_navigation_result)
    }

    override fun onDestroyView() {
        debounceHandler.removeCallbacks(budgetWorkRunnable)
        super.onDestroyView()
        _binding = null
    }
}