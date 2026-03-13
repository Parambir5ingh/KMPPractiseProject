package org.prm.drica

import android.widget.Toast

actual fun showMessage(message: String) {
    val context = AppContextProvider.applicationContext // your app context provider
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}