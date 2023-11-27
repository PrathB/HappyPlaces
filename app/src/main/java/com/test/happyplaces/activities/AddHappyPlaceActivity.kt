package com.test.happyplaces.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.permissionx.guolindev.PermissionX
import com.test.happyplaces.database.DatabaseSingleton
import com.test.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.test.happyplaces.database.PlaceModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddHappyPlaceActivity : AppCompatActivity() {
    private var binding : ActivityAddHappyPlaceBinding? = null
    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener : DatePickerDialog.OnDateSetListener

    private lateinit var galleryImageResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var cameraImageResultLauncher : ActivityResultLauncher<Intent>

    private var savedImage : Uri? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private var mHappyPlaceDetails :  PlaceModel? = null
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val database = DatabaseSingleton.getInstance(applicationContext)
        val placesDao = database.placesDao()

        setSupportActionBar(binding?.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if(intent.hasExtra(MainActivity.Extra_Place_Details)){
           mHappyPlaceDetails = intent.getSerializableExtra(MainActivity.Extra_Place_Details,PlaceModel::class.java)
        }

        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,month)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            updateDateInView()
        }
        updateDateInView()

        if(mHappyPlaceDetails!=null){
            supportActionBar?.title = "Edit Happy Place"
            binding?.etTitle?.setText(mHappyPlaceDetails!!.title)
            binding?.etDescription?.setText(mHappyPlaceDetails!!.description)

            savedImage = Uri.parse(mHappyPlaceDetails!!.image)
            binding?.ivPlaceImage?.setImageURI(savedImage)

            binding?.etDate?.setText(mHappyPlaceDetails!!.date)
            binding?.etLocation?.setText(mHappyPlaceDetails!!.location)
            latitude = mHappyPlaceDetails!!.latitude
            longitude = mHappyPlaceDetails!!.longitude

            binding?.btnSave?.text = "UPDATE"
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
                    1 -> capturePhoto()
                }

            }
            pictureDialog.show()
        }

        binding?.btnSave?.setOnClickListener {
            when{
                binding?.etTitle?.text?.isBlank()!! -> {
                    Toast.makeText(this,"Please enter a title", Toast.LENGTH_SHORT).show()
                }
                binding?.etDescription?.text?.isBlank()!! ->{
                    Toast.makeText(this,"Please enter a description", Toast.LENGTH_SHORT).show()
                }
                binding?.etLocation?.text?.isBlank()!! ->{
                    Toast.makeText(this,"Please enter a location", Toast.LENGTH_SHORT).show()
                }
                savedImage == null ->{
                    Toast.makeText(this,"Please select an image", Toast.LENGTH_SHORT).show()
                }

                else ->{
                    val saveModel = PlaceModel(if(mHappyPlaceDetails == null) 0 else mHappyPlaceDetails!!.id,binding?.etTitle?.text?.toString()!!,savedImage.toString(),
                        binding?.etDescription?.text?.toString()!!,binding?.etDate?.text?.toString()!!,
                        binding?.etLocation?.text?.toString()!!,latitude, longitude)

                    lifecycleScope.launch {
                        placesDao.insert(saveModel)
                    }
                    finish()
                }
            }
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
                            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imgURI)
                            savedImage = saveImgToInternalStorage(bitmap)
                            try {
                                Log.i("image stored","path: $savedImage")
                                binding?.ivPlaceImage?.setImageBitmap(bitmap)
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
                        val thumbnail: Bitmap = imgdata.extras?.get("data") as Bitmap
                        savedImage = saveImgToInternalStorage(thumbnail)
                        try {
                            Log.i("captured image stored","path: $savedImage")
                            binding?.ivPlaceImage?.setImageBitmap(thumbnail)
                        }catch(e:IOException){
                            e.printStackTrace()
                            Toast.makeText(this, "Failed to load image from camera", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun saveImgToInternalStorage(bitmap: Bitmap) : Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("IMAGE_DIRECTORY", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        try{
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        } catch(e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
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

    override fun onDestroy() {
        super.onDestroy()
        if(binding != null){
            binding = null
        }
    }
}