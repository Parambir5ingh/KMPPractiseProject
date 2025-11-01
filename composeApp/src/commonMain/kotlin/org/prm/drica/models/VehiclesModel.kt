package org.prm.drica.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
* Created by parambirsingh ON 25/10/25
*/
@Entity(tableName = "VehicleData")
data class VehiclesModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,// Hatchback, Sedan, SUV, Truck, VAN, Semi-Truck, Bus
    val year: String,
    val name: String,
    val kms: String,
    val boughtOn: String,
)