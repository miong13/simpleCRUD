package com.miongapps.simplecrud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class EditUserActivity : AppCompatActivity() {
    private lateinit var etEditName : EditText
    private lateinit var etEditEmail: EditText
    private lateinit var btnSaveEdit : Button
    var pos : Int = 0
    var userID : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        etEditName = findViewById(R.id.etEditName)
        etEditEmail = findViewById(R.id.etEditEmail)
        setData()

        btnSaveEdit = findViewById(R.id.btnSaveEdit)
        btnSaveEdit.setOnClickListener{
            println("Save!")
            val EmployeeName = etEditName.text.toString()
            var EmployeeEmail = etEditEmail.text.toString()
            val users = UsersDataClass(0, userID.toString(), EmployeeName, EmployeeEmail)
            saveEditData(users, pos);
        }
    }

    private fun setData(){
        var intent_extras = intent.extras
        var employeeNameEdit = intent_extras!!.getString("fullname")
        var employeeEmailEdit = intent_extras!!.getString("email")
        pos = intent_extras!!.getInt("position")
        userID = intent_extras!!.getString("userId")
//        Toast.makeText(this, userID, Toast.LENGTH_LONG).show()
        etEditName.setText(employeeNameEdit)
        etEditEmail.setText(employeeEmailEdit)
    }

    private fun saveEditData(currentItem: UsersDataClass, position: Int){
        val intent = Intent(this, Dashboard::class.java)
//        val extras = Bundle()
//        extras.putInt("imageId", currentItem.titleImage)
//        extras.putString("userId", currentItem.userId)
//        extras.putString("fullname", currentItem.fullname)
//        extras.putString("email", currentItem.email)
//        extras.putInt("position", position)
//        intent.putExtras(extras)
        // API CALL TO SAVE HERE
        ApiRequest().updateEmployee(currentItem){ result ->
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }
                startActivity(Intent(this, Dashboard::class.java))
                finish()
        }

    }
}