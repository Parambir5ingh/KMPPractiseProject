package org.prm.drica.utils

object Validator {
    fun validateDouble(amount: Double?, name: String): String? =
        if (amount == null || amount == 0.00) "$name cannot be 0.00" else null

}