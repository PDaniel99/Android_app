package hu.bme.aut.android.expenditurerevenuemanager.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import hu.bme.aut.android.expenditurerevenuemanager.R
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionItem
import hu.bme.aut.android.expenditurerevenuemanager.data.TransactionItemCategory
import hu.bme.aut.android.expenditurerevenuemanager.databinding.DialogNewTransactionItemBinding
import java.util.*

open class NewTransactionItemDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "NewTransactionItemDialogFragment"
    }

    protected lateinit var listener: NewTransactionItemDialogListener

    protected var _binding: DialogNewTransactionItemBinding? = null
    protected val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity: FragmentActivity = requireActivity()!!
        listener = if (activity is NewTransactionItemDialogListener) {
            activity
        } else {
            throw RuntimeException("Activity must implement the NewTransactionItemDialogListener interface!")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogNewTransactionItemBinding.inflate(LayoutInflater.from(context))

        binding.spCategory.adapter = ArrayAdapter(
            requireContext()!!,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.category_items)
        )

        //Kell inputvalidáció a datePickernek
        initializeDatePicker(binding.dpDatePicker)

        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.new_transaction_item)
            .setView(binding.root)
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onTransactionItemCreated(getTransactionItem())
                } else {
                    listener.onInvalidDataGiven()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    protected fun isValid(): Boolean {
        if (binding.etPartner.text != null &&
            (binding.etPartner.text.toString().isNotEmpty()) &&
            (binding.etValue.text.toString().length < 11) &&
            binding.etValue.text != null && isNumber(binding.etValue.text!!) &&
            binding.etDescription.text != null
            ) {
            return true
        }
        return false
    }

    private fun isNumber(text: Editable): Boolean {
        val value = binding.etValue.text!!.toString()
        if (value.toLongOrNull() != null) {
            return true
        }
        return false
    }

    protected fun initializeDatePicker(dp: DatePicker) {
        dp.maxDate = System.currentTimeMillis()
        val calendar2 = Calendar.getInstance();
        //Set Minimum date of calendar
        calendar2.set(2000, 0, 1);
        dp.setMinDate(calendar2.getTimeInMillis());
    }

    protected open fun getTransactionItem(): TransactionItem {
        val day = binding.dpDatePicker.dayOfMonth
        val month = binding.dpDatePicker.month
        val year =  binding.dpDatePicker.year

        val calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return TransactionItem(
            partner = binding.etPartner.text.toString(),
            description = binding.etDescription.text.toString(),
            value = binding.etValue.text.toString().toLongOrNull() ?: 0,
            category = TransactionItemCategory.getByOrdinal(binding.spCategory.selectedItemPosition)
                ?: TransactionItemCategory.EXPENDITURE,
            date = calendar.getTime()
        )
    }

    interface NewTransactionItemDialogListener {
        fun onTransactionItemCreated(item: TransactionItem)
        fun onTransactionItemUpdated(oldItem: TransactionItem, newItem: TransactionItem)
        fun onInvalidDataGiven()
    }
}