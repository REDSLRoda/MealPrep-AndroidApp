package com.example.mealprep

import android.annotation.SuppressLint
import android.icu.util.Measure
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

class SearchByIngredient : AppCompatActivity() {

    lateinit var  editText: EditText

    lateinit var displayInstruction: TextView
    var meal = mutableListOf<JSONObject>()



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_ingredient)

        editText = findViewById(R.id.editTextTextPersonName)
        displayInstruction = findViewById(R.id.textView3)
        var mealIDList = mutableListOf<String>()

        val ingredientStringBuilder = StringBuilder()
        val allIngredient = StringBuilder()

        val db = Room.databaseBuilder(this, AppDatabase::class.java,
            "mydatabase").build()
        val MealDao = db.mealDao()


        val search = findViewById<Button>(R.id.button4)
        val save = findViewById<Button>(R.id.button5)

        search.setOnClickListener {
            runBlocking {
                launch {
                    withContext(Dispatchers.IO) {
                        ingredientStringBuilder.clear()

                        val urlIngredientString = "https://www.themealdb.com/api/json/v1/1/filter.php?i=" + editText.text.toString().replace(" ","%20")
                        val urlIngredient = URL(urlIngredientString)
                        val ingredientConnection: HttpURLConnection = urlIngredient.openConnection() as HttpsURLConnection
                        val reader = BufferedReader(InputStreamReader(ingredientConnection.inputStream))
                        var eachLine: String? = reader.readLine()
                        while (eachLine != null) {
                            ingredientStringBuilder.append(eachLine + "\n")
                            eachLine = reader.readLine()
                        }
                        mealIDList = parseJSON(ingredientStringBuilder)
                    }
                }
            }
            runBlocking {
                launch {
                    withContext(Dispatchers.IO){
                        allIngredient.clear()
                        for (i in  0 until mealIDList.size){
                            launch {
                                withContext(Dispatchers.IO){

                                    val mealUrl = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + mealIDList[i]
                                    val urlIngredient2 = URL(mealUrl)
                                    val mConnection: HttpURLConnection = urlIngredient2.openConnection() as HttpsURLConnection
                                    val mreader = BufferedReader(InputStreamReader(mConnection.inputStream))
                                    var mLine : String? = mreader.readLine()

                                    while (mLine!= null){
                                        allIngredient.append(mLine + "\n")

                                        mLine = mreader.readLine()

                                    }
                                    parseJsonID(allIngredient)



                                }
                            }
                        }

                    }
                }
            }
        }
        /**/
        save.setOnClickListener {
            runBlocking {
                launch {
                    withContext(Dispatchers.IO) {

                        val json = JSONObject(allIngredient.toString())

                        val jsonArray: JSONArray = json.getJSONArray("meals")

                        launch {
                            val IngredientString = StringBuilder()
                            val MeasureString = StringBuilder()

                            for ( j in  0 until jsonArray.length()){
                                val mealList: JSONObject = jsonArray[j] as JSONObject

                                for (k in mealList.keys()){
                                    if (k.contains("Ingredient")){
                                        IngredientString.append("${mealList.get(k)}")
                                    }
                                    if (k.contains("Measure")){
                                        MeasureString.append("${mealList.get(k)}")
                                    }
                                }
                                val Store = Meal(
                                    mealList.get("idMeal").toString().toInt(),mealList.get("strMeal").toString(),mealList.get("strCategory").toString(),mealList.get("strArea").toString(),mealList.get("strDrinkAlternate").toString(),mealList.get("strInstructions").toString(),mealList.get("strTags").toString(),mealList.get("strYoutube").toString(),mealList.get("strSource").toString(),mealList.get("strMealThumb").toString(),mealList.get("strCreativeCommonsConfirmed").toString(),mealList.get("dateModified").toString(),mealList.toString(),mealList.toString()
                                )
                                MealDao.insertMeals(Store)
                            }
                        }




                    }
                    Toast.makeText(applicationContext,
                        "Data added to Database", Toast.LENGTH_SHORT).show()
                }
                }
            }


        }


    private fun parseJSON(ingredientStringBuilder: StringBuilder):MutableList<String> {
        val json = JSONObject(ingredientStringBuilder.toString())

       val allMeals = mutableListOf<String>()

        val jsonArray: JSONArray = json.getJSONArray("meals")

        for(i in 0 until jsonArray.length()){
            val meal: JSONObject = jsonArray[i] as JSONObject

            val mealTitle = meal["idMeal"] as String

            allMeals.add(mealTitle)
            Log.d("MealName", mealTitle)
        }


        return allMeals
    }

    private fun parseJsonID(idStringBuilder: StringBuilder){
        val json = JSONObject(idStringBuilder.toString())
        meal.add(json)
        val jsonArray: JSONArray = json.getJSONArray("meals")
        val allIngredient = StringBuilder()
        for (i in 0 until jsonArray.length()){
            val mealList: JSONObject = jsonArray[i] as JSONObject
            allIngredient.append(mealList.toString())
            allIngredient.append("\n\n")

            Log.d("MealName",allIngredient.toString())
        }


        val formattedString = mutableListOf<String>()

        val stringBuilder = StringBuilder()
        // get the meals json arrays
        val meals = json.getJSONArray("meals")
        // Get the appropriate strings for each meal and add them to the string builder.


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
            // API's maximum ingredient count is 20.
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

            // the string's final newline and comma should be eliminated.
            if (mealString.isNotEmpty()) {
                mealString.deleteCharAt(mealString.length - 1)
                mealString.deleteCharAt(mealString.length - 1)
            }
            // append the results
            formattedString.add("{$mealString}\n")
            stringBuilder.append("$mealString\n")
        }

        //return formattedString
        runOnUiThread {

            displayInstruction.text = stringBuilder.toString()
        }


    }
    override fun onSaveInstanceState(outState: Bundle) {
        // save the needed information when screen rotates
        super.onSaveInstanceState(outState)
        val allIngredient = StringBuilder()
        outState.putString("jsonArray", allIngredient.toString())


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // restore the relevant data

        super.onRestoreInstanceState(savedInstanceState)
        displayInstruction.text = savedInstanceState.getString("displayText", "")
        val jsonString = savedInstanceState.getString("jsonArray")
        meal = mutableListOf<JSONObject>()



    }




}














