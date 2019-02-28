package com.valette.defossez.firemapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.location.Geocoder
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.valette.defossez.firemapp.controller.FireworksController
import com.valette.defossez.firemapp.entity.Firework
import com.valette.defossez.firemapp.service.AddressService
import kotlinx.android.synthetic.main.activity_form_add_firework.*
import java.text.SimpleDateFormat


class FormAddFireworkActivity : AppCompatActivity() {

    val cal = Calendar.getInstance()
    val controller = FireworksController()
    val addressService = AddressService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_add_firework)
        initDateTimePickers()
        submit.setOnClickListener {
            validate()
        }

        inputAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Handler().postDelayed({
                    if ( addressService.getAddresses(s.toString(), 3).isNotEmpty()) {
                        var res = addressService.getAddresses(s.toString(), 3)[0]
                    }
                }, 2000)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    fun validate() {
        var address = inputAddress.text
        var res = addressService.getAddresses(address.toString(), 3)[0]
        var date = SimpleDateFormat("dd/MM/yy HH:mm").parse("${inputDate.text}  ${inputTime.text}")
        var title = inputTitle.text
        var description = inputDescription.text
        Toast.makeText(this," $address $date $title $description", Toast.LENGTH_SHORT).show()
        controller.create(Firework("", title.toString(), description.toString(), res.latitude, res.longitude, res.getAddressLine(0), date))
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