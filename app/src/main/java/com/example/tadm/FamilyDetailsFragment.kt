package com.example.tadm

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import com.example.tadm.Interface.BeforeNextClickListener
import com.example.tadm.Interface.FragmentDataListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import org.json.JSONArray
import org.json.JSONObject
class FamilyDetailsFragment : Fragment(),BeforeNextClickListener {
    private val inputLayoutList = mutableListOf<TextInputLayout>()
    private lateinit var listener: FragmentDataListener
    private lateinit var containerLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentDataListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement FragmentDataListener")
        }
    }
    private lateinit var listLayout: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var addBtn: Button

    private var beforeNextClickListener: BeforeNextClickListener? = null

    fun setBeforeNextClickListener(listener: BeforeNextClickListener) {
        beforeNextClickListener = listener
    }
    override fun onBeforeNextClicked(): Boolean {
        val jsonArray = collectInputValues()
        val jsonString = jsonArray.toString()
        listener.onFamilyDataReceived(jsonString)
        return true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_family_details, container, false)
        listLayout = view.findViewById(R.id.input_box_layout)
        scrollView = view.findViewById(R.id.list_item)
        addBtn = view.findViewById(R.id.addBtn)
        containerLayout = scrollView.findViewById(R.id.input_box_layout) // Initialize containerLayout

        addNewForm(true)

        addBtn.setOnClickListener {
            addNewForm(false)
        }


        return  view
    }
    private var formCounter = 0
    private fun addNewForm(isFirstRecord:Boolean) {
        // Inflate the input box layout for a new form
        val inputLayout = LayoutInflater.from(requireContext())
            .inflate(R.layout.input_box_layout, scrollView, false) as LinearLayout

        // Customize the new input layout here if needed

        // Create a new TextInputLayout and add the inflated LinearLayout as its child
        val textInputLayout = TextInputLayout(requireContext())
        textInputLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val mobno:TextInputEditText = inputLayout.findViewById(R.id.mobileTxt)
        val inputMobFilter = InputFilter.LengthFilter(10)
        mobno.filters = arrayOf(inputMobFilter)


        textInputLayout.addView(inputLayout)

        // Add the delete button to the TextInputLayout


        if (!isFirstRecord) {
            val deleteBtn = MaterialButton(requireContext())
            deleteBtn.text = "Delete"
            deleteBtn.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {

                    gravity = Gravity.CENTER
                    marginStart = resources.getDimensionPixelSize(R.dimen.margin_start)
                    marginEnd= resources.getDimensionPixelSize(R.dimen.margin_start)

                    gravity = Gravity.END

            }

            deleteBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
            deleteBtn.setOnClickListener {
                // Remove the corresponding input layout and view when the delete button is clicked
                inputLayoutList.remove(textInputLayout)
                containerLayout.removeView(textInputLayout)
            }

            textInputLayout.addView(deleteBtn)
        }

        // Add the new TextInputLayout to the container layout inside the ScrollView
        val containerLayout = scrollView.findViewById<LinearLayout>(R.id.input_box_layout)
        containerLayout.addView(textInputLayout)
        inputLayoutList.add(textInputLayout)
    }

//    private fun addNewForm() {
//        // Inflate the input box layout for a new form
//        val inputLayout = LayoutInflater.from(requireContext())
//            .inflate(R.layout.input_box_layout, scrollView, false) as LinearLayout
//
//        // Customize the new input layout here if needed
//
//        // Create a new TextInputLayout and add the inflated LinearLayout as its child
//        val textInputLayout = TextInputLayout(requireContext())
//        textInputLayout.layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
////        val genderDropdown: MaterialAutoCompleteTextView = inputLayout.findViewById(R.id.genderTxt)
////        val genders = arrayOf("Male", "Female", "Other")
////        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, genders)
////        genderDropdown.setAdapter(adapter)
////
////// Set the input type to none
////        genderDropdown.inputType = InputType.TYPE_NULL
//        val mobno:TextInputEditText = inputLayout.findViewById(R.id.mobileTxt)
//        val inputMobFilter = InputFilter.LengthFilter(10)
//        mobno.filters = arrayOf(inputMobFilter)
//        textInputLayout.addView(inputLayout)
//
//        // Add the new TextInputLayout to the container layout inside the ScrollView
//        val containerLayout = scrollView.findViewById<LinearLayout>(R.id.input_box_layout)
//        containerLayout.addView(textInputLayout)
//        inputLayoutList.add(textInputLayout)
//        println("add")
//        println(inputLayoutList)
//        println(textInputLayout)
//        // Add the TextInputLayout to the list for later reference if needed
//    }


    private fun collectInputValues(): JSONArray {
        val jsonArray = JSONArray()
        println(inputLayoutList)
        for (inputLayout in inputLayoutList) {
            val jsonObject = JSONObject()

            val nameTxt = inputLayout.findViewById<TextInputEditText>(R.id.nameTxt)
            val ageTxt = inputLayout.findViewById<TextInputEditText>(R.id.agetxt)
            val genderTxt = inputLayout.findViewById<TextInputEditText>(R.id.genderTxt)
            val relationTxt = inputLayout.findViewById<TextInputEditText>(R.id.RelationTxt)
            val aadharTxt = inputLayout.findViewById<TextInputEditText>(R.id.aadharTxt)
            val mobileTxt = inputLayout.findViewById<TextInputEditText>(R.id.mobileTxt)

            jsonObject.put("name", nameTxt?.text.toString())
            jsonObject.put("age", ageTxt?.text.toString())
            jsonObject.put("gender", genderTxt?.text.toString())
            jsonObject.put("relation", relationTxt?.text.toString())
            jsonObject.put("aadhar_no", aadharTxt?.text.toString())
            jsonObject.put("mobile_no", mobileTxt?.text.toString())

            jsonArray.put(jsonObject)
        }
        println(jsonArray)
        return jsonArray
    }


}