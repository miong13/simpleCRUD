package com.miongapps.simplecrud

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView

class SelectedUserActivity : AppCompatActivity() {

    private lateinit var txtName : TextView
    private lateinit var txtEmail: TextView
    private lateinit var btnSelectedBack : Button
    private lateinit var selectedImg : ShapeableImageView
    var pos : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_user)
        txtName = findViewById(R.id.txtName)
        txtEmail= findViewById(R.id.txtEmail)
        btnSelectedBack = findViewById(R.id.btnSelectedBack)
        selectedImg = findViewById(R.id.selectedImg)

        getExtraData()

        btnSelectedBack.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getExtraData(){
        var intent_extras = intent.extras
        var employeeName = intent_extras!!.getString("fullname")
        var employeeEmail = intent_extras!!.getString("email")
        var employeePhoto = intent_extras!!.getString("photo")
        var employeeImageId = intent_extras!!.getInt("imageId")
        pos = intent_extras!!.getInt("position")
        txtName.text = employeeName
        txtEmail.text = employeeEmail

        if(employeePhoto.equals("null")){
            selectedImg.setImageResource(employeeImageId)
        }else{
            val decodedString: ByteArray = Base64.decode(employeePhoto, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            selectedImg.setImageBitmap(decodedByte)
        }
    }

    override fun onStart() {
        super.onStart()
        this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
    }

    override fun finish() {
        super.finish()
        this.overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave)
    }
}