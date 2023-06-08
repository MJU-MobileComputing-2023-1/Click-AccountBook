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
    val totalPrice: Float //그래프에 쓸 data들
)

class StatisticsViewModel(private val dbHandler: DatabaseHandler) : ViewModel() {
    private val _receiptData = MutableLiveData<List<ReceiptData>>()
    val receiptData: LiveData<List<ReceiptData>> = _receiptData

    private val _lineData = MutableLiveData<List<Entry>>()
    val lineData: LiveData<List<Entry>> get() = _lineData

    private val _dates = MutableLiveData<List<Date>>()
    val dates: LiveData<List<Date>> get() = _dates

    init {
        loadData()      //ViewModel이 초기화될 때 데이터베이스에서 데이터 로드

    }
    // 데이터베이스에서 데이터를 로드하고 필요한 형식으로 변환
    private fun loadData() = viewModelScope.launch {
        val receiptList = dbHandler.getAllReceipts().sortedBy { tryParseDate(it.paymentDate)?.time }

        Log.d("StatisticsViewModel", "receipt list size: ${receiptList.size}")

        // 영수증 리스트를 ReceiptData의 리스트로 변환
        val receiptDataList = receiptList.map { ReceiptData(formatPaymentDate(it.paymentDate), it.totalPrice) }
        _receiptData.value = receiptDataList

        val datesList = receiptDataList.mapNotNull { tryParseDate(it.paymentDate) }
        _dates.value = datesList        // 영수증 데이터에서 날짜를 파싱하여 저장

        val lineDataList = datesList.mapIndexed { index, _ -> Entry(index.toFloat(), receiptDataList[index].totalPrice) }
        _lineData.value = lineDataList        // 날짜를 라인 차트에 사용될 Entries 리스트로 변환
    }
    private fun formatPaymentDate(date: String) = date.replace("-", ". ").replace(".", ". ")
    // 결제 날짜 문자열을 표시 형식으로 변환

    private fun tryParseDate(dateString: String): Date? {
        val formats = listOf("yyyy. MM. dd", "yyyy.MM.dd", "yyyy-MM-dd", "yy-MM-dd")
        // 여러 형식을 사용하여 날짜 문자열을 파싱하고 필요한 경우 연도를 수정

        for (format in formats) {
            try {
                val parsedDate = SimpleDateFormat(format, Locale.getDefault()).parse(dateString)
                return correctYearForDate(parsedDate)
            } catch (e: Exception) {}
        }
        return null
    }

    private fun correctYearForDate(date: Date?): Date? {
        return date?.let {    // 파싱된 날짜의 연도가 2000 미만인 경우 수정
            val calendar = Calendar.getInstance().apply { time = date }
            if (calendar.get(Calendar.YEAR) < 2000) {
                calendar.add(Calendar.YEAR, 2000)
                calendar.time
            } else {
                date
            }
        }
    }
}
