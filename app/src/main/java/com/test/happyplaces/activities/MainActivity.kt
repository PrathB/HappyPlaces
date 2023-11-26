package com.test.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.happyplaces.adapters.HappyPlaceAdapter
import com.test.happyplaces.database.DatabaseSingleton
import com.test.happyplaces.database.PlacesDao
import com.test.happyplaces.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val database = DatabaseSingleton.getInstance(applicationContext)
        val placesDao = database.placesDao()
        setUpRecyclerView(placesDao)

        binding?.fabAddHappyPlace?.setOnClickListener{
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setUpRecyclerView(userDao : PlacesDao) {
        lifecycleScope.launch {
            userDao.fetchAllPlaces().collect(){
                if(it.isEmpty()){
                    binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
                    binding?.rvPlaces?.visibility = View.GONE
                }else{
                    binding?.tvNoRecordsAvailable?.visibility = View.GONE
                    binding?.rvPlaces?.visibility = View.VISIBLE
                    binding?.rvPlaces?.layoutManager = LinearLayoutManager(
                        this@MainActivity,LinearLayoutManager.VERTICAL,false)
                    binding?.rvPlaces?.adapter = HappyPlaceAdapter(it)
                }
            }
        }
    }
}