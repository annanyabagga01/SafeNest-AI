package com.example.ui.screens.scam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HackathonDemoBadge
import com.example.ui.components.RiskBadge
import com.example.ui.components.SafeNestBottomNavBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DangerRed
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.SafeNestViewModel

@Composable
fun ScamCheckScreen(
    prefillDesc: String = "",
    viewModel: SafeNestViewModel,
    onNavigateToRoute: (String) -> Unit
) {
    val scamState by viewModel.scamUiState.collectAsState()

    LaunchedEffect(prefillDesc) {
        if (prefillDesc.isNotEmpty() && scamState.description.isEmpty()) {
            viewModel.updateScamInputs(desc = prefillDesc)
        }
    }

    Scaffold(
        topBar = { HackathonDemoBadge() },
        bottomBar = {
            SafeNestBottomNavBar(
                currentRoute = Screen.ScamCheck.route,
                onNavigate = onNavigateToRoute
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, BorderSubtle)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(TealPrimary, shape = RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("AI Rental Scam Detector", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                        Text("Powered by Gemini AI • Real-time Fraud Analysis", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Paste WhatsApp broker messages, rental descriptions, or token money demands to analyze fraud probability before transferring money.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Form Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Listing Details to Audit", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyDark)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = scamState.description,
                        onValueChange = { viewModel.updateScamInputs(desc = it) },
                        label = { Text("Property / Listing Description") },
                        placeholder = { Text("e.g. 1BHK in Cyber City for ₹6000. Owner says transfer ₹2000 token first.") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scam_desc_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = scamState.location,
                        onValueChange = { viewModel.updateScamInputs(location = it) },
                        label = { Text("Property Location / Locality") },
                        placeholder = { Text("e.g. DLF Cyber City, Sector 62 Noida, Hauz Khas Delhi") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scam_location_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = scamState.rent,
                            onValueChange = { viewModel.updateScamInputs(rent = it) },
                            label = { Text("Monthly Rent (₹)") },
                            placeholder = { Text("12000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("scam_rent_input")
                        )

                        OutlinedTextField(
                            value = scamState.deposit,
                            onValueChange = { viewModel.updateScamInputs(deposit = it) },
                            label = { Text("Deposit (₹)") },
                            placeholder = { Text("24000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("scam_deposit_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = scamState.brokerMessage,
                        onValueChange = { viewModel.updateScamInputs(brokerMsg = it) },
                        label = { Text("Broker Chat / WhatsApp Message") },
                        placeholder = { Text("Paste exact text sent by landlord or agent...") },
                        minLines = 2,
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scam_broker_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = scamState.suspiciousClaims,
                        onValueChange = { viewModel.updateScamInputs(claims = it) },
                        label = { Text("Suspicious Claims (Optional)") },
                        placeholder = { Text("e.g. Owner claims to be abroad, key by courier") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scam_claims_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preset Demo Quick Loaders for Hackathon testing
                    Text("TRY DEMO SCENARIOS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                viewModel.updateScamInputs(
                                    desc = "Luxury 2BHK Cyber City Gurugram",
                                    location = "DLF Cyber City, Gurugram",
                                    rent = "4500",
                                    deposit = "15000",
                                    brokerMsg = "Pay ₹5000 token money urgently on Google Pay to hold flat. Owner is Army Officer posted abroad, keys delivered by speed post.",
                                    claims = "No physical visit allowed prior to token transfer."
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("preset_scam_btn")
                        ) {
                            Text("Load High Scam", fontSize = 11.sp, color = DangerRed, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.updateScamInputs(
                                    desc = "MetroView PG Sector 62 Noida",
                                    location = "Sector 62, Noida",
                                    rent = "11000",
                                    deposit = "11000",
                                    brokerMsg = "Physical visit welcome at Sector 62. Aadhaar verification at reception required before booking.",
                                    claims = "Verified owner ID."
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("preset_safe_btn")
                        ) {
                            Text("Load Safe Listing", fontSize = 11.sp, color = SafeGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.runScamAnalysis() },
                        enabled = !scamState.isAnalyzing,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("scam_analyze_btn")
                    ) {
                        if (scamState.isAnalyzing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing with Gemini AI...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyze Fraud Risk Now", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error View if any
            if (scamState.error != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, DangerRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ReportProblem, contentDescription = null, tint = DangerRed)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = scamState.error.orEmpty(),
                            fontSize = 13.sp,
                            color = DangerRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Scam Result View
            val result = scamState.result
            if (result != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("AI Scam Audit Report", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                            RiskBadge(level = result.riskLevel, score = result.riskScore)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Risk summary text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BackgroundLight, RoundedCornerShape(10.dp))
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(text = result.summary, fontSize = 13.sp, color = NavyDark, lineHeight = 18.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Red Flags
                        if (result.redFlags.isNotEmpty()) {
                            Text("SCAM RED FLAGS DETECTED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DangerRed)
                            Spacer(modifier = Modifier.height(6.dp))
                            result.redFlags.forEach { flag ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Default.ReportProblem, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = flag, fontSize = 12.sp, color = TextPrimary, lineHeight = 17.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Positive Signals
                        if (result.positiveSignals.isNotEmpty()) {
                            Text("POSITIVE SAFETY SIGNALS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SafeGreen)
                            Spacer(modifier = Modifier.height(6.dp))
                            result.positiveSignals.forEach { sig ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = sig, fontSize = 12.sp, color = TextPrimary, lineHeight = 17.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Recommendations
                        Text("RECOMMENDED SAFE NEXT STEPS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        result.recommendations.forEach { rec ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = rec, fontSize = 12.sp, color = TextPrimary, lineHeight = 17.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // AI Safety Disclaimer Box (Mandatory Safety Rule)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(TealContainer, RoundedCornerShape(12.dp))
                    .border(1.dp, TealPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Safety Disclaimer: SafeNest AI fraud scores assist in identifying common real estate scams. Never transfer token money before inspecting property keys and verifying landlord ID in person.",
                        fontSize = 11.sp,
                        color = NavyDark,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
