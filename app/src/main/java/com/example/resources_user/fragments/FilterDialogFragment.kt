package com.example.resources_user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.resources_user.R
import com.example.resources_user.databinding.DialogFragmentFilterBinding


class FilterDialogFragment(private val preOptions:FilterOptions?=null) : DialogFragment() {
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

        // set up pre-option
        if (preOptions!=null) {
            binding.etBeginDate.setText(preOptions.beginDate)
            if (preOptions.sortOrder == "oldest"){
                binding.spnSort.setSelection(0)
            }else{
                binding.spnSort.setSelection(1)
            }
            if (preOptions.newsDesks.contains("Sports")){
                binding.cbSports.isChecked=true
            }
            if (preOptions.newsDesks.contains("Arts")){
                binding.cbArts.isChecked=true
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
        binding.btnCancel.setOnClickListener { dialog?.dismiss() }
        binding.btnSave.setOnClickListener {
            val beginDate = binding.etBeginDate.text.toString()
            val sortOrder = binding.spnSort.selectedItem.toString()
            val newsDesks = mutableListOf<String>()
            if (binding.cbSports.isChecked) {
                newsDesks.add("Sports")
            }
            if (binding.cbArts.isChecked) {
                newsDesks.add("Arts")
            }
            (activity as FilterDialogListener).onSaveFilterDialog(
                FilterOptions(
                    beginDate,
                    sortOrder,
                    newsDesks
                )
            )
            dialog?.dismiss()
        }
    }
}