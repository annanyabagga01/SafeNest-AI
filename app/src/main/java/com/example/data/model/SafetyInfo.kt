package com.example.data.model

data class EmergencyContact(
    val title: String,
    val number: String,
    val description: String,
    val iconType: String
)

data class NearbySafetyHub(
    val id: String,
    val name: String,
    val category: String, // "Police Station", "Hospital", "Metro Station", "Women Safety Hub"
    val locality: String,
    val city: String,
    val distanceKm: Double,
    val phone: String,
    val verified: Boolean = true
)

data class SafetyChecklistItem(
    val id: String,
    val title: String,
    val description: String,
    val isCrucial: Boolean = true
)
