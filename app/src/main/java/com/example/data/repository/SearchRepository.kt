package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.Property
import com.example.data.remote.BackendApiService
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for searching and fetching rental listings from mock/remote API.
 */
class SearchRepository(
    private val context: Context,
    private val apiService: BackendApiService = RetrofitClient.apiService
) {

    private val fallbackRepository = SafeNestRepository(context)

    /**
     * Fetches rental listings matching the search criteria.
     * Tries the remote API first, falling back to local mock data on network error.
     */
    suspend fun searchListings(
        query: String = "",
        city: String? = null,
        minRent: Int? = null,
        maxRent: Int? = null,
        propertyType: String? = null
    ): Result<List<Property>> = withContext(Dispatchers.IO) {
        try {
            // Attempt remote API fetch
            val effectiveCity = if (city.isNullOrBlank() || city.equals("All", ignoreCase = true)) null else city
            val response = apiService.getProperties(
                city = effectiveCity,
                minRent = minRent,
                maxRent = maxRent
            )

            if (response.isSuccessful && response.body() != null) {
                val apiProperties = response.body()!!
                val filtered = filterPropertiesLocally(apiProperties, query, propertyType)
                Result.success(filtered)
            } else {
                Log.w("SearchRepository", "API call unsuccessful (${response.code()}), falling back to mock listings")
                fetchMockListings(query, effectiveCity, minRent, maxRent, propertyType)
            }
        } catch (e: Exception) {
            Log.d("SearchRepository", "Network exception (${e.localizedMessage}), utilizing mock API dataset")
            fetchMockListings(query, city, minRent, maxRent, propertyType)
        }
    }

    private fun fetchMockListings(
        query: String,
        city: String?,
        minRent: Int?,
        maxRent: Int?,
        propertyType: String?
    ): Result<List<Property>> {
        val mockData = getMockRentalListings()
        val effectiveCity = if (city.isNullOrBlank() || city.equals("All", ignoreCase = true)) null else city
        val effectiveType = if (propertyType.isNullOrBlank() || propertyType.equals("All", ignoreCase = true)) null else propertyType

        val filtered = mockData.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.locality.contains(query, ignoreCase = true) ||
                    item.city.contains(query, ignoreCase = true) ||
                    item.type.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)

            val matchesCity = effectiveCity == null || item.city.equals(effectiveCity, ignoreCase = true)
            val matchesType = effectiveType == null || item.type.equals(effectiveType, ignoreCase = true)
            val matchesMinRent = minRent == null || item.monthlyRent >= minRent
            val matchesMaxRent = maxRent == null || item.monthlyRent <= maxRent

            matchesQuery && matchesCity && matchesType && matchesMinRent && matchesMaxRent
        }

        return Result.success(filtered)
    }

    private fun filterPropertiesLocally(
        list: List<Property>,
        query: String,
        propertyType: String?
    ): List<Property> {
        val effectiveType = if (propertyType.isNullOrBlank() || propertyType.equals("All", ignoreCase = true)) null else propertyType
        return list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.locality.contains(query, ignoreCase = true) ||
                    item.city.contains(query, ignoreCase = true)
            val matchesType = effectiveType == null || item.type.equals(effectiveType, ignoreCase = true)
            matchesQuery && matchesType
        }
    }

    /**
     * Mock rental dataset for fallback and offline search support.
     */
    private fun getMockRentalListings(): List<Property> = listOf(
        Property(
            id = "mock_1",
            title = "CampusNest Student Co-Living",
            city = "Delhi",
            locality = "Kamla Nagar (North Campus)",
            type = "PG/Hostel",
            monthlyRent = 9500,
            deposit = 9500,
            bedrooms = 1,
            genderPreference = "Girls Only",
            amenities = listOf("High-Speed Wi-Fi", "CCTV 24/7", "Biometric Gate", "3 Meals Included"),
            imageUrl = "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=800&q=80",
            latitude = 28.6834,
            longitude = 77.2074,
            ownerName = "Rajesh Gupta (Verified Owner)",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 96,
            safetyScore = 95,
            description = "Verified student housing for girls near North Campus. Features biometric entry, nutritious meals, and study desks.",
            distanceToMetroKm = 0.4
        ),
        Property(
            id = "mock_2",
            title = "MetroView Smart PG & Suites",
            city = "Noida",
            locality = "Sector 62 (Near Fortis)",
            type = "PG/Hostel",
            monthlyRent = 11000,
            deposit = 11000,
            bedrooms = 1,
            genderPreference = "Unisex",
            amenities = listOf("Wi-Fi", "CCTV", "Gym Access", "Daily Housekeeping"),
            imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
            latitude = 28.6270,
            longitude = 77.3725,
            ownerName = "Sunita Malhotra",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 94,
            safetyScore = 92,
            description = "Modern tech professional stay in Noida Sector 62. 5 mins from metro.",
            distanceToMetroKm = 0.5
        ),
        Property(
            id = "mock_3",
            title = "GreenLeaf Executive 1BHK",
            city = "Gurugram",
            locality = "Cyber City (DLF Phase 3)",
            type = "1BHK",
            monthlyRent = 22000,
            deposit = 44000,
            bedrooms = 1,
            genderPreference = "Unisex",
            amenities = listOf("Modular Kitchen", "Balcony View", "Parking", "Elevator"),
            imageUrl = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80",
            latitude = 28.4950,
            longitude = 77.0890,
            ownerName = "Col. V.K. Singh (Retd.)",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 98,
            safetyScore = 97,
            description = "Spacious 1BHK apartment in gated DLF Phase 3 society.",
            distanceToMetroKm = 0.8
        ),
        Property(
            id = "mock_4",
            title = "Hauz Khas Heritage Studio",
            city = "Delhi",
            locality = "Hauz Khas Village",
            type = "Studio",
            monthlyRent = 18500,
            deposit = 18500,
            bedrooms = 1,
            genderPreference = "Unisex",
            amenities = listOf("Fully Furnished", "High-Speed Wi-Fi", "Terrace Garden"),
            imageUrl = "https://images.unsplash.com/photo-1554995207-c18c203602cb?auto=format&fit=crop&w=800&q=80",
            latitude = 28.5494,
            longitude = 77.2001,
            ownerName = "Arjun Sethi",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 91,
            safetyScore = 90,
            description = "Charming studio apartment near Hauz Khas Metro.",
            distanceToMetroKm = 0.6
        ),
        Property(
            id = "mock_5",
            title = "Expressway Tech Hub 2BHK",
            city = "Noida",
            locality = "Sector 137 (Expressway)",
            type = "2BHK",
            monthlyRent = 16000,
            deposit = 32000,
            bedrooms = 2,
            genderPreference = "Unisex",
            amenities = listOf("Club House", "Swimming Pool", "24/7 Guard"),
            imageUrl = "https://images.unsplash.com/photo-1493809842364-78817add7ffb?auto=format&fit=crop&w=800&q=80",
            latitude = 28.5042,
            longitude = 77.4018,
            ownerName = "Meenakshi Joshi",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 93,
            safetyScore = 94,
            description = "Spacious 2BHK in a high-rise residential tower on Noida Expressway.",
            distanceToMetroKm = 0.3
        )
    )
}
