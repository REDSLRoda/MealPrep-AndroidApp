package com.example.mealprep


import androidx.room.Database

import androidx.room.RoomDatabase

@Database(entities = [Meal::class], version=1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun mealDao(): MealDao


}
