package hu.bme.aut.android.expenditurerevenuemanager

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionItem
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionListDatabase
import hu.bme.aut.android.expenditurerevenuemanager.databinding.ActivityStatisticsBinding


class StatisticsActivity() : AppCompatActivity(){

    lateinit var binding: ActivityStatisticsBinding

    private lateinit var database: TransactionListDatabase

    private lateinit var barChartExp: BarChart

    private lateinit var barChartRev: BarChart

    private var top5expenditures = ArrayList<TransactionItem>()

    private var top5revenues = ArrayList<TransactionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        barChartExp = binding.barChart
        barChartRev = binding.barChartRev
        database = TransactionListDatabase.getInstance(this@StatisticsActivity)
        val dbThread = Thread {
            val numOfTransactions = database.transactionItemDao().getNum()
            val numOfRevenues = database.transactionItemDao().getNumRev()
            val sumOfRevenues = database.transactionItemDao().getSumRev()
            val sumOfExpenditures = database.transactionItemDao().getSumExp()

            val top5expenditures = database.transactionItemDao().getTop5Exp()
            for (i in top5expenditures) {
                this.top5expenditures.add(i) }

            val top5revenues = database.transactionItemDao().getTop5Rev()
            for (i in top5revenues) {
                this.top5revenues.add(i) }

            val dataExp = assembleBarChart(this.top5expenditures)
            val dataRev = assembleBarChart(this.top5revenues)

            runOnUiThread {
                binding.tvNumberOfTransactions.text = numOfTransactions.toString()
                binding.tvNumberOfExpenditures.text = (numOfTransactions - numOfRevenues).toString()
                binding.tvNumberOfRevenues.text = numOfRevenues.toString()
                binding.tvSumOfRevenues.text = "${sumOfRevenues.toString()} Ft"
                binding.tvSumOfExpenditures.text = "${sumOfExpenditures.toString()} Ft"

                initBarChart(barChartExp, this.top5expenditures)
                barChartExp.data = dataExp
                barChartExp.invalidate()

                initBarChart(barChartRev, this.top5revenues)
                barChartRev.data = dataRev
                barChartRev.invalidate()
            }
        }
        dbThread.start()
    }

    private fun assembleBarChart(list: ArrayList<TransactionItem>): BarData {
        //Az egyik adathalmazra

            //now draw bar chart with dynamic data
            val entries: ArrayList<BarEntry> = ArrayList()

            //you can replace this data object with  your custom object
            for (i in list.indices) {
                val item = list[i]
                entries.add(BarEntry(i.toFloat(), item.value.toFloat()))
            }

            val barDataSet = BarDataSet(entries, "")
            barDataSet.valueTextSize = 16f
            barDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)
            return BarData(barDataSet)
    }


    private fun initBarChart(barChart: BarChart, items: ArrayList<TransactionItem>) {

        //hide grid lines
        barChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //remove right y-axis
        barChart.axisRight.isEnabled = false

        //remove legend
        barChart.legend.isEnabled = false

        //remove description label
        barChart.description.isEnabled = false

        //add animation
        barChart.animateY(2000)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.valueFormatter = MyAxisFormatter(items)
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +0f

        //text size setting
        xAxis.textSize = 12f

    }

    inner class MyAxisFormatter(private val items: ArrayList<TransactionItem>) : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            Log.d(TAG, "getAxisLabel: index $index")
            return if (index < items.size) {
                items[index].partner
            } else {
                ""
            }
        }
    }
}