package com.example.click_accountbook.ui.dashboard

import StatisticsViewModelFactory
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var statisticsViewModel: StatisticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val dbHandler = DatabaseHandler(requireContext())

        val viewModel: StatisticsViewModel by viewModels { StatisticsViewModelFactory(dbHandler) }
        statisticsViewModel = viewModel

        statisticsViewModel.dates.observe(viewLifecycleOwner, { dates ->
            val xAxis = binding.lineChart.xAxis
            xAxis.valueFormatter = object : ValueFormatter() {
                private val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    val dateIndex = value.toInt()
                    if (dateIndex in dates.indices) {
                        val date = dates[dateIndex]
                        return dateFormat.format(date)
                    }
                    return ""
                }
            }
            xAxis.setDrawLabels(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
        })

        statisticsViewModel.lineData.observe(viewLifecycleOwner, { lineDataList ->
            Log.d("StatisticsFragment", "Observed line data list size: ${lineDataList.size}")

            val lineDataSet = LineDataSet(lineDataList, "Total Price")
            lineDataSet.color = Color.rgb(0, 0, 139)
            lineDataSet.valueTextSize=20f
            lineDataSet.lineWidth=3f
            lineDataSet.setDrawCircles(true)
            lineDataSet.setCircleColor(Color.BLUE)
            lineDataSet.setDrawValues(true)
            val lineData = LineData(lineDataSet)

            binding.lineChart.data = lineData

            binding.lineChart.invalidate()
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}