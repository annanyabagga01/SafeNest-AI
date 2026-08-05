package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookingStatus {
    PENDING, APPROVED, REJECTED
}

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val propertyLocality: String,
    val propertyImageUrl: String,
    val monthlyRent: Int,
    val depositAmount: Int,
    val moveInDate: String,
    val durationMonths: Int,
    val tenantName: String,
    val tenantPhone: String,
    val message: String,
    val status: BookingStatus,
    val isEscrowProtected: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
