package org.prm.drica.navigation

sealed class NavRoute(val route: String) {
    object Home : NavRoute("home")
    object Settings : NavRoute("settings")
    object VehicleManagement : NavRoute("vehicleManagement")
}
