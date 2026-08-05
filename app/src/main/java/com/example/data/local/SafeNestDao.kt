package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Booking
import com.example.data.model.Property
import com.example.data.model.RoommateProfile
import com.example.data.model.ScamReport
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface SafeNestDao {
    // --- Properties ---
    @Query("SELECT * FROM properties ORDER BY trustScore DESC")
    fun getAllProperties(): Flow<List<Property>>

    @Query("SELECT * FROM properties WHERE id = :id LIMIT 1")
    suspend fun getPropertyById(id: String): Property?

    @Query("SELECT * FROM properties WHERE isSaved = 1")
    fun getSavedProperties(): Flow<List<Property>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<Property>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: Property)

    @Query("UPDATE properties SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: String, isSaved: Boolean)

    @Query("UPDATE properties SET propertyVerified = :verified WHERE id = :id")
    suspend fun updatePropertyVerification(id: String, verified: Boolean)

    // --- Bookings ---
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: String, status: String)

    // --- Scam Reports ---
    @Query("SELECT * FROM scam_reports ORDER BY timestamp DESC")
    fun getAllScamReports(): Flow<List<ScamReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScamReport(report: ScamReport)

    // --- Roommates ---
    @Query("SELECT * FROM roommate_profiles")
    fun getAllRoommates(): Flow<List<RoommateProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoommates(roommates: List<RoommateProfile>)

    // --- User Profile ---
    @Query("SELECT * FROM user_profiles WHERE uid = :uid LIMIT 1")
    fun getUserProfile(uid: String = "user_demo_1"): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)
}
