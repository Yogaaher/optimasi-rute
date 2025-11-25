package com.example.optimasirute.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.optimasirute.algorithm.GeneticAlgorithm
import com.example.optimasirute.algorithm.OptimizationResult
import com.example.optimasirute.data.dummy.WisataDummy
import com.example.optimasirute.data.model.Wisata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class SharedViewModel : ViewModel() {

    private val _optimizationResult = MutableLiveData<OptimizationResult?>()
    val optimizationResult: LiveData<OptimizationResult?> = _optimizationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _startTimeInMinutes = MutableLiveData<Int>(480)
    val startTimeInMinutes: LiveData<Int> = _startTimeInMinutes

    private val _selectedDate = MutableLiveData<Calendar>(Calendar.getInstance())
    val selectedDate: LiveData<Calendar> = _selectedDate

    private val _isBudgetEnabled = MutableLiveData<Boolean>(false)
    val isBudgetEnabled: LiveData<Boolean> = _isBudgetEnabled

    private val _budget = MutableLiveData<Long>(0L)
    val budget: LiveData<Long> = _budget

    private val _currentTotalPrice = MutableLiveData<Int>(0)
    val currentTotalPrice: LiveData<Int> = _currentTotalPrice

    fun setStartTime(hour: Int, minute: Int) {
        _startTimeInMinutes.value = hour * 60 + minute
    }

    fun setSelectedDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        _selectedDate.value = calendar
    }

    fun isWeekend(): Boolean {
        val calendar = _selectedDate.value ?: return false
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }

    fun setBudgetEnabled(isEnabled: Boolean) {
        _isBudgetEnabled.value = isEnabled
    }

    fun setBudget(newBudget: Long) {
        _budget.value = newBudget
    }

    fun updateTotalPrice(newPrice: Int) {
        _currentTotalPrice.value = newPrice
    }

    fun runOptimization(selectedWisata: List<Wisata>, startTime: Int) {
        if (selectedWisata.isEmpty()) return

        _isLoading.value = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                val ga = GeneticAlgorithm(
                    selectedWisata = selectedWisata,
                    travelTimeMatrix = WisataDummy.waktuTempuh,
                    startTimeInMinutes = startTime,
                    isWeekend = isWeekend()
                )
                ga.run()
            }
            _optimizationResult.value = result
            _isLoading.value = false
        }
    }

    fun runQuickPackage(packageId: String) {
        val allWisata = WisataDummy.daftarWisata
        val selectedPackage = WisataDummy.daftarPaket.find { it.id == packageId } ?: return
        val packageWisataList = allWisata.filter { selectedPackage.daftarNamaWisata.contains(it.nama) }
        if (packageWisataList.isEmpty()) return

        setStartTime(selectedPackage.defaultStartHour, selectedPackage.defaultStartMinute)
        val startTime = startTimeInMinutes.value ?: (selectedPackage.defaultStartHour * 60 + selectedPackage.defaultStartMinute)
        runOptimization(packageWisataList, startTime)
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