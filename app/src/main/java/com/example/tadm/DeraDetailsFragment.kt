package com.example.tadm

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tadm.Interface.BeforeNextClickListener
import com.example.tadm.Interface.FragmentDataListener
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class DeraDetailsFragment : Fragment(),BeforeNextClickListener {
    private lateinit var listener: FragmentDataListener
    private lateinit var sheepTxt: TextInputEditText
    private lateinit var goatTxt: TextInputEditText
    private lateinit var dogTxt: TextInputEditText
    private lateinit var horseTxt: TextInputEditText
    private lateinit var bullTxt: TextInputEditText
    private lateinit var cowTxt: TextInputEditText
    private lateinit var oxTxt: TextInputEditText

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
    private var beforeNextClickListener: BeforeNextClickListener? = null

    fun setBeforeNextClickListener(listener: BeforeNextClickListener) {
        beforeNextClickListener = listener
    }
    override fun onBeforeNextClicked(): Boolean {
        val sheepValue = sheepTxt.text.toString()
        val goatValue = goatTxt.text.toString()
        val dogValue = dogTxt.text.toString()
        val horseValue = horseTxt.text.toString()
        val bullValue = bullTxt.text.toString()
        val cowValue = cowTxt.text.toString()
        val oxValue = oxTxt.text.toString()

        // Create a JSON object and add data
        val jsonObject = JSONObject()
        jsonObject.put("sheep", sheepValue)
        jsonObject.put("goat", goatValue)
        jsonObject.put("dog", dogValue)
        jsonObject.put("horse", horseValue)
        jsonObject.put("bull", bullValue)
        jsonObject.put("cow", cowValue)
        jsonObject.put("ox", oxValue)
        listener.onDeraDataReceived(jsonObject.toString())
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_dera_details, container, false)
        sheepTxt = view.findViewById(R.id.sheepTxt)
        goatTxt = view.findViewById(R.id.goatTxt)
        dogTxt = view.findViewById(R.id.dogTxt)
        horseTxt = view.findViewById(R.id.horseTxt)
        bullTxt = view.findViewById(R.id.bullTxt)
        cowTxt = view.findViewById(R.id.cowTxt)
        oxTxt = view.findViewById(R.id.oxTxt)

        // Inflate the layout for this fragment
        return view
    }

}