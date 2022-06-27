package com.example.heartratemonitoringapp.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.content.ContextCompat
import com.google.gson.internal.bind.util.ISO8601Utils.format
import okhttp3.ResponseBody
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun ResponseBody.getMessage(): String {
    return try {
        val jsonObj = JSONObject(this.string())
        jsonObj.getString("message")
    } catch (e: Exception) {
        e.message.toString()
    }
}

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Spinner.selected(action: (position:Int) -> Unit) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            action(position)
        }
    }
}

fun findIndex(arr: Array<String>, item: Any): Int {
    return arr.indexOf(item)
}

fun toLittleEndian(hex: String): Int {
    var ret = 0
    var hexLittleEndian = ""
    if (hex.length % 2 != 0) return ret
    var i = hex.length - 2
    while (i >= 0) {
        hexLittleEndian += hex.substring(i, i + 2)
        i -= 2
    }
    ret = hexLittleEndian.toInt(16)
    return ret
}