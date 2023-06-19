package com.example.mealprep
import android.widget.EditText
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MealDao {
    @Query("Select * from  meal" )
    suspend fun getAll(): List<Meal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(vararg meal: Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg meals: Meal)

    @Query("SELECT * FROM meal WHERE Ingredients LIKE '%' || :searchTerm || '%' OR meal LIKE '%' || :searchTerm || '%'")
    suspend fun searchForTerm(searchTerm: String): List<Meal>
    @Query("SELECT MealThumb FROM meal WHERE Ingredients LIKE '%' || :searchTerm || '%' OR meal LIKE '%' || :searchTerm || '%'")
    suspend fun searchImageUrl(searchTerm: String): List<String>


}