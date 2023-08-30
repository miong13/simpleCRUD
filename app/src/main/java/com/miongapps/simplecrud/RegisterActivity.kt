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
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log.d
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import java.io.ByteArrayOutputStream
import com.miongapps.simplecrud.CommonUtils as CommUtils

class RegisterActivity : AppCompatActivity() {

    var sharedPrefs : SharedPreferences? = null
    var PREFS_KEY = "pref"
    var LOGGED_IN = "loggedIn"
    var LOGGED_USERFN = "loggedUserFN"
    var is_loggedIn = ""

    private lateinit var imgProfilePic : ImageView
    private lateinit var btnRegisterCancel : Button
    private lateinit var btnRegisterSave : Button
    private lateinit var etRegisterName : EditText
    private lateinit var etRegisterEmail : EditText
    private lateinit var etRegisterPassword : EditText
    private lateinit var etRegisterConfirmPassword : EditText
    private lateinit var txtBase64Image : TextView
    private lateinit var lblPasswordMatch : TextView

    // Define the pic id
    private var pic_id = 123
    val REQUEST_CODE = 200
    var Base64ImageString : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        getSupportActionBar()?.hide() // hide app title bar

        sharedPrefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        is_loggedIn = sharedPrefs!!.getString(LOGGED_IN,"").toString()



//        if (ContextCompat.checkSelfPermission(this@RegisterActivity,
//                Manifest.permission.ACCESS_FINE_LOCATION) !==
//            PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this@RegisterActivity,
//                    Manifest.permission.CAMERA)) {
//                ActivityCompat.requestPermissions(this@RegisterActivity,
//                    arrayOf(Manifest.permission.CAMERA), 1)
//            } else {
//                ActivityCompat.requestPermissions(this@RegisterActivity,
//                    arrayOf(Manifest.permission.CAMERA), 1)
//            }
//        }


        etRegisterName = findViewById(R.id.etRegisterName)
        etRegisterEmail = findViewById(R.id.etRegisterEmail)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)

        imgProfilePic = findViewById(R.id.imgProfilePic)
        btnRegisterCancel = findViewById(R.id.btnRegisterCancel)

        lblPasswordMatch = findViewById(R.id.lblPasswordMatch)
        btnRegisterSave = findViewById(R.id.btnRegisterSave)
        txtBase64Image = findViewById(R.id.txtBase64Image)

        var registerEmployeeName = etRegisterName.text
        var registerEmployeeEmail = etRegisterEmail.text
        var empPassword  = etRegisterPassword.text
        var empConfirmPassword  = etRegisterConfirmPassword.text


        etRegisterConfirmPassword.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(empPassword.toString().equals(p0.toString(), false)){
                    lblPasswordMatch.text = "Password match!"
                    lblPasswordMatch.setTextColor(getResources().getColor(R.color.color_best_tip))
                }else{
                    lblPasswordMatch.text = "Password does not match!"
                    lblPasswordMatch.setTextColor(getResources().getColor(R.color.color_worst_tip))
                }
            }
        })

        imgProfilePic.setOnClickListener {
            capturePhoto()
        }


        btnRegisterCancel.setOnClickListener {
            CancelRegister()
        }

        btnRegisterSave.setOnClickListener {
            var registerEmployeePhoto = Base64ImageString
            if(CommUtils().isValidEmail(registerEmployeeEmail)){
                if(empPassword.toString().equals(empConfirmPassword.toString(), false)){
                    val employeeData = UsersDataClass(0, "", registerEmployeeName.toString(), registerEmployeeEmail.toString(), registerEmployeePhoto.toString())
                    ApiRequest().registerEmployeeWithPhoto(employeeData, empPassword.toString()){ result ->
                        if(result.equals("Email Already Exists!")){
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                            }
                        }else{
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                            }
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                }else{
                    Toast.makeText(this, "Password does not match!", Toast.LENGTH_LONG).show()
                }

            }else{
                CommUtils().alertDialog("Error", "Invalid Email", false, this@RegisterActivity)
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show()
            }

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

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
//                                            grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            1 -> {
//                if (grantResults.isNotEmpty() && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED) {
//                    if ((ContextCompat.checkSelfPermission(this@RegisterActivity,
//                            Manifest.permission.CAMERA) ===
//                                PackageManager.PERMISSION_GRANTED)) {
//                        Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
//                }
//                return
//            }
//        }
//    }

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

            print("IMAGESTRING: $imageString")
            txtBase64Image.text = imageString
            Base64ImageString = txtBase64Image.text.toString()
            //image decode
//            val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
//            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length)
//            imgProfilePic.setImageBitmap(decodedImage)
        }
    }

    private fun CancelRegister(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}