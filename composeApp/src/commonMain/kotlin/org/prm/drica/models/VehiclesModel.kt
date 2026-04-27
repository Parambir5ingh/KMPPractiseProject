package org.prm.drica.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/*
* Created by parambirsingh ON 25/10/25
*/
@Serializable
@Entity(tableName = "VehicleData")
data class VehiclesModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String = "NA",// NA,Hatchback, Sedan, SUV, Truck, VAN, Semi-Truck, Bus
    val year: String = "1900",
    val name: String = "NA",
    val kms: String = "0", // Non decimal value
    val boughtOn: String = "0", //  Date of purchase
)