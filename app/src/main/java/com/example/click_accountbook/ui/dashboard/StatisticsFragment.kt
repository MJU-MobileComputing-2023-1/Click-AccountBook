package com.example.click_accountbook.ui.dashboard
import com.example.click_accountbook.ui.dashboard.StatisticsViewModel
import StatisticsViewModelFactory
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import com.example.click_accountbook.DatabaseHandler
import com.example.click_accountbook.R
import com.example.click_accountbook.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var statisticsViewModel: StatisticsViewModel //binding

    override fun onCreateView( //xml이 나타날때 호출되는 함수
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val dbHandler = DatabaseHandler(requireContext())

        val viewModel: StatisticsViewModel by viewModels { StatisticsViewModelFactory(dbHandler) }
        statisticsViewModel = viewModel         // 뷰 모델 초기화

        // dates라는 라이브 데이터를 구독하고, 데이터가 변경될 때마다 새로운 값을 차트의 x축에 설정
        statisticsViewModel.dates.observe(viewLifecycleOwner) { dates ->
            val xAxis = binding.lineChart.xAxis
            xAxis.valueFormatter = object : ValueFormatter() {
                private val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    val dateIndex = value.toInt()
                    if (dateIndex in dates.indices) {
                        val date = dates[dateIndex]
                        return dateFormat.format(date)
                    }
                    return "" }
            }
            xAxis.setDrawLabels(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM }

        // lineData라는 라이브 데이터를 구독하고, 데이터가 변경될 때마다 차트에 새로운 데이터를 설정
        statisticsViewModel.lineData.observe(viewLifecycleOwner) { lineDataList ->
            Log.d("StatisticsFragment", "Observed line data list size: ${lineDataList.size}")

            val lineDataSet = LineDataSet(lineDataList, "Total Price")
            lineDataSet.color = Color.rgb(0, 0, 139)
            lineDataSet.valueTextSize = 20f
            lineDataSet.lineWidth = 3f
            lineDataSet.setDrawCircles(true)
            lineDataSet.setCircleColor(Color.BLUE)
            lineDataSet.setDrawValues(true)
            val lineData = LineData(lineDataSet)

            binding.lineChart.data = lineData

            binding.lineChart.invalidate()
        }
        //receipt data 받아서 표로 생성.
        statisticsViewModel.receiptData.observe(viewLifecycleOwner) { receiptDataList ->
            val tableLayout = binding.receiptTable

            tableLayout.removeAllViews() //초기화

            // Create a new table row and add it to the table for each ReceiptData object
            receiptDataList.forEach { receiptData ->
                val row = TableRow(context).apply {
                    layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(1) }
                    setBackgroundResource(R.color.black) // 행 배경 색상 (테두리 색상) 설정
                    setPadding(5, 5, 5, 5) // 행에 패딩 추가
                }

                val paymentDateTextView = TextView(context).apply {
                    text = receiptData.paymentDate
                    setBackgroundResource(R.color.white) // 셀 배경 색상 설정
                    setPadding(5, 5, 5, 5) // 텍스트 뷰에 패딩 추가
                }
                row.addView(paymentDateTextView)

                val totalPriceTextView = TextView(context).apply {
                    text = receiptData.totalPrice.toString()
                    setBackgroundResource(R.color.white) // 셀 배경 색상 설정
                    setPadding(5, 5, 5, 5) // 텍스트 뷰에 패딩 추가
                }
                row.addView(totalPriceTextView)
                tableLayout.addView(row)
            }
        }

        return binding.root //최종적으로 생성된 뷰 return
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
