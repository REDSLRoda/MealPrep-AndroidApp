package com.example.mealprep

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {
    var imageIndex = 0// this is a reference to the currently displayed image.

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) {
            if (!savedInstanceState.isEmpty) {
                // Set the image index if one is available as necessary.
                imageIndex = savedInstanceState.getInt("imageIndex")


            }

        }
        var meals = listOf<Meal>()
        val tv = findViewById<TextView>(R.id.tv)
        tv.setText("")

// create the database
        val db = Room.databaseBuilder(this, AppDatabase::class.java,
            "mydatabase").build()
        val MealDao = db.mealDao()
        val addmealbutton : Button = findViewById(R.id.button)
        addmealbutton.text = getString(R.string.add_meals_to_db)
        val ingredientbtn : Button = findViewById(R.id.button2)
        ingredientbtn.text= getString(R.string.search_for_meals_by_ingredient)
        val searchbtn : Button = findViewById(R.id.button3)
        searchbtn.text = getString(R.string.search_for_meals)
        val btnname : Button = findViewById(R.id.btnname)
        btnname.text = getString(R.string.search_by_name)



       ingredientbtn.setOnClickListener {
           val intent = Intent(this, SearchByIngredient::class.java)
           startActivity(intent)
       }
       
        searchbtn.setOnClickListener {
            val intent = Intent(this, Search::class.java)
            startActivity(intent)
        }
        btnname.setOnClickListener {
            val intent = Intent(this, SearchFriomApi::class.java)
            startActivity(intent)
        }


        val builder = AlertDialog.Builder(this)
        addmealbutton.setOnClickListener {

        runBlocking {
            launch {
                val meal = Meal(
                    1,
                    "Sweet and Sour Pork",
                    "Pork",
                    "Chinese",
                    "null",
                    "Preparation\\r\\n1. Crack the egg into a bowl. Separate the egg white and yolk.\\r\\n\\r\\nSweet and Sour Pork\\r\\n2. Slice the pork tenderloin into ips.\\r\\n\\r\\n3. Prepare the marinade using a pinch of salt, one teaspoon of starch, two teaspoons of light soy sauce, and an egg white.\\r\\n\\r\\n4. Marinade the pork ips for about 20 minutes.\\r\\n\\r\\n5. Put the remaining starch in a bowl. Add some water and vinegar to make a starchy sauce.\\r\\n\\r\\nSweet and Sour Pork\\r\\nCooking Inuctions\\r\\n1. Pour the cooking oil into a wok and heat to 190\\u00b0C (375\\u00b0F). Add the marinated pork ips and fry them until they turn brown. Remove the cooked pork from the wok and place on a plate.\\r\\n\\r\\n2. Leave some oil in the wok. Put the tomato sauce and white sugar into the wok, and heat until the oil and sauce are fully combined.\\r\\n\\r\\n3. Add some water to the wok and thoroughly heat the sweet and sour sauce before adding the pork ips to it.\\r\\n\\r\\n4. Pour in the starchy sauce. Stir-fry all the ingredients until the pork and sauce are thoroughly mixed together.\\r\\n\\r\\n5. Serve on a plate and add some coriander for decoration.",
                    "Sweet",
                    "https:\\/\\/www.youtube.com\\/watch?v=mdaBIhgEAMo",
                    "Pork,Egg,Water,Salt,Sugar,SoySauce,Starch,Tomato Puree,Vinegar,Coriander",
                    "200g , 1 ,Dash , 1/2tsp , 1tsp ,10g ,10g ,30g ,10g ,Dash",
                    "null",
                    "https:\\/\\/www.themealdb.com\\/images\\/media\\/meals\\/1529442316.jpg",
                    "null",
                    "null"
                )
                val meal2 = Meal(
                    2,
                    "Chicken Marengo",
                    "Chicken",
                    "French",
                    "null",
                    "Heat the oil in a large flameproof casserole dish and stir-fry the mushrooms until they start to soften. Add the chicken legs and cook briefly on each side to colour them a little.\\r\\nPour in the passata, crumble in the stock cube and stir in the olives. Season with black pepper \\u2013 you shouldn\\u2019t need salt. Cover and simmer for 40 mins until the chicken is tender. Sprinkle with parsley and serve with pasta and a salad, or mash and green veg, if you like.",
                    "null",
                    "https:\\/\\/www.youtube.com\\/watch?v=U33HYUr-0Fw",
                    "Olive Oil,Mushrooms,Chicken Legs,Passata,Chicken Stock Cube,Black Olives,Parsley",
                    "1 tbs , 300g , 4 , 500g , 1 , 100g , Chopped ",
                    "https:\\/\\/www.bbcgoodfood.com\\/recipes\\/3146682\\/chicken-marengo",
                    "https:\\/\\/www.themealdb.com\\/images\\/media\\/meals\\/qpxvuq1511798906.jpg",
                    "null",
                    "null"
                )
                val meal3 = Meal(
                    3,
                    "Leblebi Soup",
                    "Vegetarian",
                    "Tunisian",
                    "null",
                    "Heat the oil in a large pot. Add the onion and cook until translucent.\\r\\nDrain the soaked chickpeas and add them to the pot together with the vegetable stock. Bring to the boil, then reduce the heat and cover. Simmer for 30 minutes.\\r\\nIn the meantime toast the cumin in a small ungreased frying pan, then grind them in a mortar. Add the garlic and salt and pound to a fine paste.\\r\\nAdd the paste and the harissa to the soup and simmer until the chickpeas are tender, about 30 minutes.\\r\\nSeason to taste with salt, pepper and lemon juice and serve hot.",
                    "Soup",
                    "https:\\/\\/www.youtube.com\\/watch?v=BgRifcCwinY",
                    "Olive Oil,Onion,Chickpeas,Vegetable Stock,cumin,Garlic,salt,Harissa Spice,Pepper,Lime",
                    "1 tbs , 300g , 4 , 500g , 1 , 100g , Chopped ",
                    "https:\\/\\/www.bbcgoodfood.com\\/recipes\\/3146682\\/chicken-marengo",
                    "https:\\/\\/www.themealdb.com\\/images\\/media\\/meals\\/x2fw9e1560460636.jpg",
                    "null",
                    "null"
                )
                MealDao.insertMeals(meal, meal2, meal3)
                var meals: List<Meal> = MealDao.getAll()

                /*for (u in meals) {
                        tv.append("\n ${u.meal} ${u.category} ${u.area} ${u.DrinkAlternate} ${u.Instructions} ${u.Tags} ${u.youtube} ${u.Ingredients}")
                    }*/


                builder.setTitle("data saved to db")
                runBlocking {
                    launch {
                        meals = MealDao.getAll() }
                }
                builder.setMessage(meals.toString())
                var ok:String="OK"
                builder.setPositiveButton("Ok") { dialog, which ->
                    Toast.makeText(applicationContext,
                        "Data added to Database", Toast.LENGTH_SHORT).show()
                }


                builder.show()


            }

        }
        }








    }
    private fun startImageSlideshow(
        imgVMeals: ImageView,
        mealImageArray: IntArray,
        duration: Long
    ) {
        // create a runnable task that will occur per duration time
        imgVMeals.postDelayed(object : Runnable {
            override fun run() {
                // we first start the animation with a fade in effect
                imgVMeals.startAnimation(
                    // we call the animation to happen in the current context of the application
                    AnimationUtils.loadAnimation(
                        applicationContext,
                        androidx.appcompat.R.anim.abc_fade_out
                    )
                )

                // Update the ImageView with the next image in the array
                imageIndex = (imageIndex + 1) % mealImageArray.size
                // we set the imageview with the above selected index
                imgVMeals.setImageResource(mealImageArray[imageIndex])

                // repeat the first animation but now wit a fade in

                imgVMeals.startAnimation(
                    AnimationUtils.loadAnimation(
                        applicationContext,
                        androidx.appcompat.R.anim.abc_fade_in
                    )
                )

                imgVMeals.postDelayed(this, duration)
            }
        }, duration)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("imageIndex", imageIndex)
    }
    // To set the appropriate picture appropriately, we fetch the image index from the save instance.

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        imageIndex = savedInstanceState.getInt("imageIndex")

    }



}


