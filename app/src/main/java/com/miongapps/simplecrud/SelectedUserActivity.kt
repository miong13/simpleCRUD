package com.miongapps.simplecrud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SelectedUserActivity : AppCompatActivity() {

    private lateinit var txtName : TextView
    private lateinit var txtEmail: TextView
    var pos : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_user)
        txtName = findViewById(R.id.txtName)
        txtEmail= findViewById(R.id.txtEmail)
        getExtraData()
    }

    private fun getExtraData(){
        var intent_extras = intent.extras
        var employeeName = intent_extras!!.getString("fullname")
        var employeeEmail = intent_extras!!.getString("email")
        pos = intent_extras!!.getInt("position")
        txtName.text = employeeName
        txtEmail.text = employeeEmail
    }
}