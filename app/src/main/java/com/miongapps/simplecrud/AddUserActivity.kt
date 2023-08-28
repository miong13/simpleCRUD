package com.miongapps.simplecrud

import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class AddUserActivity : AppCompatActivity() {
    private lateinit var etAddName : TextView
    private lateinit var etAddEmail : TextView
    private lateinit var btnAddSave : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        etAddName = findViewById(R.id.etAddName)
        etAddEmail = findViewById(R.id.etAddEmail)
        btnAddSave = findViewById(R.id.btnSaveAdd)
        btnAddSave.setOnClickListener{
            val addEmployeeName = etAddName.text.toString()
            val addEmployeeEmail = etAddEmail.text.toString()
            if(addEmployeeName.isNullOrEmpty() || addEmployeeEmail.isNullOrEmpty()){
                Toast.makeText(this, "Please fill-in all fields!", Toast.LENGTH_LONG).show()
            }else{
                val employeeData = UsersDataClass(0, "", addEmployeeName, addEmployeeEmail)
                ApiRequest().registerEmployee(employeeData) {
                    result ->
                    if(result.equals("Email Already Exists!")){
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                        }
                    }else{
                        startActivity(Intent(this, Dashboard::class.java))
                        finish()
                    }
                }

            }
        }
    }
}