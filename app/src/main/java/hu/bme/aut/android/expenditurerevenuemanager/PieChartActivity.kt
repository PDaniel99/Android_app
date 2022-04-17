package hu.bme.aut.android.expenditurerevenuemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionItemCategory
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionListDatabase
import hu.bme.aut.android.expenditurerevenuemanager.databinding.ActivityPieChartBinding
import java.text.SimpleDateFormat

class PieChartActivity : AppCompatActivity() {

    lateinit var binding: ActivityPieChartBinding

    private lateinit var database: TransactionListDatabase

    companion object {
        const val KEY_ID = "KEY_ID"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPieChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = TransactionListDatabase.getInstance(this@PieChartActivity)
        val dbThread = Thread {

            //Ebben a sorban veszem át az adatot az intent.getLongExtra metódussal
            val item = database.transactionItemDao().getItemById(intent.getLongExtra(KEY_ID, 0)!!)

            val sumOfRevenues = database.transactionItemDao().getSumRev()
            val sumOfExpenditures = database.transactionItemDao().getSumExp()
            runOnUiThread {
                binding.tvPartnerChart.text = item.partner
                binding.tvdescriptionChart.text = item.description
                val value = item.value
                binding.tvValueChart.text = "${item.value.toString()} Ft"
                val type = TransactionItemCategory.getByOrdinal(item.category.ordinal)
                binding.tvTypeChart.text = type.toString()
                binding.tvDateChart.text = SimpleDateFormat("yyyy-MM-dd").format(item.date)

                loadRatioPieChart(sumOfRevenues, sumOfExpenditures, value, type)
            }
        }
        dbThread.start()
    }

    private fun loadRatioPieChart(sumOfRevenues: Long, sumOfExpenditures: Long, value: Long, type: TransactionItemCategory?) {

        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(value.toFloat(), "Observed ${type.toString()}"))
        if (type == TransactionItemCategory.REVENUE) {
            entries.add(PieEntry((sumOfRevenues - value).toFloat(), getString(R.string.sumOfRev)))
        } else {
            entries.add(PieEntry(sumOfExpenditures.toFloat(), getString(R.string.sumOfExp)))
        }

        val dataSet = PieDataSet(entries, getString(R.string.ratio))
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        dataSet.valueTextSize = 16f

        val data = PieData(dataSet)
        binding.piechartTransaction.setEntryLabelTextSize(16f)
        binding.piechartTransaction.setEntryLabelColor(R.color.black)
        binding.piechartTransaction.data = data
        binding.piechartTransaction.invalidate()

    }
}