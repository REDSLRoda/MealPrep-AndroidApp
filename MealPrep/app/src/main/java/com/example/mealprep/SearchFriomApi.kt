package com.example.mealprep


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlinx.coroutines.*

class SearchFriomApi : AppCompatActivity() {


    private var mealTextInfo=" "
    var searching=false
    private lateinit var scrollView: ScrollView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serach_from_api)
        val txt=findViewById<TextView>(R.id.textView2)
        scrollView=findViewById(R.id.scrollView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility= View.GONE


        val btnRMeal=findViewById<Button>(R.id.btn_retrievemeal)
        val btnIngredient=findViewById<EditText>(R.id.txtSearch)

        txt.maxLines = Integer.MAX_VALUE;



        btnRMeal.setOnClickListener {
            mealTextInfo=""
            txt.text=""
            if (checkForInternet(this)){
                progressBar.visibility= View.VISIBLE



                val myScope = CoroutineScope(Dispatchers.Main)

// launch the background thread coroutine to carry out the blocking task.
                myScope.launch(Dispatchers.IO) {



                    val apiLink = "https://www.themealdb.com/api/json/v1/1/search.php?s="
                    val mealStrings = btnIngredient.text.toString().substringBefore(" ")





                    runBlocking {
                        launch {
                            runOnUiThread{ progressBar.visibility = View.VISIBLE
                                txt.text=""}

                            withContext(Dispatchers.IO) {
                                val meals=searchMealr(apiLink,mealStrings)

                                runOnUiThread{
                                    txt.append(formatFile(meals))
                                    progressBar.visibility = View.GONE
                                    txt.visibility= View.VISIBLE
                                    mealTextInfo=txt.text.toString()
                                    Log.d("why",mealTextInfo)


                                }


                            }


                        }



                    }






                }}else{
                Toast.makeText(this,"No network connectivity", Toast.LENGTH_SHORT).show()

            }


        }
        if (savedInstanceState!=null){
            runOnUiThread{
                txt.text=mealTextInfo
                txt.text=savedInstanceState.getString("displayText")
                txt.invalidate()
                txt.visibility= View.VISIBLE
                scrollView.scrollY=savedInstanceState.getInt("position")
                progressBar.visibility= View.GONE
                if (searching) {
                    btnRMeal.isClickable = true
                    btnRMeal.performClick()
                }
            }

        }


    }
    private fun searchMealr(
        apiLink: String,
        mealIngredient: String
    ): StringBuilder {
        return try {
            val fullUrl = URL(apiLink + (mealIngredient.trim().replace(" ", "_")))
            val fullUrlCon: HttpURLConnection = fullUrl.openConnection() as HttpURLConnection
            val bf = BufferedReader(InputStreamReader(fullUrlCon.inputStream))
            val sb = StringBuilder()
            var line: String? = bf.readLine()
            while (line != null) {
                sb.append(line + "\n")
                line = bf.readLine()
            }
            Log.d("Api return check", sb.toString())
            sb
        } catch (e: Exception) {
            Log.e("Api error", "Error searching meal: ${e.message}", e)
            return StringBuilder()
        }
    }
    private fun formatFile(link: java.lang.StringBuilder): StringBuilder {

        val formattedString = mutableListOf<String>()
        //  val formattedString = StringBuilder()

        val json = JSONObject(link.toString())
        val stringBuilder = StringBuilder()
        val meals = json.getJSONArray("meals")


        for (i in 0 until meals.length()) {
            val meal = meals.getJSONObject(i)
            val mealString = StringBuilder()

            mealString.append("\"Meal\":\"${meal.getString("strMeal")}\",\n")
            mealString.append("\"DrinkAlternate\":\"${meal.getString("strDrinkAlternate")}\",\n")
            mealString.append("\"Category\":\"${meal.getString("strCategory")}\",\n")
            mealString.append("\"Area\":\"${meal.getString("strArea")}\",\n")
            mealString.append("\"Instructions\":\"${meal.getString("strInstructions")}\",\n")
            mealString.append("\"Tags\":\"${meal.getString("strTags")}\",\n")
            mealString.append("\"Youtube\":\"${meal.getString("strYoutube")}\",\n")
            for (k in 1..20) {
                if (meal.getString("strIngredient$k").isNotEmpty()) {
                    mealString.append("\"Ingredient$k\":\"${meal.getString("strIngredient$k")}\",\n")

                }
            }
            for (k in 1..20) {
                if (meal.getString("strMeasure$k").isNotEmpty()) {
                    mealString.append("\"Measure$k\":\"${meal.getString("strMeasure$k")}\",\n")
                }
            }


            // remove the last comma and newline characters from the end of the string
            if (mealString.isNotEmpty()) {
                mealString.deleteCharAt(mealString.length - 1)
                mealString.deleteCharAt(mealString.length - 1)
            }


            formattedString.add("{$mealString}\n")
            stringBuilder.append("$mealString\n")
            stringBuilder.append(" ")
            stringBuilder.append("\n")
        }

        //return formattedString
        return stringBuilder
    }

    private fun checkForInternet(context: Context): Boolean {

        // the connectivity manager service should be notified of your activities.
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if android version is more than M
        // capabilities of the network to determine the type of network used for the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // returns a Network object that corresponds to the default data network that is currently active.
            val network = connectivityManager.activeNetwork ?: return false

            //representation of a network's functional capabilities.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // shows that WiFi has network connectivity or that this network employs a Wi-Fi transport.
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // shows that this network employs a cellular mode of transport. has network connectivity, either cellular
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)
        outState.putBoolean("searchPressed", searching)
        outState.putString("displayText", mealTextInfo)
        outState.putInt("position",scrollView.scrollY)




    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        super.onRestoreInstanceState(savedInstanceState)
        searching = savedInstanceState.getBoolean("searchPressed", false)
        mealTextInfo = savedInstanceState.getString("displayText", "")
        Log.d("check123",mealTextInfo)



    }









}