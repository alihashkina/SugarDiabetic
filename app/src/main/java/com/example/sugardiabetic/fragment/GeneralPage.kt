package com.example.sugardiabetic.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.TextView.OnEditorActionListener
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sugardiabetic.R
import com.example.sugardiabetic.databinding.FragmentGeneralPageBinding
import com.example.sugardiabetic.db.MyDBHelper
import com.example.sugardiabetic.viewModel.GeneralPageViewModel
import com.example.sugardiabetic.viewModel.GeneralPageViewModel.Companion.day
import com.example.sugardiabetic.viewModel.GeneralPageViewModel.Companion.hour
import com.example.sugardiabetic.viewModel.GeneralPageViewModel.Companion.minute
import com.example.sugardiabetic.viewModel.GeneralPageViewModel.Companion.month
import com.example.sugardiabetic.viewModel.GeneralPageViewModel.Companion.year


class GeneralPage : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    companion object {
        fun newInstance() = GeneralPage()
        var editSugar = ""
        var chipsCheckTxt = ""
        var dateDB = ""
        var arrayDateGraph : MutableList<String> = mutableListOf()
        var arraySugarGraph : MutableList<Int> = mutableListOf()
        lateinit var bindingGeneralPage: FragmentGeneralPageBinding
    }

    var saveyear = 0
    var savemonth = 0
    var saveday = 0
    var savehour = 0
    var saveminute = 0


    private lateinit var viewModel: GeneralPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingGeneralPage= DataBindingUtil.inflate(inflater, R.layout.fragment_general_page,container,false)
        return bindingGeneralPage.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(GeneralPageViewModel::class.java)
        bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
        editSugar = bindingGeneralPage.txtSugar.text.toString()

        bindingGeneralPage.txtSugar.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                bindingGeneralPage.btnSave.callOnClick()
               handled = true
            }
            handled
        })

        bindingGeneralPage.btnPlus.setOnClickListener {
            if(bindingGeneralPage.txtSugar.text.toString().toDouble() < 30.0) {
                bindingGeneralPage.txtSugar.setText(
                    "${((bindingGeneralPage.txtSugar.text.toString().toDouble() * 10) + 1)/10}"
                )
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
            }
            else{
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
                bindingGeneralPage.txtSugar.setText("30.0")
            }
        }
        bindingGeneralPage.btnMinus.setOnClickListener {
            if(bindingGeneralPage.txtSugar.text.toString().toDouble() != 0.0) {
                bindingGeneralPage.txtSugar.setText(
                    "${((bindingGeneralPage.txtSugar.text.toString().toDouble() * 10) - 1)/10}"
                )
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
            }
            else{
                bindingGeneralPage.txtSugar.setSelection(bindingGeneralPage.txtSugar.length())
                bindingGeneralPage.txtSugar.setText("0.0")
            }
        }

        viewModel.graph(bindingGeneralPage.graph, requireContext())

        bindingGeneralPage.btnSave.setOnClickListener {
            viewModel.chipsCheck(bindingGeneralPage.chip1, bindingGeneralPage.chip2, bindingGeneralPage.chip3, bindingGeneralPage.chip4, bindingGeneralPage.chip5)

            var cv = ContentValues()
            cv.put("DATE", "${bindingGeneralPage.txtRecord.text.drop(7)}")
            cv.put("SUGAR", bindingGeneralPage.txtSugar.text.toString())
            cv.put("CHIPS", "${chipsCheckTxt}")
            MyDBHelper(requireContext()).readableDatabase.insert("USERS", null, cv)
            chipsCheckTxt = ""
            viewModel.graph(bindingGeneralPage.graph, requireContext())
        }

        if(bindingGeneralPage.txtRecord.text == ""){
            viewModel.getDateTimeCalendar(bindingGeneralPage.txtRecord)
        }
        pickDate()
    }



    private fun pickDate(){
        bindingGeneralPage.txtRecord.setOnClickListener{
            viewModel.getDateTimeCalendar(bindingGeneralPage.txtRecord)
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        saveday = day
        savemonth = month
        saveyear = year
        viewModel.getDateTimeCalendar(bindingGeneralPage.txtRecord)
        TimePickerDialog(requireContext(), this, hour, minute, false).show()
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        savehour = hour
        saveminute = minute
        bindingGeneralPage.txtRecord.text = "Record $saveday.${savemonth + 1}.$saveyear $savehour:$saveminute"
    }

}