package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val uid: String = "user_demo_1",
    val name: String = "Ananya Sharma",
    val email: String = "ananya.sharma@example.com",
    val role: String = "Student / Professional",
    val cityPreference: String = "Noida",
    val maxBudget: Int = 15000,
    val phone: String = "+91 98765 43210",
    val verifiedIdentity: Boolean = true,
    val collegeOrCompany: String = "Amity University Noida",
    val sleepSchedule: String = "Early Bird",
    val cleanliness: String = "Strict",
    val foodPreference: String = "Vegetarian",
    val smokingPreference: String = "Non-Smoker",
    val workSchedule: String = "Student Hours (9 AM - 5 PM)",
    val noisePreference: String = "Quiet",
    val pets: String = "No Pets",
    val gender: String = "Female"
)
