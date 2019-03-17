package com.valette.defossez.firemapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import com.valette.defossez.firemapp.controller.FireworksController
import com.valette.defossez.firemapp.entity.Firework
import com.valette.defossez.firemapp.service.AddressService
import kotlinx.android.synthetic.main.activity_form_add_firework.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList
import android.view.View
import com.valette.defossez.firemapp.R
import com.valette.defossez.firemapp.adapter.AddressAdapter
import com.valette.defossez.firemapp.service.LocationService


class FormAddFireworkActivity : AppCompatActivity() {

    val ctx = this
    var timer = Timer()
    val DELAY: Long = 500

    val controller = FireworksController()

    val cal = Calendar.getInstance()
    var addresses = ArrayList<String>()
    private lateinit var locationService: LocationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_add_firework)
        initDateTimePickers()
        locationService = LocationService(this)
        submit.setOnClickListener {
            validate()
        }
        var adapter = AddressAdapter(this, R.layout.dropdown, addresses)
        inputAddress.threshold = 1
        inputAddress.setAdapter<AddressAdapter>(adapter)

        inputAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text : Editable?) {
                getAddressFromInput(text.toString(), adapter)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }

    private fun validate() {
        if(inputAddress.text.isEmpty()){
            input_layout_address.error = "Veuillez entrer une adresse valide"
            return
        }
        if(addresses.isEmpty()){
            input_layout_address.error = "Veuillez entrer une adresse valide, adresse inconnue"
            return
        }
        var address = addresses[0]

        if(inputDate.text.isEmpty()){
            input_layout_date.error = "Veuillez entrer une date"
            return
        }
        if(inputTime.text.isEmpty()){
            input_layout_time.error = "Veuillez entrer un horaire"
            return
        }
        var date = SimpleDateFormat("dd/MM/yy HH:mm").parse("${inputDate.text}  ${inputTime.text}")
        var title = inputTitle.text
        var description = inputDescription.text
        controller.create(Firework("", title.toString(), description.toString(), 0.0, 0.0, "adresse", date))
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

    private fun getAddressFromInput(text : String, adapter: ArrayAdapter<String>){
        progressBar.visibility = View.VISIBLE
        if(text.length > 3){
            timer.cancel()
            timer = Timer()
            timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            ctx.runOnUiThread {
                                progressBar.visibility = View.GONE
                            }

                        }
                    },
                    DELAY
            )
        }else{
            adapter.clear()
        }
    }
}