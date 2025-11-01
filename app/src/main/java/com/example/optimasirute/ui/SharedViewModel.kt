package com.example.optimasirute.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.optimasirute.algorithm.EvolutionStrategies
import com.example.optimasirute.algorithm.OptimizationResult
import com.example.optimasirute.data.dummy.WisataDummy
import com.example.optimasirute.data.model.Wisata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedViewModel : ViewModel() {

    private val _optimizationResult = MutableLiveData<OptimizationResult?>()
    val optimizationResult: LiveData<OptimizationResult?> = _optimizationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _startTimeInMinutes = MutableLiveData<Int>(480) // Default 08:00
    val startTimeInMinutes: LiveData<Int> = _startTimeInMinutes

    fun setStartTime(hour: Int, minute: Int) {
        _startTimeInMinutes.value = hour * 60 + minute
    }

    fun runOptimization(selectedWisata: List<Wisata>, startTime: Int) {
        if (selectedWisata.isEmpty()) return

        _isLoading.value = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                val es = EvolutionStrategies(
                    selectedWisata = selectedWisata,
                    travelTimeMatrix = WisataDummy.waktuTempuh,
                    startTimeInMinutes = startTime
                )
                es.run()
            }
            _optimizationResult.value = result
            _isLoading.value = false
        }
    }

    fun clearResult() {
        _optimizationResult.value = null
    }
}