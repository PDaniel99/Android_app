package hu.bme.aut.android.expenditurerevenuemanager.fragments

import android.R
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionItem
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionItemCategory
import hu.bme.aut.android.expenditurerevenuemanager.databinding.DialogNewTransactionItemBinding
import java.util.*

class EditTransactionItemDialogFragment(private var transactionItem: TransactionItem) : NewTransactionItemDialogFragment() {

    companion object {
        const val TAG = "EditTransactionItemDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogNewTransactionItemBinding.inflate(LayoutInflater.from(context))

        binding.spCategory.adapter = ArrayAdapter(
            requireContext()!!,
            R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(hu.bme.aut.android.expenditurerevenuemanager.R.array.category_items)
        )

        //DatePicker input validáció
        initializeDatePicker(binding.dpDatePicker)

        binding.etPartner.setText(transactionItem.partner)
        binding.spCategory.setSelection(transactionItem.category.ordinal.toInt())
        binding.etValue.setText(transactionItem.value.toString())
        binding.etDescription.setText(transactionItem.description)

        val cal = Calendar.getInstance()
        cal.setTime(transactionItem.date)
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]

        binding.dpDatePicker.init(year, month, day, null)

        //Ezzel van dolgunk
        return AlertDialog.Builder(requireActivity())
            .setTitle(getString(hu.bme.aut.android.expenditurerevenuemanager.R.string.edit_transaction_item))
            .setView(binding.root)
            .setPositiveButton(hu.bme.aut.android.expenditurerevenuemanager.R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onTransactionItemUpdated(transactionItem, getTransactionItem())
                } else {
                    listener.onInvalidDataGiven()
                }
            }
            .setNegativeButton(hu.bme.aut.android.expenditurerevenuemanager.R.string.cancel, null)
            .create()
    }

    override fun getTransactionItem(): TransactionItem {
        val day = binding.dpDatePicker.dayOfMonth
        val month = binding.dpDatePicker.month
        val year =  binding.dpDatePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        return TransactionItem(
            partner = binding.etPartner.text.toString(),
            description = binding.etDescription.text.toString(),
            value = binding.etValue.text.toString().toLongOrNull() ?: 0,
            category = TransactionItemCategory.getByOrdinal(binding.spCategory.selectedItemPosition)
                ?: TransactionItemCategory.EXPENDITURE,
            date = calendar.getTime(),
            id = transactionItem.id
        )
    }
}