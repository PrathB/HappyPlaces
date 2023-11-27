package com.test.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.happyplaces.utils.SwipeToEditCallback
import com.test.happyplaces.adapters.HappyPlaceAdapter
import com.test.happyplaces.database.DatabaseSingleton
import com.test.happyplaces.database.PlaceModel
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

                    val placesAdapter = HappyPlaceAdapter(it)
                    binding?.rvPlaces?.adapter = placesAdapter

                    placesAdapter.setOnClickListener(object: HappyPlaceAdapter.OnClickListener{
                        override fun onCLick(position: Int, model: PlaceModel) {
                            val intent = Intent(this@MainActivity,
                                HappyPlaceDetailActivity::class.java)
                            intent.putExtra(Extra_Place_Details,model)
                            startActivity(intent)
                        }
                    })

                    val editSwipeHandler = object : SwipeToEditCallback(this@MainActivity){
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val adapter = binding?.rvPlaces?.adapter as HappyPlaceAdapter
                            adapter.notifyEditItem(this@MainActivity,viewHolder.adapterPosition,
                                ADD_PLACE_ACTIVITY_REQUEST_CODE)
                        }
                    }
                    val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                    editItemTouchHelper.attachToRecyclerView(binding?.rvPlaces)
                }
            }
        }
    }

    companion object{
        var Extra_Place_Details = "extra_place_details"
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }
}