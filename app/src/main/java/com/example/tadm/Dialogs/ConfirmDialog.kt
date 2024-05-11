package com.example.tadm.Dialogs
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.tadm.R

class ConfirmDialog(
    context: Context,
    private val onYesClick: () -> Unit,
    private val onNoClick: () -> Unit,
    private val title: String,
    private val message: String
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_dialog)

        val titleTextView: TextView = findViewById(R.id.dialogTitle)
        val messageTextView: TextView = findViewById(R.id.dialogMessage)
        val btnYes: Button = findViewById(R.id.btnYes)
        val btnNo: Button = findViewById(R.id.btnNo)
        setCancelable(false)

        titleTextView.text = title
        messageTextView.text = message

        btnYes.setOnClickListener {
            onYesClick.invoke()
            dismiss()
        }

        btnNo.setOnClickListener {
            onNoClick.invoke()
            dismiss()
        }
    }
    private fun customdismissDialog(){
        dismiss()

    }
}
