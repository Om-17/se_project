package com.example.tadm.util


import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar

class SnackbarUtils(private val context: Context) {



    fun showSnackbar(rootView: View, message: String) {

        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view

        fun Int.toPx(): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (this * scale).toInt()
        }

        val params = snackbarView.layoutParams as FrameLayout.LayoutParams
        params.bottomMargin = 20.toPx() // Convert dp to pixels
        snackbarView.layoutParams = params


        snackbar.show()
    }
}
