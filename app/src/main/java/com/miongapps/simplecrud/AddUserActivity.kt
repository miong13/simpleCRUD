package com.miongapps.simplecrud

import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.material.imageview.ShapeableImageView
import java.io.ByteArrayOutputStream

class AddUserActivity : AppCompatActivity() {
    private lateinit var etAddName : TextView
    private lateinit var etAddEmail : TextView
    private lateinit var btnAddSave : Button
    private lateinit var btnBackAdd : Button
    private lateinit var imgAddProfileImage : ShapeableImageView

    // Define the pic id
    private var pic_id = 123
    val REQUEST_CODE = 200
    var Base64ImageString : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        imgAddProfileImage = findViewById(R.id.imgAddProfileImage)
        etAddName = findViewById(R.id.etAddName)
        etAddEmail = findViewById(R.id.etAddEmail)
        btnAddSave = findViewById(R.id.btnSaveAdd)

        btnAddSave.setOnClickListener{
            val addEmployeeName = etAddName.text.toString()
            val addEmployeeEmail = etAddEmail.text.toString()
            val addEmployeePhoto = Base64ImageString.toString()
            if(addEmployeeName.isNullOrEmpty() || addEmployeeEmail.isNullOrEmpty()){
                Toast.makeText(this, "Please fill-in all fields!", Toast.LENGTH_LONG).show()
            }else{
                if(CommonUtils().isValidEmail(addEmployeeEmail)) {
                        val employeeData = UsersDataClass(
                            0,
                            "",
                            addEmployeeName,
                            addEmployeeEmail,
                            addEmployeePhoto
                        )
                        ApiRequest().registerEmployeeWithPhoto(employeeData, "test") { result ->
                            if (result.equals("Email Already Exists!")) {
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                                }
                            } else {
                                startActivity(Intent(this, Dashboard::class.java))
                                finish()
                            }
                        }
                }else{
                    Toast.makeText(this, "Error: Invalid Email Format!", Toast.LENGTH_SHORT).show()
                }

            }
        }

        btnBackAdd = findViewById(R.id.btnBackAdd)
        btnBackAdd.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }

        imgAddProfileImage.setOnClickListener{
            capturePhoto()
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
            imgAddProfileImage.setImageBitmap(data.extras?.get("data") as Bitmap)
            val byteArrayOutputStream = ByteArrayOutputStream()
//            val bitmap = BitmapFactory.decodeResource(resources, imgProfilePic.id)
            val bitmap = data.extras?.get("data") as Bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
            val imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            print("IMAGESTRING: $imageString")
            Base64ImageString = imageString
//            txtBase64Image.text = imageString
//            Base64ImageString = txtBase64Image.text.toString()
        }
    }
}