package com.example.resources_user.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.resources_user.R
import com.example.resources_user.databinding.DialogFragmentFilterBinding
import java.util.*


class FilterDialogFragment(private val preOptions: FilterOptions? = null) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {
    private lateinit var binding: DialogFragmentFilterBinding

    interface FilterDialogListener {
        fun onSaveFilterDialog(filterOptions: FilterOptions)
    }

    data class FilterOptions(
        val beginDate: String,
        val sortOrder: String,
        val newsDesks: List<String>
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_fragment_filter,
            container,
            false
        )

        // init pre-option
        if (preOptions != null) {
            if (preOptions.beginDate != "") {
                val date = preOptions.beginDate
                val day = date.subSequence(date.length - 2, date.length)
                val month = date.subSequence(date.length - 4, date.length - 2)
                val year = date.subSequence(0, date.length - 4)
                "$day/$month/$year".also { binding.tvBeginDate.text = it }
            }
            if (preOptions.sortOrder == "oldest") {
                binding.spnSort.setSelection(0)
            } else {
                binding.spnSort.setSelection(1)
            }
            if (preOptions.newsDesks.contains("Sports")) {
                binding.cbSports.isChecked = true
            }
            if (preOptions.newsDesks.contains("Arts")) {
                binding.cbArts.isChecked = true
            }
            if (preOptions.newsDesks.contains("Fashion")) {
                binding.cbFashionStyle.isChecked = true
            }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // set handler for event click cancel
        binding.tvCancel.setOnClickListener { dialog?.dismiss() }

        // set handler for event click save
        binding.tvSave.setOnClickListener {
            val beginDateText = binding.tvBeginDate.text
            var beginDate = ""
            if (beginDateText != "") {
                beginDate = beginDateText.split('/').reduce { acc, str -> str + acc }
            }
            val sortOrder = binding.spnSort.selectedItem.toString()
            val newsDesks = mutableListOf<String>()
            if (binding.cbSports.isChecked) {
                newsDesks.add("Sports")
            }
            if (binding.cbArts.isChecked) {
                newsDesks.add("Arts")
            }
            if (binding.cbFashionStyle.isChecked) {
                newsDesks.add("Fashion")
                newsDesks.add("Style")
            }

            // trigger save filter event handler of MainActivity
            (activity as FilterDialogListener).onSaveFilterDialog(
                FilterOptions(
                    beginDate,
                    sortOrder,
                    newsDesks
                )
            )

            dialog?.dismiss()
        }

        // open DatePicker dialog
        binding.tvBeginDate.setOnClickListener { showDatePickerDialog() }

        // set handler for event click reset
        binding.tvReset.setOnClickListener {
            binding.tvBeginDate.text = ""
            binding.spnSort.setSelection(0)
            binding.cbSports.isChecked = false
            binding.cbArts.isChecked = false
            binding.cbFashionStyle.isChecked = false
        }
    }

    private fun showDatePickerDialog() {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireActivity(), this, year, month, day)
        datePickerDialog.show()
    }


    // handle after a date picked
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val strYear = year.toString()
        val strMonth = if (month < 9) {
            "0${month + 1}"
        } else {
            "${month + 1}"
        }
        val strDay = if (dayOfMonth < 10) {
            "0${dayOfMonth}"
        } else {
            "$dayOfMonth"
        }
        "$strDay/$strMonth/$strYear".also { binding.tvBeginDate.text = it }
    }
}