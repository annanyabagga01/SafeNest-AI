package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roommate_profiles")
data class RoommateProfile(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val occupation: String,
    val bio: String,
    val city: String,
    val maxBudget: Int,
    val sleepSchedule: String, // "Early Bird", "Night Owl", "Flexible"
    val cleanliness: String, // "Strict", "Moderate", "Casual"
    val foodPreference: String, // "Vegetarian", "Non-Veg", "Eggetarian", "Any"
    val smokingPreference: String, // "Non-Smoker", "Outside Only", "Social"
    val studyWorkSchedule: String, // "9 AM - 6 PM", "Night Shift", "Remote Work", "Student Hours"
    val noisePreference: String, // "Quiet", "Moderate", "Music/Social"
    val pets: String, // "No Pets", "Pet Friendly", "Has Dog", "Has Cat"
    val gender: String, // "Female", "Male", "Non-binary"
    val avatarUrl: String = "",
    val verifiedBadge: Boolean = true,
    val lifestyleTags: String = "Early Bird,Strict Cleanliness,Vegetarian,Non-Smoker,Quiet Study Zone"
)

data class RoommateMatchResult(
    val roommate: RoommateProfile,
    val compatibilityScore: Int, // 0-100
    val positiveReasons: List<String>,
    val potentialDifferences: List<String>,
    val aiHarmonyAdvice: String
)
