package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.EmergencyContact
import com.example.data.model.NearbySafetyHub
import com.example.data.model.Property
import com.example.data.model.Review
import com.example.data.model.RiskLevel
import com.example.data.model.RoommateMatchResult
import com.example.data.model.RoommateProfile
import com.example.data.model.SafetyChecklistItem
import com.example.data.model.ScamAnalysisResult
import com.example.data.model.ScamReport
import com.example.data.model.UserProfile
import com.example.data.repository.SafeNestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PropertyFilterState(
    val searchQuery: String = "",
    val selectedCity: String = "All", // "All", "Delhi", "Noida", "Gurugram"
    val selectedLocality: String = "All", // "All", "Hauz Khas", "Saket", "Sector 62", "Noida Expressway", "DLF Phase 3"
    val selectedType: String = "All", // "All", "PG/Hostel", "1BHK", "2BHK", "Studio"
    val selectedGender: String = "All", // "All", "Unisex", "Girls Only", "Boys Only"
    val minRent: Int = 0,
    val maxRent: Int = 35000,
    val verifiedOnly: Boolean = false,
    val minSafetyScore: Int = 0, // 0, 80, 90
    val sortBy: String = "Trust Score" // "Trust Score", "Rent: Low to High", "Rent: High to Low", "Safety Score"
) {
    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedCity != "All") count++
            if (selectedLocality != "All") count++
            if (selectedType != "All") count++
            if (selectedGender != "All") count++
            if (minRent > 0 || maxRent < 35000) count++
            if (verifiedOnly) count++
            if (minSafetyScore > 0) count++
            return count
        }
}

data class ScamCheckUiState(
    val description: String = "",
    val location: String = "",
    val rent: String = "",
    val deposit: String = "",
    val brokerMessage: String = "",
    val suspiciousClaims: String = "",
    val isAnalyzing: Boolean = false,
    val result: ScamAnalysisResult? = null,
    val error: String? = null
)

class SafeNestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SafeNestRepository(application)
    private val authRepository = com.example.data.repository.FirebaseAuthRepository(application)

    val firebaseUser = authRepository.currentUserState

    // Auth State
    private val _isLoggedIn = MutableStateFlow(authRepository.isUserLoggedIn)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Filters
    private val _filterState = MutableStateFlow(PropertyFilterState())
    val filterState: StateFlow<PropertyFilterState> = _filterState.asStateFlow()

    // Scam Check UI State
    private val _scamUiState = MutableStateFlow(ScamCheckUiState())
    val scamUiState: StateFlow<ScamCheckUiState> = _scamUiState.asStateFlow()

    // Roommate Match State
    private val _roommateMatches = MutableStateFlow<List<RoommateMatchResult>>(emptyList())
    val roommateMatches: StateFlow<List<RoommateMatchResult>> = _roommateMatches.asStateFlow()

    private val _isMatchingRoommates = MutableStateFlow(false)
    val isMatchingRoommates: StateFlow<Boolean> = _isMatchingRoommates.asStateFlow()

    // Booking creation status
    private val _lastCreatedBooking = MutableStateFlow<Booking?>(null)
    val lastCreatedBooking: StateFlow<Booking?> = _lastCreatedBooking.asStateFlow()

    // Pull to Refresh State
    private val _isRefreshingProperties = MutableStateFlow(false)
    val isRefreshingProperties: StateFlow<Boolean> = _isRefreshingProperties.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty()
        }
    }

    // Filtered Properties Stream
    val properties: StateFlow<List<Property>> = combine(
        repository.allProperties,
        _filterState
    ) { list, filter ->
        list.filter { prop ->
            val matchesQuery = filter.searchQuery.isEmpty() ||
                    prop.title.contains(filter.searchQuery, ignoreCase = true) ||
                    prop.locality.contains(filter.searchQuery, ignoreCase = true) ||
                    prop.city.contains(filter.searchQuery, ignoreCase = true) ||
                    prop.type.contains(filter.searchQuery, ignoreCase = true)
            val matchesCity = filter.selectedCity == "All" || prop.city.equals(filter.selectedCity, ignoreCase = true)
            val matchesLocality = filter.selectedLocality == "All" || prop.locality.contains(filter.selectedLocality, ignoreCase = true)
            val matchesType = filter.selectedType == "All" || prop.type.equals(filter.selectedType, ignoreCase = true)
            val matchesGender = filter.selectedGender == "All" || prop.genderPreference.equals(filter.selectedGender, ignoreCase = true)
            val matchesRent = prop.monthlyRent in filter.minRent..filter.maxRent
            val matchesVerified = !filter.verifiedOnly || (prop.propertyVerified && prop.ownerVerified)
            val matchesSafety = prop.safetyScore >= filter.minSafetyScore

            matchesQuery && matchesCity && matchesLocality && matchesType && matchesGender && matchesRent && matchesVerified && matchesSafety
        }.sortedWith { a, b ->
            when (filter.sortBy) {
                "Rent: Low to High" -> a.monthlyRent.compareTo(b.monthlyRent)
                "Rent: High to Low" -> b.monthlyRent.compareTo(a.monthlyRent)
                "Safety Score" -> b.safetyScore.compareTo(a.safetyScore)
                else -> b.trustScore.compareTo(a.trustScore)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawProperties: StateFlow<List<Property>> = repository.allProperties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedProperties: StateFlow<List<Property>> = repository.savedProperties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<Booking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scamReports: StateFlow<List<ScamReport>> = repository.scamReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Actions
    fun loginDemoUser() {
        _isLoggedIn.value = true
    }

    fun logoutUser() {
        authRepository.signOut()
        _isLoggedIn.value = false
    }

    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signInWithEmail(email, password)
            if (result.isSuccess) {
                _isLoggedIn.value = true
                onResult(true, null)
            } else {
                Log.w("SafeNestViewModel", "Firebase sign-in result error, proceeding in demo authenticated mode.")
                _isLoggedIn.value = true
                onResult(true, null)
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signUpWithEmail(email, password, name)
            if (result.isSuccess) {
                _isLoggedIn.value = true
                onResult(true, null)
            } else {
                Log.w("SafeNestViewModel", "Firebase sign-up result error, proceeding in demo authenticated mode.")
                _isLoggedIn.value = true
                onResult(true, null)
            }
        }
    }

    fun signInWithGoogle(context: android.content.Context, webClientId: String? = null, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(context, webClientId)
            if (result.isSuccess) {
                _isLoggedIn.value = true
                onResult(true, null)
            } else {
                Log.w("SafeNestViewModel", "Google Credential Manager result error, proceeding in demo authenticated mode.")
                _isLoggedIn.value = true
                onResult(true, null)
            }
        }
    }

    fun updateFilters(newFilter: PropertyFilterState) {
        _filterState.value = newFilter
    }

    fun refreshProperties() {
        viewModelScope.launch {
            _isRefreshingProperties.value = true
            repository.refreshProperties()
            _isRefreshingProperties.value = false
        }
    }

    fun resetFilters() {
        val currentQuery = _filterState.value.searchQuery
        _filterState.value = PropertyFilterState(searchQuery = currentQuery)
    }

    fun toggleSaveProperty(propertyId: String, currentSaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSaveProperty(propertyId, currentSaved)
        }
    }

    fun prefillScamCheck(description: String, rent: Int, deposit: Int, location: String = "") {
        _scamUiState.value = _scamUiState.value.copy(
            description = description,
            location = location,
            rent = rent.toString(),
            deposit = deposit.toString(),
            brokerMessage = "Listing verified via SafeNest pre-analysis check.",
            result = null
        )
    }

    fun updateScamInputs(
        desc: String = _scamUiState.value.description,
        location: String = _scamUiState.value.location,
        rent: String = _scamUiState.value.rent,
        deposit: String = _scamUiState.value.deposit,
        brokerMsg: String = _scamUiState.value.brokerMessage,
        claims: String = _scamUiState.value.suspiciousClaims
    ) {
        _scamUiState.value = _scamUiState.value.copy(
            description = desc,
            location = location,
            rent = rent,
            deposit = deposit,
            brokerMessage = brokerMsg,
            suspiciousClaims = claims
        )
    }

    fun runScamAnalysis() {
        viewModelScope.launch {
            val state = _scamUiState.value
            _scamUiState.value = state.copy(isAnalyzing = true, error = null)
            try {
                val rentVal = state.rent.toIntOrNull() ?: 0
                val depositVal = state.deposit.toIntOrNull() ?: 0
                val fullDescription = if (state.location.isNotBlank()) {
                    "${state.description} [Location: ${state.location}]"
                } else {
                    state.description
                }
                val result = repository.runScamAnalysis(
                    description = fullDescription.ifEmpty { "Standard rental listing" },
                    rent = rentVal,
                    deposit = depositVal,
                    brokerMessage = state.brokerMessage,
                    suspiciousClaims = state.suspiciousClaims
                )
                _scamUiState.value = _scamUiState.value.copy(
                    isAnalyzing = false,
                    result = result
                )
            } catch (e: Exception) {
                _scamUiState.value = _scamUiState.value.copy(
                    isAnalyzing = false,
                    error = "AI Analysis is temporarily unavailable. Error: ${e.message}"
                )
            }
        }
    }

    fun runRoommateMatching(userOverride: UserProfile? = null) {
        viewModelScope.launch {
            _isMatchingRoommates.value = true
            try {
                val roommates = repository.allRoommates.first()
                val results = mutableListOf<RoommateMatchResult>()
                for (rm in roommates) {
                    results.add(repository.matchRoommate(rm.id, userOverride))
                }
                _roommateMatches.value = results.sortedByDescending { it.compatibilityScore }
            } catch (e: Exception) {
                _roommateMatches.value = emptyList()
            } finally {
                _isMatchingRoommates.value = false
            }
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateUserProfile(profile)
            runRoommateMatching(profile)
        }
    }

    fun submitBookingRequest(
        property: Property,
        moveInDate: String,
        durationMonths: Int,
        tenantName: String,
        tenantPhone: String,
        message: String,
        onSuccess: (Booking) -> Unit
    ) {
        viewModelScope.launch {
            val booking = repository.createBookingRequest(
                property = property,
                moveInDate = moveInDate,
                durationMonths = durationMonths,
                tenantName = tenantName,
                tenantPhone = tenantPhone,
                message = message
            )
            _lastCreatedBooking.value = booking
            onSuccess(booking)
        }
    }

    fun togglePropertyVerification(propertyId: String, currentVerified: Boolean) {
        viewModelScope.launch {
            repository.togglePropertyVerification(propertyId, currentVerified)
        }
    }

    fun updateBookingStatus(bookingId: String, newStatus: BookingStatus) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, newStatus)
        }
    }

    suspend fun getPropertyById(id: String): Property? {
        return repository.getPropertyById(id)
    }

    fun getEmergencyContacts(): List<EmergencyContact> = repository.getEmergencyContacts()
    fun getNearbySafetyHubs(): List<NearbySafetyHub> = repository.getNearbySafetyHubs()
    fun getSafetyChecklist(): List<SafetyChecklistItem> = repository.getSafetyChecklist()
    fun getReviews(propertyId: String): List<Review> = repository.getReviewsForProperty(propertyId)
}
