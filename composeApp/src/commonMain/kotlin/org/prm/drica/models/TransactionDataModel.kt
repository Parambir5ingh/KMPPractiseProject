package org.prm.drica.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
* Created by parambirsingh ON 25/10/25
*/
@Entity(tableName = "TransactionData")
data class TransactionDataModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var type: String = "",
    var category: String = "",
    var amount: Double = 0.00,
    var tripKms: Double = 0.00,
    var totalKms: Double = 0.00,
    var notes: String = "",
    var vehicleId: Long = 0,
    var isVoid: Boolean = false,
    var voidNotes: String = "",
    var dateTime: Long = 0
)