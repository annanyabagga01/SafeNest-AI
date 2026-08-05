package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HackathonDemoBadge
import com.example.ui.components.PropertyCard
import com.example.ui.components.SafeNestBottomNavBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SafeNestViewModel

@Composable
fun ProfileScreen(
    viewModel: SafeNestViewModel,
    onNavigateToRoute: (String) -> Unit,
    onLogout: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val savedProperties by viewModel.savedProperties.collectAsState()
    val bookings by viewModel.allBookings.collectAsState()
    val scamReports by viewModel.scamReports.collectAsState()

    val profile = userProfile ?: com.example.data.model.UserProfile()

    Scaffold(
        topBar = { HackathonDemoBadge() },
        bottomBar = {
            SafeNestBottomNavBar(
                currentRoute = Screen.Profile.route,
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
            // Profile Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(TealContainer, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.name.take(2).uppercase(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = profile.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                if (profile.verifiedIdentity) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.VerifiedUser, contentDescription = "Verified", tint = SafeGreen, modifier = Modifier.size(18.dp))
                                }
                            }
                            Text(text = profile.email, fontSize = 12.sp, color = TextSecondary)
                            Text(text = "${profile.role} • ${profile.collegeOrCompany}", fontSize = 11.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BackgroundLight, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileMetric("Target City", profile.cityPreference)
                        ProfileMetric("Max Budget", "₹${profile.maxBudget}")
                        ProfileMetric("Sleep", profile.sleepSchedule)
                        ProfileMetric("Clean", profile.cleanliness)
                    }
                }
            }

            // Bookings Section
            if (bookings.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "YOUR ESCROW BOOKINGS (${bookings.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    bookings.forEach { booking ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = booking.propertyTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(text = booking.status.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Move-in: ${booking.moveInDate} • ${booking.durationMonths} Months lease", fontSize = 11.sp, color = TextSecondary)
                                Text(text = "Rent: ₹${booking.monthlyRent}/mo • Deposit: ₹${booking.depositAmount}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Saved Homes Section
            if (savedProperties.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "SAVED PROPERTIES (${savedProperties.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                    ) {
                        items(savedProperties) { prop ->
                            PropertyCard(
                                property = prop,
                                onCardClick = { onNavigateToRoute(Screen.PropertyDetail.createRoute(prop.id)) },
                                onSaveToggle = { viewModel.toggleSaveProperty(prop.id, prop.isSaved) },
                                modifier = Modifier.width(280.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Audit History Log
            if (scamReports.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "AI SCAM AUDIT HISTORY (${scamReports.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    scamReports.forEach { rep ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = rep.inputDescription.take(30) + "...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                    Text(text = rep.riskLevel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                                }
                                Text(text = rep.summary, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Logout Button
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedButton(
                    onClick = onLogout,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("profile_logout_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = NavyDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ProfileMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
    }
}
