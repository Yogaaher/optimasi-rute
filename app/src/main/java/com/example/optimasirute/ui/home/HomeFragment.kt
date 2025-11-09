package com.example.optimasirute.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.optimasirute.R
import com.example.optimasirute.databinding.FragmentHomeBinding
import com.example.optimasirute.ui.SharedViewModel

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
            sharedViewModel.runQuickPackage("SEJARAH")
            findNavController().navigate(R.id.action_navigation_input_to_navigation_result)
        }

        binding.btnPaketAlam.setOnClickListener {
            sharedViewModel.runQuickPackage("ALAM_KELUARGA")
            findNavController().navigate(R.id.action_navigation_input_to_navigation_result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}