package com.example.mealprep


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Meal(
    val id: Int?,
    @PrimaryKey val meal: String,
    val category: String?,
    val area: String?,
    val DrinkAlternate: String?,
    val Instructions: String?,
    val Tags: String?,
    val youtube: String?,
    val Ingredients: String?,
    val Measure: String?,
    val Source : String,
    val MealThumb: String?,
    val CreativeCommonsConfirmed: String,
    val dateModified: String



    ) {

}


