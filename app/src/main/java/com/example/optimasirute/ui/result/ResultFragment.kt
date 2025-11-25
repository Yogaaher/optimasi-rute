package com.example.optimasirute.ui.result

import android.animation.ObjectAnimator
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
import com.example.optimasirute.algorithm.OptimizationResult
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

        setupExpandableDetails()
    }

    private fun observeViewModel() {
        sharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.contentScrollView.isVisible = !isLoading
        }

        sharedViewModel.optimizationResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                updateResultView(result)
            }
        }
    }

    private fun updateResultView(result: OptimizationResult) {
        binding.itineraryContainer.removeAllViews()
        binding.cardDetails.isVisible = result.isValid

        if (result.isValid) {
            binding.tvTitle.text = "Jadwal Perjalanan Optimal"

            result.itinerary.forEach { item ->
                val itineraryView = createItineraryItemView(item)
                binding.itineraryContainer.addView(itineraryView)
            }

            val hours = result.totalMinutes / 60
            val minutes = result.totalMinutes % 60
            val totalPrice = result.itinerary.sumOf { it.placePrice }
            val executionSeconds = result.executionTimeMillis / 1000.0

            binding.tvExecutionTimeDetail.text =
                String.format(Locale.US, "Waktu Proses: %.2f detik", executionSeconds)
            binding.tvTotalTimeLabel.text = "Total Durasi & Harga Tiket"
            binding.tvTotalTimeResult.text =
                "$hours jam $minutes menit (${sharedViewModel.formatRupiah(totalPrice)})"

            binding.itineraryContainer.isVisible = true
            binding.tvTotalTimeLabel.isVisible = true
            binding.tvTotalTimeResult.isVisible = true

            binding.tvFitnessDetail.text =
                String.format(Locale.US, "Nilai Fitness Terbaik: %.8f", result.fitness)
            binding.tvGenerationDetail.text =
                "Keberagaman Akhir: ${result.finalDiversity} rute unik ditemukan"
            binding.tvPopulationDetail.text =
                "Diproses selama ${result.totalGenerations} generasi dengan ${result.finalPopulationCount} populasi"

        } else {
            binding.tvTitle.text = "Perencanaan Gagal"

            val errorTextView = TextView(requireContext()).apply {
                text =
                    "Tidak ditemukan rute yang memungkinkan dengan waktu mulai dan pilihan wisata Anda. Coba ubah waktu mulai ke lebih awal atau pilih kombinasi wisata yang berbeda."
                textSize = 16f
                setPadding(0, 16, 0, 0)
            }
            binding.itineraryContainer.addView(errorTextView)

            binding.itineraryContainer.isVisible = true
            binding.tvTotalTimeLabel.isVisible = false
            binding.tvTotalTimeResult.isVisible = false
        }
    }

    private fun setupExpandableDetails() {
        binding.detailsHeader.setOnClickListener {
            val content = binding.detailsContent
            val arrow = binding.arrowIcon

            if (content.isVisible) {
                content.isVisible = false
                ObjectAnimator.ofFloat(arrow, "rotation", 180f, 0f).setDuration(300).start()
            } else {
                content.isVisible = true
                ObjectAnimator.ofFloat(arrow, "rotation", 0f, 180f).setDuration(300).start()
            }
        }
    }

    private fun createItineraryItemView(item: ItineraryItem): View {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.item_itinerary, binding.itineraryContainer, false)

        val title: TextView = view.findViewById(R.id.tv_place_name)
        val price: TextView = view.findViewById(R.id.tv_place_price)
        val schedule: TextView = view.findViewById(R.id.tv_schedule)
        val travelInfo: TextView = view.findViewById(R.id.tv_travel_info)

        title.text = item.placeName

        price.text = if (item.placePrice > 0) {
            "Harga Tiket: ${sharedViewModel.formatRupiah(item.placePrice)}"
        } else {
            "Gratis"
        }

        var scheduleText = "Kunjungan: ${item.visitStartTime} - ${item.visitEndTime}"
        if (item.waitTimeMinutes > 0) {
            val waitHours = item.waitTimeMinutes / 60
            val waitMinutes = item.waitTimeMinutes % 60
            var waitText = ""
            if (waitHours > 0) waitText += "$waitHours jam "
            if (waitMinutes > 0) waitText += "$waitMinutes menit"
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