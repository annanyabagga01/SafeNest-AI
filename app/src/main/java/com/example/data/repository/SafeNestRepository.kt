package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
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
import com.example.data.remote.GeminiScamService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SafeNestRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.safeNestDao()
    private val geminiService = GeminiScamService()

    val allProperties: Flow<List<Property>> = dao.getAllProperties()
    val savedProperties: Flow<List<Property>> = dao.getSavedProperties()
    val allBookings: Flow<List<Booking>> = dao.getAllBookings()
    val scamReports: Flow<List<ScamReport>> = dao.getAllScamReports()
    val allRoommates: Flow<List<RoommateProfile>> = dao.getAllRoommates()
    val userProfile: Flow<UserProfile?> = dao.getUserProfile("user_demo_1")

    suspend fun initializeSeedDataIfEmpty() = withContext(Dispatchers.IO) {
        val propertiesList = allProperties.first()
        if (propertiesList.isEmpty()) {
            dao.insertProperties(getSeedProperties())
        }
        val roommatesList = allRoommates.first()
        if (roommatesList.isEmpty()) {
            dao.insertRoommates(getSeedRoommates())
        }
        val currentProfile = userProfile.first()
        if (currentProfile == null) {
            dao.insertUserProfile(UserProfile())
        }
        val bookingsList = allBookings.first()
        if (bookingsList.isEmpty()) {
            dao.insertBooking(
                Booking(
                    id = "bk_seed_1",
                    propertyId = "prop_1",
                    propertyTitle = "CampusNest Student Co-Living",
                    propertyLocality = "Kamla Nagar (North Campus), Delhi",
                    propertyImageUrl = "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=800&q=80",
                    monthlyRent = 9500,
                    depositAmount = 9500,
                    moveInDate = "15 Aug 2026",
                    durationMonths = 11,
                    tenantName = "Ananya Sharma",
                    tenantPhone = "+91 98765 43210",
                    message = "Interested in physical walkthrough and room allocation.",
                    status = BookingStatus.PENDING,
                    isEscrowProtected = true,
                    timestamp = System.currentTimeMillis() - 86400000L
                )
            )
        }
    }

    suspend fun getPropertyById(id: String): Property? = withContext(Dispatchers.IO) {
        dao.getPropertyById(id)
    }

    suspend fun toggleSaveProperty(id: String, currentSaved: Boolean) = withContext(Dispatchers.IO) {
        dao.updateSavedStatus(id, !currentSaved)
    }

    suspend fun createBookingRequest(
        property: Property,
        moveInDate: String,
        durationMonths: Int,
        tenantName: String,
        tenantPhone: String,
        message: String
    ): Booking = withContext(Dispatchers.IO) {
        val booking = Booking(
            id = "bk_" + System.currentTimeMillis(),
            propertyId = property.id,
            propertyTitle = property.title,
            propertyLocality = property.locality + ", " + property.city,
            propertyImageUrl = property.imageUrl,
            monthlyRent = property.monthlyRent,
            depositAmount = property.deposit,
            moveInDate = moveInDate,
            durationMonths = durationMonths,
            tenantName = tenantName,
            tenantPhone = tenantPhone,
            message = message,
            status = BookingStatus.PENDING,
            isEscrowProtected = true
        )
        dao.insertBooking(booking)
        booking
    }

    suspend fun updateBookingStatus(id: String, status: BookingStatus) = withContext(Dispatchers.IO) {
        dao.updateBookingStatus(id, status.name)
    }

    suspend fun togglePropertyVerification(id: String, currentVerified: Boolean) = withContext(Dispatchers.IO) {
        dao.updatePropertyVerification(id, !currentVerified)
    }

    suspend fun runScamAnalysis(
        description: String,
        rent: Int,
        deposit: Int,
        brokerMessage: String,
        suspiciousClaims: String
    ): ScamAnalysisResult = withContext(Dispatchers.IO) {
        val result = geminiService.analyzeListing(
            description = description,
            rent = rent,
            deposit = deposit,
            brokerMessage = brokerMessage,
            suspiciousClaims = suspiciousClaims
        )
        val report = ScamReport(
            inputDescription = description,
            advertisedRent = rent,
            depositAmount = deposit,
            brokerMessage = brokerMessage,
            suspiciousClaims = suspiciousClaims,
            riskLevel = result.riskLevel.name,
            riskScore = result.riskScore,
            summary = result.summary,
            redFlagsJson = result.redFlags.joinToString(";")
        )
        dao.insertScamReport(report)
        result
    }

    suspend fun matchRoommate(
        targetRoommateId: String,
        userOverride: UserProfile? = null
    ): RoommateMatchResult = withContext(Dispatchers.IO) {
        val roommates = allRoommates.first()
        val target = roommates.find { it.id == targetRoommateId } ?: roommates.first()
        val user = userOverride ?: userProfile.first() ?: UserProfile()

        var score = 40
        val positives = mutableListOf<String>()
        val differences = mutableListOf<String>()

        if (user.sleepSchedule.equals(target.sleepSchedule, ignoreCase = true)) {
            score += 12
            positives.add("Matched Sleep Schedule: Both prefer ${user.sleepSchedule}")
        } else {
            differences.add("Different Sleep Schedule (${user.sleepSchedule} vs ${target.sleepSchedule})")
        }

        if (user.cleanliness.equals(target.cleanliness, ignoreCase = true)) {
            score += 12
            positives.add("Matched Cleanliness Standards: Both value ${user.cleanliness}")
        } else {
            differences.add("Cleanliness Expectation (${user.cleanliness} vs ${target.cleanliness})")
        }

        if (user.foodPreference.equals(target.foodPreference, ignoreCase = true) || target.foodPreference == "Any") {
            score += 10
            positives.add("Food & Kitchen Harmony (${target.foodPreference})")
        } else {
            differences.add("Food Preference (${user.foodPreference} vs ${target.foodPreference})")
        }

        if (user.smokingPreference.equals(target.smokingPreference, ignoreCase = true)) {
            score += 10
            positives.add("Aligned Habits: Both are ${user.smokingPreference}")
        } else {
            score -= 5
            differences.add("Smoking Habit (${user.smokingPreference} vs ${target.smokingPreference})")
        }

        // Tag matching
        val userTagsList = user.lifestyleTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val targetTagsList = target.lifestyleTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val sharedTags = userTagsList.intersect(targetTagsList.toSet()).toList()

        if (sharedTags.isNotEmpty()) {
            val tagBonus = (sharedTags.size * 6).coerceAtMost(30)
            score += tagBonus
            positives.add("Matching Lifestyle Tags: ${sharedTags.joinToString(", ")}")
        }

        val finalScore = score.coerceIn(45, 98)
        val advice = geminiService.generateRoommateAdvice(user, target, finalScore, positives, differences)

        RoommateMatchResult(
            roommate = target,
            compatibilityScore = finalScore,
            positiveReasons = positives.ifEmpty { listOf("Compatible budget and location preferences.") },
            potentialDifferences = differences.ifEmpty { listOf("No significant conflicts identified.") },
            aiHarmonyAdvice = advice
        )
    }

    suspend fun updateUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        dao.insertUserProfile(profile)
    }

    fun getEmergencyContacts(): List<EmergencyContact> = listOf(
        EmergencyContact("National Emergency Helpline", "112", "All emergency services (Police, Fire, Ambulance)", "police"),
        EmergencyContact("Women Helpline (NCR)", "1091", "24/7 National Women Safety Helpline", "shield"),
        EmergencyContact("Student Anti-Ragging & Safety", "1800-180-5522", "UGC National Student Safety Toll Free", "school"),
        EmergencyContact("Cyber Crime Reporting", "1930", "Report financial rent scams & cyber fraud immediately", "alert")
    )

    fun getNearbySafetyHubs(): List<NearbySafetyHub> = listOf(
        NearbySafetyHub("hub_1", "Sector 62 Police Station", "Police Station", "Sector 62", "Noida", 0.8, "+91 120 2400100", true),
        NearbySafetyHub("hub_2", "Fortis Hospital Noida", "Hospital", "Sector 62", "Noida", 1.2, "+91 120 4300222", true),
        NearbySafetyHub("hub_3", "Noida Electronic City Metro Station", "Metro Station", "Sector 62", "Noida", 0.5, "+91 11 23417910", true),
        NearbySafetyHub("hub_4", "DLF Cyber City Police Outpost", "Police Station", "DLF Phase 2", "Gurugram", 1.1, "+91 124 2351100", true),
        NearbySafetyHub("hub_5", "Max Super Speciality Hospital", "Hospital", "Saket / Hauz Khas", "Delhi", 1.5, "+91 11 26515050", true)
    )

    fun getSafetyChecklist(): List<SafetyChecklistItem> = listOf(
        SafetyChecklistItem("sc_1", "Never Pay Token Money Before In-Person Inspection", "Physical verification of doors, keys, and building locks is essential before transferring any funds.", true),
        SafetyChecklistItem("sc_2", "Verify Owner Identity Documents", "Cross-check landlord Aadhaar card or property deed papers in person or via SafeNest verified badge.", true),
        SafetyChecklistItem("sc_3", "Check Night Time Lighting & CCTV", "Inspect entry gates, street lighting, and active CCTV camera placement around the locality.", true),
        SafetyChecklistItem("sc_4", "Use SafeNest Escrow Booking Request", "Always submit booking requests through SafeNest AI to ensure transaction logging and legal tracking.", true)
    )

    fun getReviewsForProperty(propertyId: String): List<Review> = listOf(
        Review("r1", propertyId, "Rohan Verma", "Software Engineer at Paytm", 4.8f, "July 2026", "Super safe society with 24/7 security guard and biometrics. Metro is literally 5 mins walk."),
        Review("r2", propertyId, "Priya Sharma", "Student at Amity", 4.5f, "June 2026", "Clean rooms, high-speed Wi-Fi, and the owner is genuine. Verified badge gave me peace of mind.")
    )

    // Seed Data
    private fun getSeedProperties(): List<Property> = listOf(
        Property(
            id = "prop_1",
            title = "CampusNest Student Co-Living",
            city = "Delhi",
            locality = "Kamla Nagar (North Campus)",
            type = "PG/Hostel",
            monthlyRent = 9500,
            deposit = 9500,
            bedrooms = 1,
            genderPreference = "Girls Only",
            amenities = listOf("High-Speed Wi-Fi", "CCTV 24/7", "Biometric Gate", "3 Meals Included", "AC Room", "Power Backup"),
            imageUrl = "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=800&q=80",
            latitude = 28.6834,
            longitude = 77.2074,
            ownerName = "Rajesh Gupta (Verified Owner)",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 96,
            safetyScore = 95,
            description = "Premium verified student housing for girls near North Campus. Features biometric entry, 24/7 security guard, nutritious meals, study desks, and high-speed fiber internet.",
            distanceToMetroKm = 0.4,
            isSaved = true
        ),
        Property(
            id = "prop_2",
            title = "MetroView Smart PG & Suites",
            city = "Noida",
            locality = "Sector 62 (Near Fortis)",
            type = "PG/Hostel",
            monthlyRent = 11000,
            deposit = 11000,
            bedrooms = 1,
            genderPreference = "Unisex",
            amenities = listOf("Wi-Fi", "CCTV", "Gym Access", "Daily Housekeeping", "RO Water", "Power Backup"),
            imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
            latitude = 28.6270,
            longitude = 77.3725,
            ownerName = "Sunita Malhotra",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 94,
            safetyScore = 92,
            description = "Modern tech professional stay in Noida Sector 62. 5 minutes from Electronic City Metro Station. Fully furnished with ergonomic chairs, fridge, attached bath, and biometric access.",
            distanceToMetroKm = 0.5,
            isSaved = false
        ),
        Property(
            id = "prop_3",
            title = "GreenLeaf Executive 1BHK",
            city = "Gurugram",
            locality = "Cyber City (DLF Phase 3)",
            type = "1BHK",
            monthlyRent = 22000,
            deposit = 44000,
            bedrooms = 1,
            genderPreference = "Unisex",
            amenities = listOf("Modular Kitchen", "Balcony View", "Underground Parking", "Elevator", "Security Guard", "24/7 Power"),
            imageUrl = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80",
            latitude = 28.4950,
            longitude = 77.0890,
            ownerName = "Col. V.K. Singh (Retd.)",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 98,
            safetyScore = 97,
            description = "Spacious, elegant 1BHK apartment in gated DLF Phase 3 society. Ideal for IT corporate professionals working in Cyber City. Gated security with digital visitor authorization.",
            distanceToMetroKm = 0.8,
            isSaved = true
        ),
        Property(
            id = "prop_4",
            title = "Hauz Khas Heritage Residency",
            city = "Delhi",
            locality = "Hauz Khas Village",
            type = "Studio",
            monthlyRent = 18500,
            deposit = 18500,
            bedrooms = 1,
            genderPreference = "Unisex",
            amenities = listOf("Fully Furnished", "High-Speed Wi-Fi", "Terrace Garden", "Washing Machine", "Kitchenette"),
            imageUrl = "https://images.unsplash.com/photo-1554995207-c18c203602cb?auto=format&fit=crop&w=800&q=80",
            latitude = 28.5494,
            longitude = 77.2001,
            ownerName = "Arjun Sethi",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 91,
            safetyScore = 90,
            description = "Charming studio apartment near Hauz Khas Metro. Quiet residential pocket with leafy surroundings, vibrant cafes nearby, and strict night wardens.",
            distanceToMetroKm = 0.6,
            isSaved = false
        ),
        Property(
            id = "prop_5",
            title = "Sector 137 Tech Hub Co-Living",
            city = "Noida",
            locality = "Sector 137 (Expressway)",
            type = "2BHK",
            monthlyRent = 16000,
            deposit = 32000,
            bedrooms = 2,
            genderPreference = "Unisex",
            amenities = listOf("Club House", "Swimming Pool", "24/7 Guard", "Gated Entry", "Fast Elevator", "Power Backup"),
            imageUrl = "https://images.unsplash.com/photo-1493809842364-78817add7ffb?auto=format&fit=crop&w=800&q=80",
            latitude = 28.5042,
            longitude = 77.4018,
            ownerName = "Meenakshi Joshi",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 93,
            safetyScore = 94,
            description = "Spacious 2BHK in a high-rise residential tower on Noida Expressway. Double security gates, intercom system, and immediate access to Sector 137 Metro Station.",
            distanceToMetroKm = 0.3,
            isSaved = false
        ),
        Property(
            id = "prop_6",
            title = "Golf Course View Residency",
            city = "Gurugram",
            locality = "Golf Course Road (Sec 54)",
            type = "1BHK",
            monthlyRent = 26000,
            deposit = 52000,
            bedrooms = 1,
            genderPreference = "Boys Only",
            amenities = listOf("Covered Parking", "Gym", "Power Backup", "Modular Kitchen", "Smart Locks", "CCTV"),
            imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80",
            latitude = 28.4410,
            longitude = 77.1080,
            ownerName = "Karan Grover",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 95,
            safetyScore = 96,
            description = "Luxury 1BHK suite along Golf Course Road. Features smart keypad entry, soundproof double-glazed windows, and full 24-hour power backup.",
            distanceToMetroKm = 0.4,
            isSaved = false
        ),
        Property(
            id = "prop_7",
            title = "Laxmi Nagar CA & Student Haven",
            city = "Delhi",
            locality = "Laxmi Nagar Metro",
            type = "PG/Hostel",
            monthlyRent = 7800,
            deposit = 7800,
            bedrooms = 1,
            genderPreference = "Girls Only",
            amenities = listOf("Food Included", "Study Room", "CCTV", "Biometric Gate", "Filter Water"),
            imageUrl = "https://images.unsplash.com/photo-1540518614846-7eded433c457?auto=format&fit=crop&w=800&q=80",
            latitude = 28.6304,
            longitude = 77.2774,
            ownerName = "Sangeeta Roy",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 92,
            safetyScore = 91,
            description = "Budget-friendly student PG for girls 2 minutes walk from Laxmi Nagar Metro Station. Dedicated study room, female warden on-site, and healthy home-cooked meals.",
            distanceToMetroKm = 0.2,
            isSaved = false
        ),
        Property(
            id = "prop_8",
            title = "Knowledge Park Scholar Suites",
            city = "Noida",
            locality = "Knowledge Park II (Greater Noida)",
            type = "PG/Hostel",
            monthlyRent = 8500,
            deposit = 8500,
            bedrooms = 1,
            genderPreference = "Unisex",
            amenities = listOf("Shuttle Bus", "Wi-Fi", "Meal Plan", "Gym", "Gaming Lounge", "Security Guard"),
            imageUrl = "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?auto=format&fit=crop&w=800&q=80",
            latitude = 28.4600,
            longitude = 77.5000,
            ownerName = "Scholar Housing Pvt Ltd",
            ownerVerified = true,
            propertyVerified = true,
            trustScore = 97,
            safetyScore = 95,
            description = "Institutional college co-living complex near top Greater Noida universities. Free daily campus shuttle, 24/7 security booth, and high-speed Wi-Fi.",
            distanceToMetroKm = 0.7,
            isSaved = false
        )
    )

    private fun getSeedRoommates(): List<RoommateProfile> = listOf(
        RoommateProfile(
            id = "rm_1",
            name = "Rhea Kapoor",
            age = 22,
            occupation = "UI/UX Designer at Zomato",
            bio = "Early riser, loves clean minimalistic spaces and coffee brewing. Looking for a flatmate in Noida Sector 62 or Cyber City.",
            city = "Noida",
            maxBudget = 14000,
            sleepSchedule = "Early Bird",
            cleanliness = "Strict",
            foodPreference = "Vegetarian",
            smokingPreference = "Non-Smoker",
            studyWorkSchedule = "Hybrid (9 AM - 6 PM)",
            noisePreference = "Quiet",
            pets = "No Pets",
            gender = "Female",
            verifiedBadge = true,
            lifestyleTags = "Early Bird,Strict Cleanliness,Vegetarian,Non-Smoker,Quiet Study Zone,WFH Friendly,Fitness Enthusiast"
        ),
        RoommateProfile(
            id = "rm_2",
            name = "Aman Verma",
            age = 24,
            occupation = "Backend Developer at Microsoft",
            bio = "Tech enthusiast, likes quiet coding nights and gaming on weekends. Looking for a 2BHK companion near Cyber City Gurugram.",
            city = "Gurugram",
            maxBudget = 18000,
            sleepSchedule = "Night Owl",
            cleanliness = "Moderate",
            foodPreference = "Non-Veg",
            smokingPreference = "Non-Smoker",
            studyWorkSchedule = "Remote Work",
            noisePreference = "Moderate",
            pets = "Pet Friendly",
            gender = "Male",
            verifiedBadge = true,
            lifestyleTags = "Night Owl,Moderate Cleanliness,Non-Veg,Non-Smoker,Pet Friendly,WFH Friendly,Music Lover"
        ),
        RoommateProfile(
            id = "rm_3",
            name = "Sneha Chawla",
            age = 21,
            occupation = "MA Student at Delhi University",
            bio = "Friendly student in North Campus. Loves reading, tea, and keeping the apartment organized.",
            city = "Delhi",
            maxBudget = 10000,
            sleepSchedule = "Early Bird",
            cleanliness = "Strict",
            foodPreference = "Vegetarian",
            smokingPreference = "Non-Smoker",
            studyWorkSchedule = "Student Hours (8 AM - 4 PM)",
            noisePreference = "Quiet",
            pets = "No Pets",
            gender = "Female",
            verifiedBadge = true,
            lifestyleTags = "Early Bird,Strict Cleanliness,Vegetarian,Non-Smoker,Quiet Study Zone,Teetotaler"
        ),
        RoommateProfile(
            id = "rm_4",
            name = "Vikram Saxena",
            age = 25,
            occupation = "Financial Analyst at EY",
            bio = "Organized, non-intrusive professional. Spend weekdays at office, weekends exploring Delhi heritage spots.",
            city = "Delhi",
            maxBudget = 16000,
            sleepSchedule = "Early Bird",
            cleanliness = "Moderate",
            foodPreference = "Any",
            smokingPreference = "Non-Smoker",
            studyWorkSchedule = "Office Hours (9 AM - 7 PM)",
            noisePreference = "Quiet",
            pets = "No Pets",
            gender = "Male",
            verifiedBadge = true,
            lifestyleTags = "Early Bird,Moderate Cleanliness,Non-Smoker,Quiet Study Zone,Fitness Enthusiast"
        ),
        RoommateProfile(
            id = "rm_5",
            name = "Divya Nair",
            age = 23,
            occupation = "Product Manager at Swiggy",
            bio = "Cleanliness freak, loves cooking south-indian meals, early jogger. Looking for female flatmates in South Delhi or Noida Expressway.",
            city = "Noida",
            maxBudget = 16500,
            sleepSchedule = "Early Bird",
            cleanliness = "Strict",
            foodPreference = "Vegetarian",
            smokingPreference = "Non-Smoker",
            studyWorkSchedule = "Office Hours (9 AM - 6 PM)",
            noisePreference = "Quiet",
            pets = "No Pets",
            gender = "Female",
            verifiedBadge = true,
            lifestyleTags = "Early Bird,Strict Cleanliness,Vegetarian,Non-Smoker,Quiet Study Zone,WFH Friendly,Teetotaler"
        ),
        RoommateProfile(
            id = "rm_6",
            name = "Kabir Mehta",
            age = 26,
            occupation = "Data Scientist at Amazon",
            bio = "Respectful flatmate, loves high-speed Wi-Fi, weekend football, and keeping common areas tidy. Flexible with move-in dates.",
            city = "Gurugram",
            maxBudget = 20000,
            sleepSchedule = "Flexible",
            cleanliness = "Strict",
            foodPreference = "Non-Veg",
            smokingPreference = "Non-Smoker",
            studyWorkSchedule = "Hybrid (10 AM - 7 PM)",
            noisePreference = "Quiet",
            pets = "Pet Friendly",
            gender = "Male",
            verifiedBadge = true,
            lifestyleTags = "Flexible Hours,Strict Cleanliness,Non-Smoker,Pet Friendly,WFH Friendly,Fitness Enthusiast"
        )
    )
}
