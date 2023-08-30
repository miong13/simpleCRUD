package com.miongapps.simplecrud

import android.app.Activity
import android.app.AlertDialog
import android.text.TextUtils
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity

class CommonUtils {
    fun isValidEmail(target: CharSequence?): Boolean {
        println("Email Validity Check")
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun alertDialog(title: String, message: String, isFinish: Boolean, currentActivity : AppCompatActivity) {
        AlertDialog.Builder(currentActivity).setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                if(isFinish) {
                    currentActivity.finish()
                }
            }
            .setCancelable(false).show()
    }

    fun compareUserPass(password: String, confirmPassword: String) : Boolean{
        if(password == confirmPassword){
            return true
        }else{
            return false
        }
    }
}