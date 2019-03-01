package com.valette.defossez.firemapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import com.valette.defossez.firemapp.controller.FireworksController
import com.valette.defossez.firemapp.entity.Firework
import com.valette.defossez.firemapp.service.AddressService
import kotlinx.android.synthetic.main.activity_form_add_firework.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList
import android.R.string.cancel
import android.content.Intent
import com.valette.defossez.firemapp.adapter.AddressAdapter


class FormAddFireworkActivity : AppCompatActivity() {

    val cal = Calendar.getInstance()
    val controller = FireworksController()
    val addressService = AddressService(this)
    var addresses = ArrayList<String>()
    val ctx = this
    var timer = Timer()
    val DELAY: Long = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_add_firework)
        initDateTimePickers()
        submit.setOnClickListener {
            validate()
        }
        var adapter = AddressAdapter(this, R.layout.dropdown, addresses)
        inputAddress.threshold = 1
        inputAddress.setAdapter<AddressAdapter>(adapter)

        inputAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text : Editable?) {
                if(text.toString().length > 3){
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                            object : TimerTask() {
                                override fun run() {
                                    ctx.runOnUiThread {
                                        if (text.toString().isNotEmpty() && addressService.getAddresses(text.toString(), 3).isNotEmpty()) {
                                            var address = addressService.getAddresses(text.toString(), 5)[0].getAddressLine(0)
                                            adapter.clear()
                                            adapter.add(address)
                                        }
                                    }

                                }
                            },
                            DELAY
                    )
                }else{
                    adapter.clear()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    fun validate() {
        var address = inputAddress.text
        var res = addressService.getAddresses(address.toString(), 3)[0]
        var date = SimpleDateFormat("dd/MM/yy HH:mm").parse("${inputDate.text}  ${inputTime.text}")
        var title = inputTitle.text
        var description = inputDescription.text
        controller.create(Firework("", title.toString(), description.toString(), res.latitude, res.longitude, res.getAddressLine(0), date))
        finish()
    }

    private fun initDateTimePickers() {
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