package com.miongapps.simplecrud

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.imageview.ShapeableImageView
import java.io.ByteArrayOutputStream

class EditUserActivity : AppCompatActivity() {
    private lateinit var etEditName : EditText
    private lateinit var etEditEmail: EditText
    private lateinit var btnSaveEdit : Button
    private lateinit var btnBackEdit : Button
    private lateinit var imgEditProfileImage : ShapeableImageView
    var pos : Int = 0
    var userID : String? = null

    // Define the pic id
    private var pic_id = 123
    val REQUEST_CODE = 200
    var Base64ImageString : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        etEditName = findViewById(R.id.etEditName)
        etEditEmail = findViewById(R.id.etEditEmail)
        imgEditProfileImage = findViewById(R.id.imgEditProfileImage)

        btnSaveEdit = findViewById(R.id.btnSaveEdit)
        btnSaveEdit.setOnClickListener{
            println("Save!")
            val EmployeeName = etEditName.text.toString()
            var EmployeeEmail = etEditEmail.text.toString()
            if(CommonUtils().isValidEmail(EmployeeEmail)){
                val users = UsersDataClass(0, userID.toString(), EmployeeName, EmployeeEmail, Base64ImageString.toString())
                saveEditData(users, pos);
            }else{
                Toast.makeText(this, "Error: Invalid Email Format!", Toast.LENGTH_SHORT).show()
            }

        }
        btnBackEdit = findViewById(R.id.btnBackEdit)
        btnBackEdit.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }

        imgEditProfileImage.setOnClickListener {
            capturePhoto()
        }
        // load or set all data to fields
        setData()
    }

    private fun setData(){
        var intent_extras = intent.extras
        var employeeNameEdit = intent_extras!!.getString("fullname")
        var employeeEmailEdit = intent_extras!!.getString("email")
        var employeePhotoEdit = intent_extras!!.getString("photo")
        var employeeImageId = intent_extras!!.getInt("imageId")
        pos = intent_extras!!.getInt("position")
        userID = intent_extras!!.getString("userId")
//        Toast.makeText(this, userID, Toast.LENGTH_LONG).show()
        etEditName.setText(employeeNameEdit)
        etEditEmail.setText(employeeEmailEdit)

        if(employeePhotoEdit.equals("null")){
            imgEditProfileImage.setImageResource(employeeImageId)
        }else{
            val decodedString: ByteArray = Base64.decode(employeePhotoEdit, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            imgEditProfileImage.setImageBitmap(decodedByte)
        }
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
        ApiRequest().updateEmployeeWithPhoto(currentItem, ""){ result ->
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }
                startActivity(Intent(this, Dashboard::class.java))
                finish()
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
            imgEditProfileImage.setImageBitmap(data.extras?.get("data") as Bitmap)
            val byteArrayOutputStream = ByteArrayOutputStream()
//            val bitmap = BitmapFactory.decodeResource(resources, imgProfilePic.id)
            val bitmap = data.extras?.get("data") as Bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
            val imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            print("IMAGESTRING: $imageString")
            Base64ImageString = imageString

        }
    }
}