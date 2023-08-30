package com.miongapps.simplecrud

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream


class EditProfileActivity : AppCompatActivity() {

    private lateinit var etProfileName : EditText
    private lateinit var etProfileEmail : EditText
    private lateinit var etProfilePassword : EditText
    private lateinit var btnProfileSave : Button
    private lateinit var btnProfileBack : Button
    private lateinit var imgEditProfileImage : ShapeableImageView

    var sharedPrefs : SharedPreferences? = null
    var PREFS_KEY = "pref"
    var LOGGED_IN = "loggedIn"
    var LOGGED_USERFN = "loggedUserFN"
    var LOGGED_USEREM = "loggedUserEM"
    var LOGGED_USERPH = "loggedUserPH"
    var LOGGED_USERID = "loggedUserID"
    var is_loggedIn = ""

    // Define the pic id
    private var pic_id = 123
    val REQUEST_CODE = 200
    var Base64ImageString : String? = null

    var userProfileName : String? = null
    var userProfileEmail : String? = null
    var userProfilePhoto : String? = null
    var userProfileId : String? = null

    private lateinit var lblUserFullName : TextView
    private lateinit var circleImgProfilePic : CircleImageView
    private lateinit var lblUserEmail : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        etProfileName = findViewById(R.id.etProfileName)
        etProfileEmail = findViewById(R.id.etProfileEmail)
        etProfilePassword = findViewById(R.id.etProfilePassword)
        btnProfileSave = findViewById(R.id.btnProfileSave)
        btnProfileBack = findViewById(R.id.btnProfileBack)
        imgEditProfileImage = findViewById(R.id.imgEditProfileImage)

        sharedPrefs = getSharedPreferences(PREFS_KEY, MODE_PRIVATE)

        setData()

        btnProfileSave.setOnClickListener {
            val EmployeeName = etProfileName.text.toString()
            var EmployeeEmail = etProfileEmail.text.toString()
            var EmployeePassword = etProfilePassword.text.toString()
//            Toast.makeText(this, "" + userProfileId, Toast.LENGTH_SHORT).show()
            if(CommonUtils().isValidEmail(EmployeeEmail)){
                val users = UsersDataClass(0, userProfileId.toString(), EmployeeName, EmployeeEmail, Base64ImageString.toString())
                saveProfile(users, EmployeePassword, 0);
            }else{
                Toast.makeText(this, "Error: Invalid Email Format!", Toast.LENGTH_SHORT).show()
            }
        }

        btnProfileBack.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }

        imgEditProfileImage.setOnClickListener {
            capturePhoto()
        }
    }

    private fun setData(){
        var intent_extras = intent.extras
        userProfileName = intent_extras!!.getString("fullname")
        userProfileEmail = intent_extras!!.getString("email")
        userProfilePhoto = intent_extras!!.getString("photo")
        userProfileId = intent_extras!!.getString("userId")

        Base64ImageString = userProfilePhoto
        etProfileName.setText(userProfileName)
        etProfileEmail.setText(userProfileEmail)

        if(userProfilePhoto.equals("null")){
            imgEditProfileImage.setImageResource(R.drawable.profile_5004140)
        }else{
            val decodedString: ByteArray = Base64.decode(userProfilePhoto, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            imgEditProfileImage.setImageBitmap(decodedByte)
        }
    }

    private fun saveProfile(currentItem: UsersDataClass, password: String, position: Int){
//        val intent = Intent(this, Dashboard::class.java)
        // API CALL TO SAVE HERE
        ApiRequest().updateEmployeeWithPhoto(currentItem, password){ result ->
            if(result.equals("Email Already Exists!")){
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }
            }else{
                val editor: SharedPreferences.Editor = sharedPrefs!!.edit()
                editor.putString(LOGGED_USERFN, etProfileName.text.toString())
                editor.putString(LOGGED_USEREM, etProfileEmail.text.toString())
                editor.putString(LOGGED_USERPH, Base64ImageString.toString())
                editor.apply()
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }
            }
//            startActivity(Intent(this, Dashboard::class.java))
//            finish()
        }
    }

    fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
            imgEditProfileImage.setImageBitmap(data.extras?.get("data") as Bitmap)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val bitmap = data.extras?.get("data") as Bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
            val imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            print("IMAGESTRING: $imageString")
            Base64ImageString = imageString
        }
    }
}