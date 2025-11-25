package com.example.optimasirute.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.button.MaterialButton
import com.example.optimasirute.R
import com.example.optimasirute.databinding.FragmentHomeBinding
import com.example.optimasirute.ui.SharedViewModel
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_input)
        }

        binding.btnPaketSejarah.setOnClickListener {
            showDayTypeDialog("SEJARAH")
        }

        binding.btnPaketAlam.setOnClickListener {
            showDayTypeDialog("ALAM_KELUARGA")
        }
    }

        private fun showDayTypeDialog(packageId: String) {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_day_type, null)

            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create()

            val btnWeekday = dialogView.findViewById<MaterialButton>(R.id.btn_weekday)
            val btnWeekend = dialogView.findViewById<MaterialButton>(R.id.btn_weekend)

            btnWeekday.setOnClickListener {
                setFutureDateAndRunPackage(packageId, isWeekend = false)
                dialog.dismiss()
            }

            btnWeekend.setOnClickListener {
                setFutureDateAndRunPackage(packageId, isWeekend = true)
                dialog.dismiss()
            }
            dialog.show()
        }


        private fun setFutureDateAndRunPackage(packageId: String, isWeekend: Boolean) {
        val calendar = Calendar.getInstance()

        if (isWeekend) {
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        } else {
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, 2)
            }
        }

        sharedViewModel.setSelectedDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        sharedViewModel.runQuickPackage(packageId)
        findNavController().navigate(R.id.action_navigation_input_to_navigation_result)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}