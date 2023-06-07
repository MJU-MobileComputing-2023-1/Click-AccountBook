package com.example.click_accountbook.ui.dashboard

import StatisticsViewModelFactory
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.click_accountbook.DatabaseHandler
import com.example.click_accountbook.R
import com.example.click_accountbook.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var statisticsViewModel: StatisticsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val dbHandler = DatabaseHandler(requireContext()) // Here create or retrieve your DBHandler instance as per your application design

        val viewModel: StatisticsViewModel by viewModels { StatisticsViewModelFactory(dbHandler) }
        statisticsViewModel = viewModel

        statisticsViewModel.lineData.observe(viewLifecycleOwner, { lineDataList ->
            Log.d("StatisticsFragment", "Observed line data list size: ${lineDataList.size}")

            // Creating LineDataSet from the List<Entry>
            val lineDataSet = LineDataSet(lineDataList, "Total Price")

            // Creating LineData from the LineDataSet
            val lineData = LineData(lineDataSet)


            //x축 label(수정 필요)
            val xAxis = binding.lineChart.xAxis
            xAxis.valueFormatter = object : ValueFormatter() {
                private val dateFormat = SimpleDateFormat("yyyy. MM.dd", Locale.getDefault())


                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    val millis = value.toLong()
                    return dateFormat.format(Date(millis))   // 수정된 부분: 문자열 날짜를 반환합니다.
                }
            }
            xAxis.setDrawLabels(true) // x축 label보이게 (날짜)

            binding.lineChart.data = lineData

            // Refresh the chart
            binding.lineChart.invalidate()
        })


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
