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
    val lineData: LiveData<List<Entry>> = _lineData

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val receiptList = dbHandler.getAllReceipts()
            Log.d("StatisticsViewModel", "Loaded receipt list size: ${receiptList.size}")
            val receiptDataList = receiptList.map {
                val formattedPaymentDate = it.paymentDate.replace(".", ". ")
                ReceiptData(formattedPaymentDate, it.totalPrice)
            }
            _receiptData.value = receiptDataList

            val lineDataList = receiptDataList.mapNotNull {
                try {
                    val date = SimpleDateFormat("yyyy. MM.dd", Locale.getDefault()).parse(it.paymentDate)
                    Log.d("StatisticsViewModel", "Parsed date in milliseconds: ${date.time}")
                    val entry = Entry(date.time.toFloat(), it.totalPrice)
                    Log.d(
                        "StatisticsViewModel",
                        "Line chart entry: Date: ${date}, Total Price: ${it.totalPrice}"
                    )
                    entry
                } catch (e: ParseException) {
                    // Invalid date format, skip this entry
                    Log.e(
                        "StatisticsViewModel", "Invalid date format: ${it.paymentDate}"
                    ) //unknown으로 인식되면 예외 처리로 다음 값들로 넘어가게 함
                    // .

                    null
                }
            }
            _lineData.value = lineDataList
        }
    }
}
