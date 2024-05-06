package com.example.tadm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class SearchActivity : AppCompatActivity() {
    private lateinit var searchCategory:MaterialAutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchCategory= findViewById(R.id.scTxt)
        val categ = arrayOf("Name","Father Name","Phone No")
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, categ)
        searchCategory.setAdapter(adapter)
        searchCategory.setOnClickListener{
            searchCategory.showDropDown()
        }
        searchCategory.setOnFocusChangeListener { _, hasFocus -> if(hasFocus){ searchCategory.showDropDown()} }
        searchCategory.inputType = InputType.TYPE_NULL

// Set the input type to none
    }

}