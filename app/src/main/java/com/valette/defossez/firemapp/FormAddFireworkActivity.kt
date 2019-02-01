package com.valette.defossez.firemapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_form_add_firework.*
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class FormAddFireworkActivity : AppCompatActivity() {

    val cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_add_firework)
        initDateTimePickers()
        submit.setOnClickListener {
            submit()
        }
    }

    fun submit() {
        var address = inputAddress.text
        var date = SimpleDateFormat("dd/MM/yy HH:mm").parse("${inputDate.text}  ${inputTime.text}")
        var title = inputTitle.text
        var description = inputDescription.text
        Toast.makeText(this," $address $date $title $description", Toast.LENGTH_SHORT).show()
    }

    private fun initDateTimePickers(){
        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }

        inputDate.setOnClickListener {
            DatePickerDialog(this, date, cal
                    .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        val time = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            updateTime()
        }

        inputTime.setOnClickListener {
            TimePickerDialog(this, time, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
    }

    private fun updateDate() {
        val format = "dd/MM/yy"
        val sdf = SimpleDateFormat(format, Locale.FRANCE)
        inputDate.setText(sdf.format(cal.time))
    }

    private fun updateTime() {
        val format = "HH:mm"
        val sdf = SimpleDateFormat(format, Locale.FRANCE)
        inputTime.setText(sdf.format(cal.time))
    }
}