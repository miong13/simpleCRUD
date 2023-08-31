package com.miongapps.simplecrud

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var btnLogin : Button
    private lateinit var etEmail : EditText
    private lateinit var etPword : EditText
    private lateinit var ERROR_MSG : String
    private lateinit var tvAlertMessages : TextView
    private lateinit var txtRegister : TextView
    var sharedPrefs : SharedPreferences? = null
    var PREFS_KEY = "pref"
    var LOGGED_IN = "loggedIn"
    var LOGGED_USERFN = "loggedUserFN"
    var LOGGED_USEREM = "loggedUserEM"
    var LOGGED_USERPH = "loggedUserPH"
    var LOGGED_USERID = "loggedUserID"
    var is_loggedIn : String? = null
    var resultApi : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide() // hide app title bar

        sharedPrefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        is_loggedIn = sharedPrefs!!.getString(LOGGED_IN, "").toString()

        if(is_loggedIn.equals("true")){
            val DashboardIntent = Intent(this, Dashboard::class.java)
            startActivity(DashboardIntent)
            finish()
        }

        tvAlertMessages = findViewById(R.id.tvAlertMessages)
        txtRegister = findViewById(R.id.txtRegister)
        RegisterMe(txtRegister)

        // EMAIL EDITTEXT
        etEmail = findViewById(R.id.etEmail)
        var email = etEmail.text
        // PWORD EDITTEXT
        etPword = findViewById(R.id.etPword)
        var pword = etPword.text
        // LOGIN BUTTON
        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener{
            if(email.isNullOrEmpty() || pword.isNullOrEmpty()){
                ERROR_MSG = "Please fill-in email and password!"
                ToastAlert(ERROR_MSG)
            }else{
                etEmail.clearFocus()
                etPword.clearFocus()
                tvAlertMessages.text = ""
                if(isValidEmail(email)){
                    ApiRequest().loginEmployee(email, pword) { result ->
//                    println("MARKRAMOS REQUEST RESULT - $mess")
                        println("LOGIN: $result")
                        if(result.length() > 0) {
                            println(result.length())
                            println(result.get("err"));
                            when(result.get("err").toString()) {
                                "error" -> {
                                    ERROR_MSG = "Wrong Email and Password Combination!"
                                    Handler(Looper.getMainLooper()).post {
                                        // write your code here
                                        alertDialog("Error",  ERROR_MSG + "", false)
                                    }
                                }
                                "inactive" -> {
                                    ERROR_MSG = "The user in inactive!"
                                    Handler(Looper.getMainLooper()).post {
                                        // write your code here
                                        alertDialog("Error",  ERROR_MSG + "", false)
                                    }
                                }
                                "success" -> {
                                    val editor: SharedPreferences.Editor = sharedPrefs!!.edit()
                                    editor.putString(LOGGED_IN, "true")
                                    editor.putString(LOGGED_USERFN, result.get("full_name").toString())
                                    editor.putString(LOGGED_USEREM, result.get("email").toString())
                                    editor.putString(LOGGED_USERPH, result.get("photo").toString())
                                    editor.putString(LOGGED_USERID, result.get("uuid").toString())
                                    editor.apply()
                                    val DashboardIntent = Intent(this, Dashboard::class.java)
                                    startActivity(DashboardIntent)
                                    finish()
                                }
                                else -> {
                                    ERROR_MSG = "Please contact developer!"
                                    Handler(Looper.getMainLooper()).post {
                                        // write your code here
                                        alertDialog("Error",  ERROR_MSG + "", false)
                                    }
                                }
                            }
                        }

                    }
                }else{
                    ToastAlert("Invalid Email Format!")
                }

            }
        }

        this.onBackPressedDispatcher
    }

    fun ToastAlert(message: String){
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(this, message, duration) // in Activity
        toast.show()
    }

    // on below line we are calling on start method.
    override fun onStart() {
        super.onStart()
        if(is_loggedIn.equals("true")){
            val DashboardIntent = Intent(this, Dashboard::class.java)
            startActivity(DashboardIntent)
            finish()
        }

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            }
        }
    }

    private fun alertDialog(title: String, message: String, isFinish: Boolean) {
        AlertDialog.Builder(this).setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                if(isFinish) {
                    finish()
                }
            }
            .setCancelable(false).show()
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        println("Email Validity Check")
        println(target);
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun RegisterMe(txtRegister : TextView){
        txtRegister.setOnClickListener {
            println("CLICK REGISTER!")
            startActivity(Intent(this, RegisterActivity::class.java))
//            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.CAMERA) ===
                                PackageManager.PERMISSION_GRANTED)) {
//                        Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
    override fun onBackPressed() {

    }
}


