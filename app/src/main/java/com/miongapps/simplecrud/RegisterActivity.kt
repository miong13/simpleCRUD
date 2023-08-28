package com.miongapps.simplecrud

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log.d
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream


class RegisterActivity : AppCompatActivity() {

    var sharedPrefs : SharedPreferences? = null
    var PREFS_KEY = "pref"
    var LOGGED_IN = "loggedIn"
    var LOGGED_USERFN = "loggedUserFN"
    var is_loggedIn = ""

    private lateinit var imgProfilePic : ImageView
    private lateinit var btnRegisterCancel : Button

    // Define the pic id
    private var pic_id = 123
    val REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        getSupportActionBar()?.hide() // hide app title bar

        sharedPrefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        is_loggedIn = sharedPrefs!!.getString(LOGGED_IN,"").toString()

        if (ContextCompat.checkSelfPermission(this@RegisterActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@RegisterActivity,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this@RegisterActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                ActivityCompat.requestPermissions(this@RegisterActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            }
        }


        imgProfilePic = findViewById(R.id.imgProfilePic)
        imgProfilePic.setOnClickListener {
            capturePhoto()
        }

        btnRegisterCancel = findViewById(R.id.btnRegisterCancel)
        btnRegisterCancel.setOnClickListener {
            CancelRegister()
        }
    }

    override fun onStart() {
        super.onStart()
        if(is_loggedIn.equals("true")){
            val DashboardIntent = Intent(this, Dashboard::class.java)
            startActivity(DashboardIntent)
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@RegisterActivity,
                            Manifest.permission.CAMERA) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
//                        capturePhoto()
                    }
                } else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
//            logo = findViewById(R.id.iv)
            imgProfilePic.setImageBitmap(data.extras?.get("data") as Bitmap)
            
            val byteArrayOutputStream = ByteArrayOutputStream()
//            val bitmap = BitmapFactory.decodeResource(resources, imgProfilePic.id)
            val bitmap = data.extras?.get("data") as Bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
            val imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            println("IMAGESTRING: $imageString")

//            //image decode
//            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
//            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length)
//            imageView.setImageBitmap(decodedImage)
        }
    }

    private fun CancelRegister(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}