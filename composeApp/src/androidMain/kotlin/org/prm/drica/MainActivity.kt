package org.prm.drica

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.prm.drica.db.getRoomDatabase
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AppContextProvider.activityContext = this@MainActivity

        val db = getRoomDatabase(getDatabaseBuilder(this))

        setContent {
            App(db)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App()
}