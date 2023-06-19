package com.example.mealprep
import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


// This website was made so that users may conduct database searches.
class Search : AppCompatActivity() {
    // ArrayList of class ItemsViewModel
    private var data = ArrayList<ItemsViewModel>()
    private var recyclerView: RecyclerView? = null

    //to see if a search is being conducted on rotate
    private var searching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // adjust the content view to reflect the activity of looking for food
        setContentView(R.layout.activity_searchfromdb)
        // make the search-meals button's variable.
        val btnSearch = findViewById<Button>(R.id.button6)
        //The edit text area is where we retrieve the user's search terms.
        val userSearchTerm = findViewById<EditText>(R.id.editTextdatabase2)
        // This is the recycling view that we employ to show the picture and the data.

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        // As a result, a vertical arrangement is produced Manager.
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerView = recyclerview

        // create a connection to the db
        val db = Room.databaseBuilder(this, AppDatabase::class.java,
            "mydatabase").build()



        // Extract the search word the user enters and trim to remove any unnecessary space

        if (savedInstanceState != null) {
            data = savedInstanceState.getSerializable("dataList") as ArrayList<ItemsViewModel>
            val adapter = CustomAdapter(data)
            if (recyclerView != null) {
                recyclerView!!.layoutManager?.onRestoreInstanceState(
                    savedInstanceState.getParcelable(
                        "scrollPosition"
                    )
                )
            }
            searching = savedInstanceState.getBoolean("searching")
            if (searching) {
                btnSearch.performClick()
            }


            // Adapter setting using the recyclerview
            recyclerview.adapter = adapter


        }

        // the button search should have a on click listener set.
        btnSearch.setOnClickListener {
            val searchCriteria = userSearchTerm.text.trim().toString()
            searching = true

            if (!checkForInternet(applicationContext)) {
                Toast.makeText(applicationContext, "No Internet", Toast.LENGTH_SHORT).show()
            }
            // We must run block because we will be accessing the internet.
            runBlocking {
                launch {
                    // The use of the internet is seen as an IO activity.
                    data.clear()
                    withContext(Dispatchers.IO) {

                        data = search(data, searchCriteria, db)
                    }
                    if (data.isNotEmpty()) {
                        runOnUiThread {
                            // set the data to custom adapter
                            val adapter = CustomAdapter(data)

                            // Setting the Adapter with the recyclerview
                            recyclerview.adapter = adapter
                            searching = false


                        }
                    } else {
                        Toast.makeText(applicationContext, "No meal found", Toast.LENGTH_SHORT).show()
                    }

                }



            }


        }


    }

    private suspend fun search(
        data: ArrayList<ItemsViewModel>,
        userSearchTerm: String, db: AppDatabase
    ): ArrayList<ItemsViewModel> {
        // clear the data just in case
        data.clear()
        // create a variable of our database

        // call the query with the criteria user wants
        val results = db.mealDao().searchForTerm(userSearchTerm)
        Log.d("results", results.toString())
        // we check if the results are empty or not
        Log.d("size", results.isEmpty().toString())
        if (results.isEmpty()) {
            // if empty return a msg to user
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    "No Meal or Ingredient found",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        } else {
            // we loop through each meal found as necessary
            for (meal in results) {
                val formattedResults = formatMeals(meal)

                // assign the url of the current meal to a imageURL variable
                val imageURl = meal.MealThumb
                //as we are accessing the internet re run in a blocking statement
                runBlocking {
                    withContext(Dispatchers.IO) {

                        // continue in a try catch block to handle exceptions
                        try {

                            Log.d("checkImageURl", imageURl.toString())
                            // create URL from the above string
                            val url = URL(imageURl)
                            // create an http connection
                            val connection = url.openConnection() as HttpURLConnection
                            // allow the connection to get an input
                            connection.doInput = true
                            // connect to the URL
                            connection.connect()
                            // get the input values
                            val input = connection.inputStream
                            // since this is an image we put to a bitmap
                            val bitmap = BitmapFactory.decodeStream(input)

                            // add the view model object to the list
                            data.add(ItemsViewModel(bitmap, formattedResults))
                            // close the stream and disconnect
                            input.close()
                            connection.disconnect()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            val defaultImage =
                                BitmapFactory.decodeResource(resources, R.drawable.nothing)
                            data.add(ItemsViewModel(defaultImage, formattedResults))
                        }

                    }


                }


            }
        }
        //return the data
        return data
    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection

        // check if the network is active or not
        val network = connectivityManager.activeNetwork ?: return false

        //checks the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // if there is a wifi connection send true
            // cannot check if there is actual internet working
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // if the is a mobile data connection return true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    }


    // the below method re arranges the meal results into the format in the cwk

    private fun formatMeals(mealsList: Meal): StringBuilder {
        val sb = java.lang.StringBuilder()
        sb.append("\"Meal\":\"${mealsList.meal}\",\n")
        sb.append("\"DrinkAlternate\":\"${mealsList.DrinkAlternate}\",\n")
        sb.append("\"Category\":\"${mealsList.category}\",\n")
        sb.append("\"Area\":\"${mealsList.area}\",\n")
        sb.append("\"Instructions\":\"${mealsList.Instructions}\",\n")
        sb.append("\"Tags\":\"${mealsList.Tags}\",\n")
        sb.append("\"Youtube\":\"${mealsList.youtube}\",\n")

        if (mealsList.Ingredients?.isNotEmpty() == true)
            for (x in 0 until mealsList.Ingredients.length) {
                sb.append("\"Ingredient$x\":\"${mealsList.Ingredients[x]}\",\n")

            }


        if (mealsList.Measure?.isNotEmpty() == true)
            for (x in 0 until mealsList.Measure.length) {
                sb.append("\"Ingredient$x\":\"${mealsList.Measure[x]}\",\n")

            }

        sb.append("\n")



        return sb
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("dataList", data)
        if (recyclerView != null)
            outState.putParcelable(
                "scrollPosition",
                recyclerView!!.layoutManager?.onSaveInstanceState()
            )
        outState.putBoolean("searching", searching)


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        data = savedInstanceState.getSerializable("dataList") as ArrayList<ItemsViewModel>


    }
}

