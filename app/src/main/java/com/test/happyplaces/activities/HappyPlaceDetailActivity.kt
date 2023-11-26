package com.test.happyplaces.activities

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.test.happyplaces.database.PlaceModel
import com.test.happyplaces.databinding.ActivityHappyPlaceDetailBinding

class HappyPlaceDetailActivity : AppCompatActivity() {
    private var binding: ActivityHappyPlaceDetailBinding? = null
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var happyPlaceDetailModel : PlaceModel? = null
        if(intent.hasExtra(MainActivity.Extra_Place_Details)){
            happyPlaceDetailModel = intent.getSerializableExtra(MainActivity.Extra_Place_Details,PlaceModel::class.java)
        }

        if(happyPlaceDetailModel!=null){
            setSupportActionBar(binding?.toolbarPlaceDetail)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = happyPlaceDetailModel.title
            binding?.toolbarPlaceDetail?.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            binding?.ivPlaceImage?.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            binding?.tvDescription?.text = happyPlaceDetailModel.description
            binding?.tvLocation?.text = happyPlaceDetailModel.location

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(binding!=null){
            binding = null
        }
    }
}