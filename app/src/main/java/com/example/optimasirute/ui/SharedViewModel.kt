package com.example.optimasirute.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.optimasirute.algorithm.GeneticAlgorithm
import com.example.optimasirute.algorithm.OptimizationResult
import com.example.optimasirute.data.dummy.WisataDummy
import com.example.optimasirute.data.model.Wisata
import java.text.NumberFormat
import java.util.Locale
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

    private val _isBudgetEnabled = MutableLiveData<Boolean>(false)
    val isBudgetEnabled: LiveData<Boolean> = _isBudgetEnabled

    private val _budget = MutableLiveData<Long>(0L)
    val budget: LiveData<Long> = _budget

    private val _currentTotalPrice = MutableLiveData<Int>(0)
    val currentTotalPrice: LiveData<Int> = _currentTotalPrice

    fun setBudgetEnabled(isEnabled: Boolean) {
        _isBudgetEnabled.value = isEnabled
    }

    fun setBudget(newBudget: Long) {
        _budget.value = newBudget
    }

    fun updateTotalPrice(newPrice: Int) {
        _currentTotalPrice.value = newPrice
    }

    fun setStartTime(hour: Int, minute: Int) {
        _startTimeInMinutes.value = hour * 60 + minute
    }

    fun runOptimization(selectedWisata: List<Wisata>, startTime: Int) {
        if (selectedWisata.isEmpty()) return

        _isLoading.value = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                val ga = GeneticAlgorithm(
                    selectedWisata = selectedWisata,
                    travelTimeMatrix = WisataDummy.waktuTempuh,
                    startTimeInMinutes = startTime
                )
                ga.run()
            }
            _optimizationResult.value = result
            _isLoading.value = false
        }
    }

    fun clearResult() {
        _optimizationResult.value = null
    }

    fun formatRupiah(number: Number): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0
        return numberFormat.format(number)
    }
}