package com.example.mealprep

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment

class ExampleDialog : AppCompatDialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.layout_opendialog, null)
        builder.setView(view)
            .setTitle("Alert")
            .setMessage("Meals Added to Database")
            .setNegativeButton(
                "Cancel"
            ) { dialogInterface, i -> }
            .setPositiveButton(
                "OK"
            ) { dialogInterface, i -> }


        return builder.create()
    }
}




