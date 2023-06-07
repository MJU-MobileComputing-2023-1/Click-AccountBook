package com.example.click_accountbook.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.click_accountbook.DatabaseHandler
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import java.text.ParseException

data class ReceiptData(
    val paymentDate: String,
    val totalPrice: Float
)

class StatisticsViewModel(private val dbHandler: DatabaseHandler) : ViewModel() {
    private val _receiptData = MutableLiveData<List<ReceiptData>>()
    val receiptData: LiveData<List<ReceiptData>> = _receiptData

    // Create LiveData for List<Entry> for the LineChart
    private val _lineData = MutableLiveData<List<Entry>>()
    val lineData: LiveData<List<Entry>> get() = _lineData
    private val _dates = MutableLiveData<List<Date>>()
    val dates: LiveData<List<Date>> get() = _dates

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val receiptList = dbHandler.getAllReceipts()
            Log.d("StatisticsViewModel", "Loaded receipt list size: ${receiptList.size}")
            val receiptDataList = receiptList.map {
                val formattedPaymentDate = it.paymentDate.replace("-", ". ").replace(".", ". ")
                ReceiptData(formattedPaymentDate, it.totalPrice)
            }
            _receiptData.value = receiptDataList

            // Create a list of dates from the receipt data
            val datesList = _receiptData.value?.mapNotNull { receiptData ->
                tryParseDate(receiptData.paymentDate)?.also { date ->
                    Log.d("StatisticsViewModel", "Parsed date in milliseconds: ${date.time}")
                    Log.d(
                        "StatisticsViewModel",
                        "Line chart entry: Date: $date, Total Price: ${receiptData.totalPrice}"
                    )
                }
            } ?: emptyList()

            _dates.value = datesList

            // Replace date.time with index.toFloat()
            val lineDataList = datesList.indices.map { index ->
                Entry(index.toFloat(), _receiptData.value?.get(index)?.totalPrice ?: 0f)
            }
            _lineData.value = lineDataList
        }
    }


    private fun tryParseDate(dateString: String): Date? {
        val formats = listOf("yyyy. MM. dd", "yyyy.MM.dd", "yyyy-MM-dd")

        for (format in formats) {
            try {
                return SimpleDateFormat(format, Locale.getDefault()).parse(dateString)
            } catch (e: ParseException) {
            }
        }

        return null
    }
}

