package hu.bme.aut.android.expenditurerevenuemanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.expenditurerevenuemanager.R
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionItem
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionItemCategory
import hu.bme.aut.android.expenditurerevenuemanager.databinding.ItemTransactionListBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val listener: TransactionItemClickListener) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>()
{

    private val items = mutableListOf<TransactionItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TransactionViewHolder(
        ItemTransactionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transactionItem = items[position]

        holder.binding.ivIcon.setImageResource(getImageResource(transactionItem.category))
        holder.binding.tvDescription.text = transactionItem.description
        holder.binding.tvValue.text = "${transactionItem.value} Ft"
        holder.binding.tvPartner.text = transactionItem.partner
        holder.binding.tvDate.text = SimpleDateFormat("yyyy-MM-dd").format(transactionItem.date)


        holder.binding.ibRemove.setOnClickListener {
            listener.onItemDeleted(transactionItem)
        }

        //Eseménykezelő ha a statisztika gombra kattintunk
        holder.binding.ibSeeStatistics.setOnClickListener {
            listener.onItemsStatisticsSelected(transactionItem)
        }

        //Eseménykezelő arra az esetre ha rákattintunk egy elemre
        holder.binding.root.setOnClickListener {
            listener.onItemSelected(transactionItem)
        }
    }

    @DrawableRes()
    private fun getImageResource(category: TransactionItemCategory): Int {
        return when (category) {
            TransactionItemCategory.EXPENDITURE -> R.drawable.ic_baseline_arrow_back_24
            TransactionItemCategory.REVENUE -> R.drawable.ic_baseline_arrow_forward_24
        }
    }

    override fun getItemCount(): Int = items.size

    //Ezt implementálja a MainActivity
    interface TransactionItemClickListener {
        fun onItemDeleted(item: TransactionItem)
        //Ez a fuggveny arra van, hogy ha csak simán random rakattintunk egy elemre
        fun onItemSelected(item: TransactionItem)
        // Kell egy függvény a statisztikának
        fun onItemsStatisticsSelected(item: TransactionItem)
    }

    inner class TransactionViewHolder(val binding: ItemTransactionListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        }


    fun addItem(item: TransactionItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(transactionItems: List<TransactionItem>) {
        items.clear()
        items.addAll(transactionItems)
        notifyDataSetChanged()
    }

    fun removeItem(item: TransactionItem) {
        val position = items.indexOf(item)
        items.remove(item)
        notifyItemRemoved(position)
    }

    fun updateItem(oldItem: TransactionItem, newItem: TransactionItem) {
        //
        val position = items.indexOf(oldItem)
        items[position] = newItem
        notifyItemChanged(position)
    }
}