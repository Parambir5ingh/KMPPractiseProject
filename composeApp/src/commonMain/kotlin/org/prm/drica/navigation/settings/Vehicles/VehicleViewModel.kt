package org.prm.drica.navigation.settings.Vehicles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import org.prm.drica.db.DriCaDatabase
import org.prm.drica.models.VehiclesModel

class VehicleViewModel(
    private val database: DriCaDatabase
) : ViewModel() {
    val vehicleDao = database.getVehiclesDao()

    var name by mutableStateOf("")
    var type by mutableStateOf("NA")
    var year by mutableStateOf("")
    var kms by mutableStateOf("")
    var boughtOn by mutableStateOf("")

    var error by mutableStateOf<String?>(null)
    var isSaving by mutableStateOf(false)

    fun saveVehicle(onSaved: () -> Unit) {

        if (name.isBlank()) {
            error = "Vehicle name required"
            return
        }

        val yearInt = year.toIntOrNull()
        if (yearInt == null || yearInt !in 1900..LocalDate.now().year) {
            error = "Enter valid year between 1900 and ${LocalDate.now().year}"
            return
        }

        if (kms.toLongOrNull() == null) {
            error = "Invalid kms"
            return
        }

        if (boughtOn.isBlank()) {
            error = "Purchase date required"
            return
        }

        viewModelScope.launch {

            isSaving = true

            vehicleDao.upsert(
                VehiclesModel(
                    type = type,
                    year = year,
                    name = name,
                    kms = kms,
                    boughtOn = boughtOn
                )
            )

            isSaving = false
            clear()
            onSaved()
        }
    }

    private fun clear() {
        name = ""
        type = "NA"
        year = ""
        kms = ""
        boughtOn = ""
        error = null
    }
}