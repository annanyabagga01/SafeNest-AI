package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class Property(
    @PrimaryKey val id: String,
    val title: String,
    val city: String,
    val locality: String,
    val type: String, // PG/Hostel, 1BHK, 2BHK, Studio
    val monthlyRent: Int,
    val deposit: Int,
    val bedrooms: Int,
    val genderPreference: String, // Unisex, Girls Only, Boys Only
    val amenities: List<String>,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val ownerName: String,
    val ownerVerified: Boolean,
    val propertyVerified: Boolean,
    val trustScore: Int, // 0-100
    val safetyScore: Int, // 0-100
    val description: String,
    val distanceToMetroKm: Double = 0.5,
    val isSaved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class Review(
    val id: String,
    val propertyId: String,
    val userName: String,
    val userRole: String, // e.g. "Student at Amity", "Tech Professional at DLF"
    val rating: Float,
    val date: String,
    val comment: String
)
