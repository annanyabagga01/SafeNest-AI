package com.example.data.remote

import com.example.data.model.Property
import com.example.data.model.RoommateProfile
import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Data transfer object for the AI scam analysis request.
 */
data class ScamAnalysisApiRequest(
    @Json(name = "description") val description: String,
    @Json(name = "rent") val rent: Int,
    @Json(name = "deposit") val deposit: Int,
    @Json(name = "brokerMessage") val brokerMessage: String? = null,
    @Json(name = "suspiciousClaims") val suspiciousClaims: String? = null
)

/**
 * Data transfer object for the AI scam analysis response.
 */
data class ScamAnalysisApiResponse(
    @Json(name = "riskLevel") val riskLevel: String,
    @Json(name = "riskScore") val riskScore: Int,
    @Json(name = "redFlags") val redFlags: List<String>,
    @Json(name = "positiveSignals") val positiveSignals: List<String>,
    @Json(name = "recommendations") val recommendations: List<String>,
    @Json(name = "summary") val summary: String
)

/**
 * Data transfer object for roommate compatibility matching request.
 */
data class RoommateMatchApiRequest(
    @Json(name = "userProfile") val userProfile: RoommateProfile,
    @Json(name = "candidateProfile") val candidateProfile: RoommateProfile
)

/**
 * Data transfer object for roommate compatibility matching response.
 */
data class RoommateMatchApiResponse(
    @Json(name = "compatibilityScore") val compatibilityScore: Int,
    @Json(name = "positiveReasons") val positiveReasons: List<String>,
    @Json(name = "potentialDifferences") val potentialDifferences: List<String>,
    @Json(name = "aiHarmonyAdvice") val aiHarmonyAdvice: String
)

/**
 * Type-safe Retrofit interface defining backend API endpoints.
 */
interface BackendApiService {

    /**
     * AI Scam Detection endpoint for analyzing property listings.
     * Path: /api/ai/scam-analysis
     */
    @POST("api/ai/scam-analysis")
    suspend fun analyzeScam(
        @Body request: ScamAnalysisApiRequest
    ): Response<ScamAnalysisApiResponse>

    /**
     * AI Roommate Matching endpoint for calculating compatibility and advice.
     * Path: /api/roommate/match
     */
    @POST("api/roommate/match")
    suspend fun matchRoommate(
        @Body request: RoommateMatchApiRequest
    ): Response<RoommateMatchApiResponse>

    /**
     * Get list of verified rental properties.
     * Path: /api/properties
     */
    @GET("api/properties")
    suspend fun getProperties(
        @Query("city") city: String? = null,
        @Query("minRent") minRent: Int? = null,
        @Query("maxRent") maxRent: Int? = null
    ): Response<List<Property>>

    /**
     * Get details for a specific property.
     * Path: /api/properties/{id}
     */
    @GET("api/properties/{id}")
    suspend fun getPropertyById(
        @Path("id") id: String
    ): Response<Property>

    /**
     * Get list of potential roommates in a given city.
     * Path: /api/roommates
     */
    @GET("api/roommates")
    suspend fun getRoommates(
        @Query("city") city: String? = null
    ): Response<List<RoommateProfile>>
}
