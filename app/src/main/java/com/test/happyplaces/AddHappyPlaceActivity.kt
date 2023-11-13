package com.test.happyplaces

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX
import com.test.happyplaces.databinding.ActivityAddHappyPlaceBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddHappyPlaceActivity : AppCompatActivity() {
    private var binding : ActivityAddHappyPlaceBinding? = null
    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener : DatePickerDialog.OnDateSetListener
    private lateinit var galleryImageResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var cameraImageResultLauncher : ActivityResultLauncher<Intent>
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,month)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            updateDateInView()
        }

        binding?.etDate?.setOnClickListener {
            DatePickerDialog(
                this@AddHappyPlaceActivity,
                dateSetListener,calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()

        }
        binding?.tvAddImage?.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            pictureDialog.setItems(arrayOf("Select photo from gallery","Capture photo from camera")){
                    _, which ->
                when(which){
                    0 -> selectPhotoFromGallery()
                    1-> capturePhoto()
                }

            }
            pictureDialog.show()
        }
        registerOnActivityForResult()
    }

    private fun registerOnActivityForResult(){
//        this fxn will initialize the gallery image result launcher
        galleryImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
                    if(result.resultCode == Activity.RESULT_OK){
                        val imgdata : Intent? = result.data
                        if(imgdata!= null) {
                            val imgURI = imgdata.data
                            try {
                                binding?.ivPlaceImage?.setImageURI(imgURI)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(this, "Failed to load image from gallery", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
        }
//        this fxn will initialize the camera image result launcher
        cameraImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
                if(result.resultCode == Activity.RESULT_OK){
                    val imgdata : Intent? = result.data
                    if(imgdata!=null) {
                        val thumbNail: Bitmap = imgdata.extras?.get("data") as Bitmap
                        try {
                            binding?.ivPlaceImage?.setImageBitmap(thumbNail)
                        }catch(e:IOException){
                            e.printStackTrace()
                            Toast.makeText(this, "Failed to load image from camera", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun selectPhotoFromGallery() {
        PermissionX.init(this@AddHappyPlaceActivity)
            .permissions(Manifest.permission.READ_MEDIA_IMAGES)
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "Please grant permission to read media images in " +
                        "App Settings for additional functionality.", "OK", "Cancel")
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
//                  Intent to direct us to the gallery to select an image
                    val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryImageResultLauncher.launch(galleryIntent)
                }else{
                    Toast.makeText(this, "Permission Denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun capturePhoto(){
        PermissionX.init(this@AddHappyPlaceActivity)
            .permissions(Manifest.permission.CAMERA)
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "Please grant camera permission " +
                        "in App Settings for additional functionality.", "OK", "Cancel")
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
//                  Intent to direct us to the camera to capture an image
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraImageResultLauncher.launch(cameraIntent)
                }else{
                    Toast.makeText(this, "Permission Denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateDateInView(){
        val format =  "dd.MM.yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        binding?.etDate?.setText(sdf.format(calendar.time).toString())
    }
}