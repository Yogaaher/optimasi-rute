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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.optimasirute.R
import com.example.optimasirute.data.dummy.WisataDummy
import com.example.optimasirute.data.model.Wisata
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
    private var selectedStartPoint: Wisata? = null

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

        // Setup UI Components
        setupExpandableLayout()
        setupRecyclerView()
        setupStartTimePicker()
        setupDatePicker()
        setupBudgetFeature()

        binding.btnProcess.setOnClickListener { processRoute() }
        observeViewModel()
    }

    // --- FUNGSI BARU UNTUK EXPAND/COLLAPSE ---
    private fun setupExpandableLayout() {
        binding.btnToggleInput.setOnClickListener {
            val isVisible = binding.layoutExpandableContent.visibility == View.VISIBLE
            if (isVisible) {
                // Logic untuk menyembunyikan (Collapse)
                binding.layoutExpandableContent.visibility = View.GONE
                // Rotasi panah ke samping (-90 derajat)
                binding.ivToggleArrow.animate().rotation(-90f).setDuration(200).start()
            } else {
                // Logic untuk menampilkan (Expand)
                binding.layoutExpandableContent.visibility = View.VISIBLE
                // Rotasi panah kembali ke bawah (0 derajat)
                binding.ivToggleArrow.animate().rotation(0f).setDuration(200).start()
            }
        }
    }

    private fun observeViewModel() {
        sharedViewModel.startTimeInMinutes.observe(viewLifecycleOwner) { minutes ->
            val hours = minutes / 60
            val mins = minutes % 60
            binding.tvStartTimeValue.text = String.format("%02d:%02d", hours, mins)
            wisataAdapter.updateCurrentTime(minutes)
        }

        sharedViewModel.selectedDate.observe(viewLifecycleOwner) { calendar ->
            // Format tanggal dipersingkat agar muat di card
            val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("in", "ID"))
            binding.tvDateValue.text = sdf.format(calendar.time)
            wisataAdapter.updateDayType(sharedViewModel.isWeekend())
            updateTotalPriceAndBudgetStatus()
        }

        sharedViewModel.isBudgetEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.layoutInputBudget.visibility = if (isEnabled) View.VISIBLE else View.GONE
            // Pastikan switch sync dengan state
            if (binding.switchBudget.isChecked != isEnabled) {
                binding.switchBudget.isChecked = isEnabled
            }

            if (!isEnabled) {
                binding.inputBudget.text = null
                sharedViewModel.setBudget(0L)
            }
            updateAdapterBasedOnBudget()
        }

        sharedViewModel.budget.observe(viewLifecycleOwner) { updateAdapterBasedOnBudget() }

        sharedViewModel.currentTotalPrice.observe(viewLifecycleOwner) { price ->
            // Update teks total harga di header list (sebelah kanan)
            binding.tvTotalPrice.text = "Total: ${sharedViewModel.formatRupiah(price)}"
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
        binding.switchBudget.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.setBudgetEnabled(isChecked)
        }

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
            updateStartPointDropdown()
        }
        binding.rvWisata.apply {
            adapter = wisataAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
        updateTotalPriceAndBudgetStatus()
        updateStartPointDropdown()
    }

    private fun updateStartPointDropdown() {
        val selectedWisata = wisataAdapter.getSelectedWisata()
        val hasSelection = selectedWisata.isNotEmpty()

        // Start point ada di dalam layout expandable, tetap diupdate meski hidden
        binding.layoutStartPoint.isEnabled = hasSelection

        if (hasSelection) {
            binding.dropdownStartPoint.setText(selectedStartPoint?.nama ?: "Akan dipilihkan otomatis", false)
        } else {
            binding.dropdownStartPoint.setText("Pilih wisata terlebih dahulu", false)
            selectedStartPoint = null
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            selectedWisata.map { it.nama }
        )
        binding.dropdownStartPoint.setAdapter(adapter)

        binding.dropdownStartPoint.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            selectedStartPoint = selectedWisata.find { it.nama == selectedName }
        }

        if (selectedStartPoint != null && !selectedWisata.contains(selectedStartPoint)) {
            selectedStartPoint = null
            binding.dropdownStartPoint.setText("Akan dipilihkan otomatis", false)
        }
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
            // Hapus item terakhir jika melebihi budget
            while (currentTotal > budget && wisataAdapter.getSelectedWisata().isNotEmpty()) {
                wisataAdapter.removeLastSelectedItem()
                // Update recycler view manual agar checkbox ter-uncheck
                binding.rvWisata.adapter?.notifyDataSetChanged()

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
        sharedViewModel.runOptimization(selectedWisata, startTime, selectedStartPoint)
        findNavController().navigate(R.id.action_navigation_input_to_navigation_result)
    }

    override fun onDestroyView() {
        debounceHandler.removeCallbacks(budgetWorkRunnable)
        super.onDestroyView()
        _binding = null
    }
}