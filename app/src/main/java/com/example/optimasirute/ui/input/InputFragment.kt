package com.example.optimasirute.ui.input

import android.app.TimePickerDialog
import android.os.Bundle
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
import com.example.optimasirute.data.local.SelectionPreferences
import com.example.optimasirute.databinding.FragmentInputBinding
import com.example.optimasirute.ui.SharedViewModel

class InputFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var wisataAdapter: WisataAdapter
    private lateinit var selectionPrefs: SelectionPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectionPrefs = SelectionPreferences(requireContext())
        setupRecyclerView()
        setupStartTimePicker()

        binding.btnProcess.setOnClickListener {
            val selectedIds = wisataAdapter.getSelectedWisata().map { it.nama }.toSet()
            selectionPrefs.saveSelectedIds(selectedIds)
            processRoute()
        }

        sharedViewModel.startTimeInMinutes.observe(viewLifecycleOwner) { minutes ->
            val hours = minutes / 60
            val mins = minutes % 60
            binding.tvStartTimeValue.text = String.format("%02d:%02d", hours, mins)

            // --- LOGIKA FEEDBACK VISUAL ---
            // Setiap kali waktu berubah, perbarui adapter
            wisataAdapter.updateCurrentTime(minutes)
            // ---------------------------------
        }
    }

    private fun setupStartTimePicker() {
        binding.cardStartTime.setOnClickListener {
            val currentTime = sharedViewModel.startTimeInMinutes.value ?: 480
            val currentHour = currentTime / 60
            val currentMinute = currentTime % 60

            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    // --- LOGIKA VALIDASI PROAKTIF ---
                    validateAndSetStartTime(hourOfDay, minute)
                    // --------------------------------
                },
                currentHour,
                currentMinute,
                true
            ).show()
        }
    }

    private fun validateAndSetStartTime(hour: Int, minute: Int) {
        val newStartTimeInMinutes = hour * 60 + minute
        val allWisata = WisataDummy.daftarWisata

        // Cek apakah waktu mulai yang baru ini sudah melewati jam tutup SEMUA tempat wisata
        val isTooLateForAll = allWisata.all { newStartTimeInMinutes >= it.tutup }

        if (isTooLateForAll) {
            // Tampilkan dialog peringatan
            AlertDialog.Builder(requireContext())
                .setTitle("Waktu Mulai Tidak Valid")
                .setMessage("Waktu mulai yang Anda pilih sudah melewati jam operasional semua tempat wisata. Silakan pilih waktu yang lebih awal.")
                .setPositiveButton("OK", null)
                .show()
        } else {
            // Jika valid, baru perbarui ViewModel
            sharedViewModel.setStartTime(hour, minute)
        }
    }

    private fun setupRecyclerView() {
        val savedIds = selectionPrefs.getSelectedIds()
        wisataAdapter = WisataAdapter(WisataDummy.daftarWisata, savedIds)
        binding.rvWisata.apply {
            adapter = wisataAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
            )
        }
    }

    private fun processRoute() {
        // ... (kode ini tetap sama)
        val selectedWisata = wisataAdapter.getSelectedWisata()

        if (selectedWisata.size < 2) {
            Toast.makeText(context, "Pilih minimal 2 tempat wisata", Toast.LENGTH_SHORT).show()
            return
        }

        val startTime = sharedViewModel.startTimeInMinutes.value ?: 480
        sharedViewModel.runOptimization(selectedWisata, startTime)
        findNavController().navigate(R.id.action_navigation_input_to_navigation_result)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}