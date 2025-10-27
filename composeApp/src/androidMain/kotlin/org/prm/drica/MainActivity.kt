package org.prm.drica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.prm.drica.db.getRoomDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val db = getRoomDatabase(getDatabaseBuilder(this)).getTransactionDao()

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