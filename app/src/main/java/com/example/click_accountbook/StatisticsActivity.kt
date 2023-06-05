package com.example.click_accountbook
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.HorizontalScrollView
import com.example.click_accountbook.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class StatisticsActivity : AppCompatActivity() {

    data class CommitData(val date: String, val commitNum: Int)

    //데이터 생성
    val dataList: List<CommitData> = listOf(
        CommitData("08-28",3),
        CommitData("08-29",2),
        CommitData("08-30",5),
        CommitData("08-31",2),
        CommitData("09-01",3),
        CommitData("09-02",6),
        CommitData("09-03",7),
        CommitData("09-04",1),
        CommitData("09-05",3),
        CommitData("09-06",2)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linechart = findViewById<LineChart>(R.id.line_chart)
        val xAxis = linechart.xAxis

        //데이터 가공
        //y축
        val entries: MutableList<Entry> = mutableListOf()
        for (i in dataList.indices){
            entries.add(Entry(i.toFloat(), dataList[i].commitNum.toFloat()))
        }
        val lineDataSet =LineDataSet(entries,"entries")

        lineDataSet.apply {
            color = resources.getColor(R.color.black, null)
            circleRadius = 5f
            lineWidth = 3f
            setCircleColor(resources.getColor(R.color.purple_700, null))
            circleHoleColor = resources.getColor(R.color.purple_700, null)
            setDrawHighlightIndicators(false)
            setDrawValues(true) // 숫자표시
            valueTextColor = resources.getColor(R.color.black, null)
            valueFormatter = DefaultValueFormatter(0)  // 소숫점 자릿수 설정
            valueTextSize = 10f
        }

        //차트 전체 설정
        linechart.apply {
            axisRight.isEnabled = false   //y축 사용여부
            axisLeft.isEnabled = false
            legend.isEnabled = false    //legend 사용여부
            description.isEnabled = false //주석
            isDragXEnabled = true   // x 축 드래그 여부
            isScaleYEnabled = false //y축 줌 사용여부
            isScaleXEnabled = false //x축 줌 사용여부
        }

        //X축 설정
        xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(true)
            setDrawLabels(true)
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = XAxisCustomFormatter(changeDateText(dataList))
            textColor = resources.getColor(R.color.black, null)
            textSize = 10f
            labelRotationAngle = 0f
            setLabelCount(10, true)
        }

        val horizontalScrollView = findViewById<HorizontalScrollView>(R.id.horizontal_scroll_view)
        horizontalScrollView.post{
            horizontalScrollView.scrollTo(
                linechart.width,
                0
            )
        }

        linechart.apply {
            data = LineData(lineDataSet)
            notifyDataSetChanged() //데이터 갱신
            invalidate() // view갱신
        }

    }

    fun changeDateText(dataList: List<CommitData>): List<String> {
        val dataTextList = ArrayList<String>()
        for (i in dataList.indices) {
            val textSize = dataList[i].date.length
            val dateText = dataList[i].date.substring(textSize - 2, textSize)
            if (dateText == "01") {
                dataTextList.add(dataList[i].date)
            } else {
                dataTextList.add(dateText)
            }
        }
        return dataTextList
    }

    class XAxisCustomFormatter(val xAxisData: List<String>) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return xAxisData[(value).toInt()]
        }

    }


}