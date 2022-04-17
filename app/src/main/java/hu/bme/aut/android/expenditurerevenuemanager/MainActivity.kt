package hu.bme.aut.android.expenditurerevenuemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.expenditurerevenuemanager.adapter.TransactionAdapter
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionItem
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionListDatabase
import hu.bme.aut.android.expenditurerevenuemanager.databinding.ActivityMainBinding
import hu.bme.aut.android.expenditurerevenuemanager.fragments.EditTransactionItemDialogFragment
import hu.bme.aut.android.expenditurerevenuemanager.fragments.NewTransactionItemDialogFragment
import kotlinx.coroutines.*

class MainActivity :
    AppCompatActivity(),
    CoroutineScope by MainScope(),
    TransactionAdapter.TransactionItemClickListener,
    NewTransactionItemDialogFragment.NewTransactionItemDialogListener
{

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: TransactionListDatabase
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)

        database = TransactionListDatabase.getInstance(applicationContext)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = TransactionAdapter(this)
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter
        loadItemsInBackground()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemView = item.itemId

        when(itemView) {
            R.id.add -> NewTransactionItemDialogFragment().show(supportFragmentManager, NewTransactionItemDialogFragment.TAG)
            R.id.statistics -> openStatisticsActivity()
        }
        return true
    }

    private fun openStatisticsActivity() {
        val statisticsIntent = Intent(this, StatisticsActivity::class.java)
        startActivity(statisticsIntent)
    }

    private fun loadItemsInBackground() = launch {
        val items = withContext(Dispatchers.IO) {
            database.transactionItemDao().getAll()
        }
        adapter.update(items)
    }

    override fun onItemDeleted(item: TransactionItem) {
        deleteItemInBackground(item)
    }

    // Ez hívódik meg amikor csak ráklikkelek egy item-re
    override fun onItemSelected(item: TransactionItem) {
        EditTransactionItemDialogFragment(item).show(supportFragmentManager, EditTransactionItemDialogFragment.TAG)
    }

    //Akkor hívódik meg ha a piechart-ra kattintunk
    override fun onItemsStatisticsSelected(item: TransactionItem) {
        val pieChartIntent = Intent(this, PieChartActivity::class.java)

        //Átadjuk az ID-t, hogy tudjuk melyik elem információit kell lekérni.
        //Más megoldás sorminta kialakulásához vezetne.
        pieChartIntent.putExtra(PieChartActivity.KEY_ID, item.id)

        startActivity(pieChartIntent)
    }

    private fun updateItemInBackground(oldItem: TransactionItem, newItem: TransactionItem) = launch {
        withContext(Dispatchers.IO) {
            database.transactionItemDao().update(newItem)
            notifyIfExpendituresOutWeightRevenues()
        }
        adapter.updateItem(oldItem, newItem)
    }

    private fun deleteItemInBackground(item: TransactionItem) = launch {
        withContext(Dispatchers.IO) {
            database.transactionItemDao().deleteItem(item)
            notifyIfExpendituresOutWeightRevenues()
        }
        adapter.removeItem(item)
    }

    //Ez hívódik meg akkor amikor egy elemet létrehozok
    override fun onTransactionItemCreated(item: TransactionItem) {
        addItemInBackgound(item)
    }

    override fun onTransactionItemUpdated(olditem: TransactionItem, newItem: TransactionItem) {
        updateItemInBackground(olditem, newItem)
    }

    override fun onInvalidDataGiven() {
        Toast.makeText(this@MainActivity, getString(R.string.invalidDataText), Toast.LENGTH_SHORT).show()
    }

    private fun addItemInBackgound(item: TransactionItem) = launch {
        var id: Long = 0

        withContext(Dispatchers.IO) {
            id = database.transactionItemDao().insert(item)
            notifyIfExpendituresOutWeightRevenues()
        }

        item.id = id
        adapter.addItem(item)
    }

    private fun notifyIfExpendituresOutWeightRevenues() = launch {
        withContext(Dispatchers.IO) {
            val exp = database.transactionItemDao().getSumExp()
            val rev = database.transactionItemDao().getSumRev()
            runOnUiThread {
                if (exp > rev) {
                    Toast.makeText(this@MainActivity, getString(R.string.notificationText), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}