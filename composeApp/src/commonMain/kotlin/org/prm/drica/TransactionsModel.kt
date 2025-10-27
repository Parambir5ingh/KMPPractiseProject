package org.prm.drica

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
* Created by parambirsingh ON 25/10/25
*/
@Entity(tableName = "TransactionData")
data class TransactionDataModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String
)