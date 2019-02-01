package com.valette.defossez.firemapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_form_add_firework.*
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.text.SimpleDateFormat


class FormAddFireworkActivity : AppCompatActivity() {

    val myCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_add_firework)

        submit.setOnClickListener {
            validate()
        }

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        inputDate.setOnClickListener {
            DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    fun validate() {
        var address = inputAddress.text
        var date = inputDate.text
        var hour = inputHour.text
        var title = inputTitle.text
        var description = inputDescription.text
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yy"
        val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)

        inputDate.setText(sdf.format(myCalendar.time))
    }
}