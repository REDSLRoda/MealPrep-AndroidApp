package com.example.mealprep

import android.graphics.Bitmap
// We develop the following data class in order to display the items in the recycling view.
data class ItemsViewModel(val image: Bitmap, val text: StringBuilder):java.io.Serializable {
}