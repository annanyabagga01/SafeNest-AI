package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.RiskLevel
import com.example.data.model.RoommateMatchResult
import com.example.data.model.RoommateProfile
import com.example.data.model.ScamAnalysisResult
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiScamService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeListing(
        description: String,
        rent: Int,
        deposit: Int,
        brokerMessage: String,
        suspiciousClaims: String
    ): ScamAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "null") {
            Log.d("GeminiScamService", "Using local smart heuristic analysis engine (no API key configured)")
            return@withContext performSmartHeuristicAnalysis(description, rent, deposit, brokerMessage, suspiciousClaims)
        }

        val prompt = """
            You are SafeNest AI, a real estate fraud & rental scam detector in India (Delhi, Gurugram, Noida).
            Analyze this property rental listing for potential scam indicators and return ONLY valid JSON.
            
            Listing Information:
            - Property Description: "$description"
            - Advertised Monthly Rent: ₹$rent
            - Requested Deposit: ₹$deposit
            - Broker/Owner Message: "$brokerMessage"
            - Suspicious Claims / Notes: "$suspiciousClaims"

            Return JSON with EXACTLY this structure:
            {
              "riskLevel": "LOW" | "MEDIUM" | "HIGH",
              "riskScore": number between 0 and 100,
              "redFlags": ["list of specific red flags detected"],
              "positiveSignals": ["list of positive safety signals"],
              "recommendations": ["list of practical safe next steps for tenant"],
              "summary": "Short 2-3 sentence clear summary of the risk assessment"
            }
        """.trimIndent()

        try {
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string()

            if (response.isSuccessful && !responseText.isNullOrEmpty()) {
                val jsonResp = JSONObject(responseText)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val content = candidates.getJSONObject(0).optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.getJSONObject(0)?.optString("text")

                    if (!text.isNullOrEmpty()) {
                        val startIdx = text.indexOf('{')
                        val endIdx = text.lastIndexOf('}')
                        val jsonString = if (startIdx != -1 && endIdx > startIdx) {
                            text.substring(startIdx, endIdx + 1)
                        } else {
                            text
                        }

                        val parsed = JSONObject(jsonString)
                        val levelStr = parsed.optString("riskLevel", "MEDIUM").uppercase()
                        val level = when (levelStr) {
                            "HIGH" -> RiskLevel.HIGH
                            "LOW" -> RiskLevel.LOW
                            else -> RiskLevel.MEDIUM
                        }

                        val redFlagsJson = parsed.optJSONArray("redFlags") ?: JSONArray()
                        val positiveJson = parsed.optJSONArray("positiveSignals") ?: JSONArray()
                        val recsJson = parsed.optJSONArray("recommendations") ?: JSONArray()

                        val redFlags = mutableListOf<String>()
                        for (i in 0 until redFlagsJson.length()) redFlags.add(redFlagsJson.getString(i))

                        val positiveSignals = mutableListOf<String>()
                        for (i in 0 until positiveJson.length()) positiveSignals.add(positiveJson.getString(i))

                        val recs = mutableListOf<String>()
                        for (i in 0 until recsJson.length()) recs.add(recsJson.getString(i))

                        return@withContext ScamAnalysisResult(
                            riskLevel = level,
                            riskScore = parsed.optInt("riskScore", 50),
                            redFlags = redFlags,
                            positiveSignals = positiveSignals,
                            recommendations = recs,
                            summary = parsed.optString("summary", "AI scam assessment completed.")
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiScamService", "Gemini API call error: ${e.message}")
        }

        // Fallback if API call fails or yields empty result
        performSmartHeuristicAnalysis(description, rent, deposit, brokerMessage, suspiciousClaims)
    }

    suspend fun generateRoommateAdvice(
        user: UserProfile,
        roommate: RoommateProfile,
        score: Int,
        positiveReasons: List<String>,
        differences: List<String>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "You both share complementary living styles! Focus on setting clear guidelines for common spaces and noise levels to maintain harmony."
        }

        val prompt = """
            Provide 2 friendly, constructive advice bullet points for two potential roommates in a shared apartment.
            Tenant 1: ${user.name}, ${user.sleepSchedule}, ${user.cleanliness} cleanliness, ${user.foodPreference} food.
            Tenant 2: ${roommate.name}, ${roommate.sleepSchedule}, ${roommate.cleanliness} cleanliness, ${roommate.foodPreference} food.
            Compatibility score: $score%.
            Matches: ${positiveReasons.joinToString(", ")}.
            Differences: ${differences.joinToString(", ")}.
            Keep response short and encouraging (under 60 words).
        """.trimIndent()

        try {
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }
            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string()
            if (response.isSuccessful && !responseText.isNullOrEmpty()) {
                val jsonResp = JSONObject(responseText)
                val text = jsonResp.optJSONArray("candidates")?.getJSONObject(0)
                    ?.optJSONObject("content")?.optJSONArray("parts")?.getJSONObject(0)?.optString("text")
                if (!text.isNullOrEmpty()) return@withContext text.orEmpty().trim()
            }
        } catch (e: Exception) {
            Log.e("GeminiScamService", "Roommate AI call error: ${e.message}")
        }

        "You both share complementary living styles! Set clear guidelines for common spaces and noise levels to maintain a smooth home environment."
    }

    private fun performSmartHeuristicAnalysis(
        description: String,
        rent: Int,
        deposit: Int,
        brokerMessage: String,
        suspiciousClaims: String
    ): ScamAnalysisResult {
        val textLower = "$description $brokerMessage $suspiciousClaims".lowercase()
        var score = 15 // Base baseline risk
        val redFlags = mutableListOf<String>()
        val positiveSignals = mutableListOf<String>()
        val recs = mutableListOf<String>()

        // Rule 1: Advance payment / Urgent token money
        if (textLower.contains("advance") || textLower.contains("token money") || textLower.contains("pay now") || textLower.contains("urgent") || textLower.contains("reserve fee")) {
            score += 40
            redFlags.add("Demand for immediate advance token/reservation money before physical property viewing")
        }

        // Rule 2: Unusually low rent for Prime Locality
        if (rent > 0 && rent < 6000) {
            score += 25
            redFlags.add("Advertised rent (₹$rent/month) is significantly below market rates for NCR localities")
        } else if (rent in 6000..30000) {
            positiveSignals.add("Monthly rent (₹$rent) aligns with expected local market rates")
        }

        // Rule 3: Deposit abnormally high (>3x rent)
        if (rent > 0 && deposit > rent * 3) {
            score += 20
            redFlags.add("Excessive security deposit requested (₹$deposit, over ${deposit / rent}x monthly rent)")
        } else if (deposit > 0) {
            positiveSignals.add("Security deposit is within standard 1-2 months rent range")
        }

        // Rule 4: Suspicious wording (army officer abroad, WhatsApp only, no visit allowed)
        if (textLower.contains("abroad") || textLower.contains("out of India") || textLower.contains("transfer") || textLower.contains("keys by courier") || textLower.contains("no visit")) {
            score += 35
            redFlags.add("Owner claims to be away/abroad and offers key delivery via courier only after payment")
        }

        if (textLower.contains("verified") || textLower.contains("owner id") || textLower.contains("physical inspection")) {
            positiveSignals.add("Listing specifies physical inspection or verified owner identity")
        }

        val riskLevel = when {
            score >= 65 -> RiskLevel.HIGH
            score >= 35 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        recs.add("Never transfer token money or deposit before physically viewing the property inside out.")
        recs.add("Verify landlord ownership documents and government-issued ID (Aadhaar/PAN) in person.")
        recs.add("Use SafeNest AI's verified escrow booking flow for safe transaction recording.")

        val summary = when (riskLevel) {
            RiskLevel.HIGH -> "HIGH SCAM RISK DETECTED ($score/100). The listing shows major fraud red flags such as advance money demands or suspicious claims."
            RiskLevel.MEDIUM -> "MODERATE RISK ($score/100). Exercise caution and insist on a physical walkthrough and owner ID verification prior to payment."
            RiskLevel.LOW -> "LOW RISK ($score/100). The listing appears standard, but standard safety precautions should still be followed."
        }

        return ScamAnalysisResult(
            riskLevel = riskLevel,
            riskScore = score.coerceIn(5, 95),
            redFlags = redFlags.ifEmpty { listOf("No critical red flags detected in provided text.") },
            positiveSignals = positiveSignals.ifEmpty { listOf("Standard listing formatting.") },
            recommendations = recs,
            summary = summary
        )
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
}
