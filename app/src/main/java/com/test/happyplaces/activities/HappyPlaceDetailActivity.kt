package com.test.happyplaces.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.test.happyplaces.databinding.ActivityHappyPlaceDetailBinding

class HappyPlaceDetailActivity : AppCompatActivity() {
    private var binding: ActivityHappyPlaceDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarPlaceDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarPlaceDetail?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}