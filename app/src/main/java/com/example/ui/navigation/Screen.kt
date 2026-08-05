package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Properties : Screen("properties")
    object PropertyDetail : Screen("property_detail/{propertyId}") {
        fun createRoute(propertyId: String) = "property_detail/$propertyId"
    }
    object ScamCheck : Screen("scam_check?prefill={prefill}") {
        fun createRoute(prefill: String = "") = "scam_check?prefill=$prefill"
    }
    object RoommateMatch : Screen("roommate_match")
    object Booking : Screen("booking/{propertyId}") {
        fun createRoute(propertyId: String) = "booking/$propertyId"
    }
    object Safety : Screen("safety")
    object Profile : Screen("profile")
    object Admin : Screen("admin")
}
