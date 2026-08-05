package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RiskLevel {
    LOW, MEDIUM, HIGH
}

data class ScamAnalysisResult(
    val riskLevel: RiskLevel,
    val riskScore: Int, // 0 to 100
    val redFlags: List<String>,
    val positiveSignals: List<String>,
    val recommendations: List<String>,
    val summary: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "scam_reports")
data class ScamReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val inputDescription: String,
    val advertisedRent: Int,
    val depositAmount: Int,
    val brokerMessage: String,
    val suspiciousClaims: String,
    val riskLevel: String,
    val riskScore: Int,
    val summary: String,
    val redFlagsJson: String,
    val timestamp: Long = System.currentTimeMillis()
)
