package com.example.optimasirute.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.optimasirute.R
import com.example.optimasirute.algorithm.ItineraryItem
import com.example.optimasirute.databinding.FragmentResultBinding
import com.example.optimasirute.ui.SharedViewModel
import java.util.Locale

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        binding.btnBackToInput.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        sharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.contentScrollView.isVisible = !isLoading
        }

        sharedViewModel.optimizationResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                // Hapus jadwal lama sebelum menampilkan yang baru
                binding.itineraryContainer.removeAllViews()

                // Buat dan tampilkan jadwal detail
                result.itinerary.forEach { item ->
                    val itineraryView = createItineraryItemView(item)
                    binding.itineraryContainer.addView(itineraryView)
                }

                // Tampilkan total waktu
                val hours = result.totalMinutes / 60
                val minutes = result.totalMinutes % 60
                binding.tvTotalTimeResult.text = "$hours jam $minutes menit"
            }
        }
    }

    // Fungsi baru untuk membuat satu baris jadwal secara dinamis
    private fun createItineraryItemView(item: ItineraryItem): View {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.item_itinerary, binding.itineraryContainer, false)

        val title: TextView = view.findViewById(R.id.tv_place_name)
        val schedule: TextView = view.findViewById(R.id.tv_schedule)
        val travelInfo: TextView = view.findViewById(R.id.tv_travel_info)

        title.text = item.placeName
        var scheduleText = "Kunjungan: ${item.visitStartTime} - ${item.visitEndTime}"
        if (item.waitTimeMinutes > 0) {
            val waitHours = item.waitTimeMinutes / 60
            val waitMinutes = item.waitTimeMinutes % 60

            var waitText = ""
            if (waitHours > 0) {
                waitText += "$waitHours jam "
            }
            if (waitMinutes > 0) {
                waitText += "$waitMinutes menit"
            }

            scheduleText += "\n(Tiba pukul ${item.arrivalTime}, Menunggu ${waitText.trim()})"
        }
        schedule.text = scheduleText

        if (item.travelToNextMinutes != null) {
            travelInfo.text = "Perjalanan ke lokasi selanjutnya: ${item.travelToNextMinutes} menit"
            travelInfo.isVisible = true
        } else {
            travelInfo.isVisible = false
        }

        return view
    }

    override fun onDestroyView() {
        sharedViewModel.clearResult()
        super.onDestroyView()
        _binding = null
    }
}