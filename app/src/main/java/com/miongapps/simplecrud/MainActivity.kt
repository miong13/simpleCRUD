package com.miongapps.simplecrud

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    var is_loggedIn = ""
    var resultApi : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide() // hide app title bar

        sharedPrefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        is_loggedIn = sharedPrefs!!.getString(LOGGED_IN, "").toString()

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
                    ApiRequest().loginEmployee(email, pword) { mess ->
//                    println("MARKRAMOS REQUEST RESULT - $mess")
                        if(mess.equals("error")){
                            ERROR_MSG = "Wrong Email and Password Combination!"
//                        ToastAlert(ERROR_MSG)
//                        tvAlertMessages.text = ERROR_MSG + ""

                            Handler(Looper.getMainLooper()).post {
                                // write your code here
                                alertDialog("Error",  ERROR_MSG + "", false)
                            }
                        }else{
                            val editor: SharedPreferences.Editor = sharedPrefs!!.edit()
                            editor.putString(LOGGED_IN, "true")
                            editor.putString(LOGGED_USERFN, mess)
                            editor.apply()

                            val DashboardIntent = Intent(this, Dashboard::class.java)
                            startActivity(DashboardIntent)
                            finish()
                        }
                    }
                }else{
                    ToastAlert("Invalid Email Format!")
                }

            }
        }

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
}


